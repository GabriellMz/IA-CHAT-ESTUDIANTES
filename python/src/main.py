from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from pymongo import MongoClient
from pyswip import Prolog
import datetime
import os

app = FastAPI(title="EVEA - Bloque 3")

# 1. Conexión a MongoDB
client = MongoClient("mongodb://localhost:27017/")
db = client["evea_db"]
historial_collection = db["historial_chats"]

# 2. Inicializar el motor de Prolog
prolog = Prolog()
ruta_prolog = os.path.abspath(os.path.join(os.path.dirname(__file__), "../../Prolog/reglas.pl"))
if os.path.exists(ruta_prolog):
    prolog.consult(ruta_prolog)

# MODELOS
class NotificationLogin(BaseModel):
    usuario_id: int
    token: str

class MensajeEstudiante(BaseModel):
    usuario_id: int
    mensaje: str

# ENDPOINTS

@app.post("/api/integracion/login-exitoso")
def preparar_entorno(data: NotificationLogin):
    historial = historial_collection.find_one({"usuario_id": data.usuario_id})
    if not historial:
        historial_collection.insert_one({
            "usuario_id": data.usuario_id,
            "historial_mensajes": [],
            "metadata_aprendizaje": {"total_interacciones": 0, "nivel_prioridad": "estándar"}
        })
    return {"status": "ready", "message": f"Contenedor NoSQL listo para usuario {data.usuario_id}"}

@app.get("/api/chat/historial/{usuario_id}")
def obtener_historial(usuario_id: int):
    historial = historial_collection.find_one({"usuario_id": usuario_id}, {"_id": 0})
    if not historial:
        raise HTTPException(status_code=404, detail="Historial no encontrado")
    return historial

@app.post("/api/chat/enviar")
def procesar_mensaje(data: MensajeEstudiante):
    nuevo_mensaje_alumno = {
        "remitente": "estudiante",
        "contenido": data.mensaje,
        "fecha": str(datetime.datetime.now())
    }
    historial_collection.update_one({"usuario_id": data.usuario_id}, {"$push": {"historial_mensajes": nuevo_mensaje_alumno}})
    
    # CONSULTA REAL AL MOTOR DE PROLOG
    try:
        consulta = list(prolog.query(f"apto_avanzar({data.usuario_id}, Resultado)"))
        if consulta:
            veredicto = str(consulta[0]["Resultado"])
            apto = True if "si" in veredicto.lower() else False
        else:
            veredicto = "No se encontraron registros lógicos en Prolog para este ID."
            apto = False
    except Exception:
        veredicto = "Sujeto a revisión de prerrequisitos (Reglas lógicas en construcción)."
        apto = True

    respuesta_sistema = f"Hola. Procesé tu estado académico en el motor lógico. Resultado: {veredicto}"
    
    # Guardar respuesta del sistema en MongoDB
    nuevo_mensaje_sistema = {
        "remitente": "sistema",
        "contenido": respuesta_sistema,
        "fecha": str(datetime.datetime.now()),
        "evaluacion_prolog": {
            "apto_para_avanzar": apto,
            "veredicto_original": veredicto
        }
    }
    historial_collection.update_one(
        {"usuario_id": data.usuario_id},
        {"$push": {"historial_mensajes": nuevo_mensaje_sistema}, "$inc": {"metadata_aprendizaje.total_interacciones": 1}}
    )
    
    return {"respuesta": respuesta_sistema, "apto": apto}
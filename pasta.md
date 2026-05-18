Bloque 1: Núcleo Transaccional, Seguridad y Lógica de Negocio (Scala + SQL) GABRIEL
Este bloque se convierte en el guardián del sistema. Scala, con su tipado fuerte y manejo seguro de concurrencia, es el candidato ideal para gestionar las transacciones críticas y la conexión a la base de datos relacional.

Tecnología de Persistencia: Base de datos SQL (ej. PostgreSQL o MySQL).

Nuevas Responsabilidades:

Gestionar el registro de nuevos usuarios (estudiantes, profesores, administradores).

Manejar el proceso de Login, validación de credenciales y generación de tokens de seguridad (como JWT).

Mantener el catálogo estricto de la malla curricular, cursos y calificaciones finales consolidadas.

Flujo de Datos: Cuando un usuario ingresa al EVEA, Scala consulta la base de datos SQL para validar su identidad. Si es correcto, Scala le devuelve al cliente un token de sesión y su usuario_id, el cual será la llave para conectar con el resto del sistema.






Bloque 2: Sistema Experto y Motor de Inferencia (Prolog) WATANZA
El "cerebro" lógico del EVEA se mantiene enfocado exclusivamente en la deducción académica, sin preocuparse por cómo o dónde se guardan los datos, ya que recibe todo el contexto necesario de los otros bloques.

Responsabilidades: Evaluar las reglas del negocio educativo, verificar prerrequisitos y determinar si un estudiante está apto para avanzar según su nivel actual.

Flujo de Datos: Recibe un "estado actual" del estudiante en formato crudo (inyectado a través de Python). Consulta sus archivos locales (hechos.pl y reglas.pl) y devuelve un veredicto lógico que luego será guardado en el historial.






Bloque 3: Gestión de Interacciones, Análisis y Puente de Datos (Python + NoSQL) CESAR IVAN
Este bloque asume ahora la carga pesada del tráfico en tiempo real. Python es excelente para manipular objetos JSON y diccionarios, lo que lo hace el compañero perfecto para tu base de datos documental.

Tecnología de Persistencia: Base de datos NoSQL (ej. MongoDB para persistencia a largo plazo y opcionalmente Redis para respuestas en caché).

Nuevas Responsabilidades:

Cargar el historial de chats previos de un estudiante al iniciar una sesión.

Guardar en tiempo real cada nuevo mensaje, interacción, y el resultado de las evaluaciones de Prolog dentro de un documento JSON flexible.

Ejecutar análisis de datos sobre el historial completo de chats para identificar patrones de aprendizaje o generar métricas.

Flujo de Datos: Cuando el usuario envía un mensaje de entrenamiento, la petición llega a Python. Python guarda inmediatamente la interacción en la base de datos NoSQL asociada al usuario_id (que Scala validó previamente), consulta a Prolog si es necesario, y guarda la respuesta generada antes de devolverla al usuario.






Bloque 4: Capa de Integración, I/O y Gobernanza (El Bus de Comunicación) FRANCIS
Este bloque asegura que el mundo estructurado (SQL) y el mundo no estructurado (NoSQL) se entiendan sin mezclarse de forma desordenada.

Responsabilidades: Centralizar los contratos de las APIs (cómo se llaman Scala y Python entre sí) y definir cómo se relacionan los datos de ambos mundos.

El Puente de Datos: La regla de oro aquí es que el usuario_id generado por SQL en el Bloque 1, es la clave principal que se usa en NoSQL en el Bloque 3. De esta forma, el perfil estructurado del alumno en SQL está vinculado de forma lógica a todo su historial dinámico de chats en NoSQL, sin necesidad de unir ambas bases de datos físicamente.

Documentación Clave: En la carpeta /integracion/, deberás documentar cómo Scala notifica a Python que un usuario ha iniciado sesión correctamente para que Python prepare su entorno de chat en NoSQL.




proyecto-software-multilenguaje/
│
├── docs/
│   ├── introduccion.md
│   ├── marco_teorico.md
│   ├── arquitectura.md
│   └── conclusiones.md
│
├── scala/
│   ├── build.sbt
│   └── src/
│       └── main/scala/
│           └── app/
│               ├── Main.scala
│               ├── servicios/
│               └── modelos/
│
├── prolog/
│   ├── hechos.pl
│   ├── reglas.pl
│   └── consultas.pl
│
├── python/
│   ├── requirements.txt
│   └── src/
│       ├── main.py
│       ├── integracion/
│       └── utils/
│
├── integracion/
│   ├── comunicacion_scala_python.md
│   └── comunicacion_python_prolog.md
│
├── data/
│   ├── entrada/
│   └── salida/
│
├── README.md
└── LICENSE
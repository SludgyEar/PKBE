# RemindMe

> RemindMe es una herramienta digital que te permite capturar y organizar información, ideas y conocimiento mediante notas y etiquetas que ayudan a identificar y relacionar temas.

> Enfocada en el uso individual, te permite almacenar material subjetivo, tareas, notas de reuniones, resúmenes de libros, estrófas de canciones, etc.

## Funcionalidades

- Registro e inicio de sesión seguro.
- Creación y almacenamiento de notas con etiquetas.
- Visualización de las notas pertenecientes a un usuario.

## Tecnologías

| FrontEnd  | BackEnd |
| :-------------: |:-------------:|
| HTML      | Java     |
| CSS     | Spring Boot     |
| JS      | PostgreSQL     |

## Requisitos

1. Java JDK 21: [Descargar](https://www.oracle.com/latam/java/technologies/downloads/#jdk21)
2. Docker Desktop [Descargar](https://www.docker.com/products/docker-desktop/)


## Instalación

1. Clonar el repositorio.

```
git clone https://github.com/SludgyEar/PKBE.git
```

2. Configurar base de datos PostgreSQL.
+ Navegar a src/main/docker y crear un archivo **.env**, donde se deben de colocar las credenciales de la base de datos.
##### *Recuerda las credenciales.*

```
POSTGRES_USER=admin
POSTGRES_PASSWORD=remindme
POSTGRES_DB=remindme_db
```
+ Levantar la base de datos.

```
docker-compose up -d
```

3. Configurar proyecto Spring Boot.
+ Navegar a src/main/resources y crear un archivo __application.properties__, donde se deben colocar las siguientes líneas de configuración.
##### *Recuerda adaptar el ejemplo con tus credenciales y cambiar el puerto en caso de usar otro.*

+ Conexión con la base de datos, (no cambiar el nombre de la aplicación).
```
spring.application.name=pkbe

spring.datasource.url=jdbc:postgresql://localhost:5432/remindme_db
spring.datasource.username=admin
spring.datasource.password=remindme
spring.datasource.driver-class-name=org.postgresql.Driver
```

+ Configuración de JPA / Hibernate.
```
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```
+ Activar Flyway.
```
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```
+ Seguridad JWT.
```
# Security
jwt.header=Authorization
jwt.secret=TUCLAVESUPERSECRETAYLARGAQUETIENEQUESERNOLAOLVIDESNILACOMPARTASPORESONOLAPONGOAQUI
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```
+ Bitácora (logs).
```
# Imprime información detallada durante el arranque de spring boot
spring.main.log-startup-info=true
# Imprime todo el proceso de seguridad, razones, rutas, procesos de autenticacion
logging.level.org.springframework.security=DEBUG
# Proporciona visibilidad del tráfico HTTP que llega a spring boot
logging.level.org.springframework.web=DEBUG
# Proporciona logs con informción mínima en la raíz de las clases
logging.level.rest.pkbe=INFO
# Proporciona logs verbosos en la capa de servicio
logging.level.rest.pkbe.domain.service=DEBUG

# Combinación para depuración de bases de datos. Cambiar a TRACE/DEBUG para depuraciones
logging.level.org.springframework.transaction=INFO
logging.level.org.springframework.orm.jpa=INFO
logging.level.org.hibernate.engine.transaction.internal=INFO

logging.file.name=logs/pkbe.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=7
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level [%logger{36}] - %msg%n
```

## Ejecución

+ Navegar a la raíz del proyecto y ejecutar el siguiente comando.

```
./mvnw spring-boot:run
```

Abre el proyecto en [localhost:8080](http://localhost:8080)

## Uso de RemindMe

+ Cree un usuario.
Para registrar un usuario deberá ingresar su nombre o nickname, un correo, una contraseña y confirmar esa contraseña.

![Pantalla_de_registro_usuario](/src/main/resources/static/images/register.png)

+ Inicie sesión.
Para iniciar sesión con su usuario deberá ingresar el correo y contraseña que fueron ingresados al registrarse.

![Pantalla_de_inicio_sesion](/src/main/resources/static/images/login.png)

+ Cree su primer nota.
Para crear una nota deberá ingresar un título, escribir el contenido de la nota usando o no formato *Markdown*, después deberá de colocarle una etiqueta para identificar su tema/topic, cuando haya terminado, haga clic en *Guardar nota*.

![Pantalla_creacion_notas](/src/main/resources/static/images/crear_nota.png)

+ Visualizar notas.
Para visualizar sus notas deberá de dar clic en el botón *Ver mis notas*, aquí se mostrarán todas las notas pertenecientes al usuario.

![Pantalla_tus_notas_sin_notas](/src/main/resources/static/images/pantalla_no_notas.png)
![Pantalla_tus_notas_con_notas](/src/main/resources/static/images/pantalla_notas.png)

+ Para cerrar sesión haga clic en el botón de *Cerrar sesión*.
# ktor-server
A basic "template" for a Kotlin/Ktor API server.

Please note that this is an experimental project.
I don't have much experience in Kotlin/Java, so
certain things may not be the most optimally done.

## Development (Kotlin)
- Requires at least JDK 16. You can download the [Latest JDK](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot). (Make sure to set the JAVA_HOME path variable).
- Clone the repository `git clone https://github.com/ricochhet/ktor-server.git`
- Open the root directory of the repository in Intellij. 
  - If you need to set the SDK: `File > Project Structure > Project SDK > Add SDK > JDK > /Path/To/JDK`
- Set up the `.env.example` by renaming it to `.env` and changing `SIGN_KEY_0` to your own key.
- Download and create a PostgreSQL server and database. [Download](https://www.postgresql.org/download/).
- Set up the `hikari.properties.example` by renaming it to `hikari.properties` and changing the values to reflect your postgres instance.
- Run `gradlew build` to install required dependencies and build the project.
- To run, use `gradlew run` or right-click the `Application.kt` if you're using Intellij.

## General API Testing
- Download [Postman](https://www.postman.com/downloads/) to test the API routes.
- Use [https://www.websocket.org/echo.html](https://www.websocket.org/echo.html) to test websockets.

## Documentation & Features

Each user has a roles object. Separate roles by a comma to have multiple. `USER,ADMIN`.
For a new user, the only role possible is `USER`. Anything else will be denied.

Currently, the only way to add different roles is through the database itself. Although,
in the future, a `MASTER` account will be able to assign roles via the API.

*All user data is encrypted with one-way SHA-256.*

### Basic example routes
  - `/customer`, `/customer/{id}` - `GET`
  - `/customer/${id}` - `DELETE`
  - `/customer` - `POST`
  - `/order`, `/order/${id}`, `/order/${id}/total` - `GET`


### Registration/Sign-up Routes
  - `/register` - `POST` - `roles` is optional, as it can only ever be `USER`.
  - `/register`, `/register/${id}` - `GET` - Requires: `ADMIN` role, `SessionUserAuthentication` cookie.
  - `/register/${id}` - `DELETE` - Requires: `ADMIN` role, `SessionUserAuthentication` cookie.

#### Example POST request:
  ```json
  {
    "email": "john@doe.com",
    "password": "password",
    "roles": "USER"
  }
  ```

### Login/Sign-in Routes
  - `/login` - `GET`
  - `/login` - `POST` - Requires: `BasicUserAuthentication` cookie (Automatically assigned).
  
### Session (Successful Sign-in) routes
  - `/session` - `GET` - Requires: `USER` role, `SessionUserAuthentication` cookie.



## References
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Ktor Docs](https://ktor.io/docs/welcome.html)
- [Ktor API Sample](https://github.com/ktorio/ktor-http-api-sample)
- [GraalVM](https://www.graalvm.org/)
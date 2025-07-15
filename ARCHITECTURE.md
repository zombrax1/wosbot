# Project Architecture

This project is a multi-module Maven build structured around several components:

- **wos-hmi** – JavaFX based UI for configuring profiles and viewing logs.
- **wos-utiles** – Utility library wrapping OpenCV and Tesseract operations.
- **wos-persitence** – Database access layer using Hibernate and SQLite.
- **wos-serv** – Core services containing automation tasks and schedulers.
- **wos-ot** – Shared DTOs and enumerations used across modules.

The parent `pom.xml` aggregates these modules and defines common properties such as the Java version and the JavaFX dependency management. Each module declares its own dependencies and inherits from the parent project version.

To build all modules run:

```sh
mvn clean package
```

The resulting executable JAR is produced in `wos-hmi/target` as `wos-bot-<version>.jar`.

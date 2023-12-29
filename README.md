## Sample skeleton project using Spring Boot 3 as backend and Vue as frontend.

### Technologies used with backend:
- Hibernate
- H2
- Flyway
- Lombok
- Spring Dev tools
- Gradle

---
### Additional notes
- Build project: `./gradlew clean build` (Linux) or `gradlew.bat clean build` (Windows)
- Run project: `./gradlew bootRun` (Linux) or `gradlew.bat bootRun` (Windows)
- Connect H2 database with browser: [`http://localhost:8080/h2-console`](http://localhost:8080/h2-console), jdbc url: `jdbc:h2:mem:test;MODE=PostgreSQL`
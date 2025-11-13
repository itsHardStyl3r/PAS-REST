# PAS-REST (backend)

<img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" height="32" width="32" alt="Spring logo" /> <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/mongodb/mongodb-original.svg" height="32" width="32" alt="MongoDB logo" />

Aplikacja REST API.

## Budowanie aplikacji

Bez uruchomionej bazy danych, aplikacja pominie wykonywanie testów. Warto zatem najpierw uruchomić kontener:

```bash
docker compose up -d
```

a następnie:

```bash
mvn install
```

### Stos technologiczny

- Java 21
- Spring Boot 3.5.7
- Docker

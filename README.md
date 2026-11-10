# Corporate Training Resource & Lab Infrastructure Management Platform

A Java Spring Boot + MySQL web application based on the assigned 7th-semester problem statement. Its central business engine is the **Training Infrastructure Allocation & Conflict Detection Engine**.

## Implemented scope

The application includes screens and database support for all 12 assigned modules:
1. Course Management
2. Batch Management
3. Trainer Management
4. Classroom Management
5. Computer Lab Management
6. Software Requirement Management
7. Resource Requirement Management
8. Resource Allocation Management
9. Conflict Detection Engine
10. Infrastructure Dashboard
11. Maintenance & Unavailability Management
12. Audit History

Additional support screens manage trainer assignments, course software requirements and lab software installations.

## Technology

- Java 21 (recommended; Java 23 can also run Spring Boot 3.5)
- Spring Boot 3.5.6
- Spring MVC + Thymeleaf
- Spring JDBC
- Spring Security
- MySQL 8
- Maven
- HTML, CSS and JavaScript

This is intentionally a straightforward MVC/JDBC implementation for a student project. It does not use a separate React frontend or JPA/Hibernate.

## Requirements

- JDK 21 or later
- Maven 3.9+
- MySQL Server 8
- VS Code (Extension Pack for Java and Spring Boot Extension Pack recommended)

## Database setup

1. Start MySQL Server.
2. Open MySQL Workbench or a MySQL terminal.
3. Run `src/main/resources/schema.sql`. The application also runs this schema at startup.
4. The JDBC URL creates the database automatically if the MySQL user has permission.

Default connection values:
- Database: `corporate_training`
- Username: `root`
- Password: empty by default

If your MySQL password is not empty, set environment variables before running:

Windows CMD:
```cmd
set DB_USERNAME=root
set DB_PASSWORD=your_mysql_password
```

Optional custom URL:
```cmd
set DB_URL=jdbc:mysql://localhost:3306/corporate_training?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Do not commit real database passwords to Git.

## Run in VS Code / terminal

Open this folder (the one containing `pom.xml`) in VS Code.

```bash
mvn clean spring-boot:run
```

Or build a runnable JAR:

```bash
mvn clean package
java -jar target/corporate-training-management-1.0.0.jar
```

Open: http://localhost:8080

## Demo login accounts

These are development-only in-memory accounts. They are not persisted in MySQL.

| Role | Username | Password |
|---|---|---|
| Administrator | `admin` | `Admin@123` |
| Training Manager | `manager` | `Manager@123` |
| Trainer | `trainer` | `Trainer@123` |

Change these before any real deployment. Passwords are BCrypt encoded in the configured in-memory user store.

## Main workflow

1. Sign in as `admin` or `manager`.
2. Review sample courses, batches, trainers, classrooms and labs.
3. Add or edit master records through the sidebar.
4. Add course-to-software and lab-to-software mappings.
5. Add a batch requirement with minimum capacity, systems and facility needs.
6. Open **Resource Allocation** and provide batch ID, resource type, resource ID, date and time.
7. The conflict engine checks:
   - time overlap for the selected resource;
   - capacity against batch size;
   - lab system count against the batch requirement;
   - software required by the course against software mapped to the lab;
   - projector and internet requirements;
   - scheduled maintenance;
   - optional trainer availability and overlapping trainer assignment.
8. A conflict returns HTTP 409 with reasons and records each reason in `conflict_records`. An accepted allocation is recorded in `allocations` and `audit_history`.

Sample data IDs are inserted by `schema.sql`. For example, batch `1`, lab `1`, trainer `1` are seeded. The seeded Java course has a software mapping to JDK, MySQL and VS Code, and lab 1 has those installations.

## Important implementation notes / limitations to review

This is a generated starter implementation, not a production-certified enterprise system. Before final submission:
- Review all SQL and test on your own MySQL installation.
- The trainer role currently has read access to module API data; the detailed trainer-only schedule view is a future refinement.
- Demo users are in-memory, not database-managed. The user/role administration UI and persistent account lifecycle need a later implementation if required by your guide.
- Generic CRUD screens are metadata-driven. The allocation engine is a dedicated service.
- Software compatibility currently validates course-to-software mappings against lab-to-software mappings. The free-text `software_ids` field on a batch requirement is retained for requirement documentation; map course software using the Course Software Requirements screen for engine checks.
- Allocation is currently recorded per resource and session date. Recurring multi-day batch allocation, reassignment workflows and automated utilization analytics can be extended.
- `performed_by` for generic CRUD audit rows is currently recorded as `system`; allocation records use the authenticated username.
- The current schema uses a generic resource ID for classroom/lab maintenance and allocations. IDs are interpreted together with `resource_type`.

## Project structure

```text
src/main/java/com/corporate/training
  config/       Security and module metadata
  controller/   Web pages, REST API and error handling
  service/      CRUD and allocation/conflict business logic
src/main/resources
  schema.sql
  templates/    Thymeleaf pages
  static/css/   UI styles
  static/js/    Browser-side module and allocation logic
docs/           Synopsis, requirements, ERD and test cases
```

## API overview

- `GET /api/modules` — list module definitions
- `GET /api/modules/{key}/meta` — field metadata
- `GET /api/modules/{key}` — list records
- `GET /api/modules/{key}/{id}` — get record
- `POST /api/modules/{key}` — create/update record
- `DELETE /api/modules/{key}/{id}` — delete record
- `GET /api/allocations` — list confirmed allocations
- `POST /api/allocations` — validate and create allocation

All API requests require login. Write operations require Administrator or Training Manager.

## Documentation

See `docs/` for:
- project synopsis;
- requirement analysis;
- database design and Mermaid ER diagram;
- test scenarios;
- the original assigned problem statement PDF.

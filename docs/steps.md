# File Upload API Roadmap

This roadmap outlines the planned structure, layers, endpoints, and tasks for the File Upload Management API.

-----
src/
 └─ main/
     ├─ java/
     │   └─ com/yourcompany/filestorageapi/
     │       ├─ FileStorageApiApplication.java   <-- main class
     │       ├─ config/                         <-- @Configuration beans
     │       ├─ controller/                     <-- @RestController beans
     │       ├─ service/                        <-- @Service beans
     │       ├─ repository/                     <-- @Repository beans
     │       ├─ model/                          <-- @Entity beans
     │       └─ dto/                            <-- optional request/response DTOs

---

## 1. Project Setup

* Initialize Spring Boot app
* Use Spring Boot 3.3.x, Java 17, Maven/Gradle
* Include Spring Web starter

## 2. Database

* Connect PostgreSQL to Spring Boot
* Configure `spring.datasource.url`, username/password
* Test DB connection
* Create dev DB `filestorageapi`

## 3. Model / Entity

* Create Metadata entity
* Fields: `fileId`, `userId` / `clientId`, `originalName`, `storagePath`, `uploadedAt`, `category`
* Annotate with JPA

## 4. Repository

* Create JPA repository for metadata CRUD operations

## 5. Service Layer

### File Storage Service

* Save files to `./dev_files/<API_CLIENT_ID>/<Category>/`
* Ensure directories exist
* Handle duplicate filenames and sanitize inputs

### Metadata Service

* Save metadata to DB
* Return file info after upload

## 6. Controller

### Upload Endpoint

* POST `/upload`
* Receives file, clientId, category
* Calls services
* Returns file info or error

### Download / Fetch Endpoint

* GET `/files/{clientId}/{category}/{fileId}`
* Streams file to client

### Delete Endpoint (optional)

* DELETE `/files/...`
* Removes file from storage and metadata

## 7. Security

* Implement JWT or token-based authentication
* Ensure client can only access their own files

## 8. Configuration

* Base path configurable via `storage.base-path` in `application.properties`
* Use different paths for dev vs prod environments

## 9. Exception Handling

* Handle file not found, DB errors, invalid requests
* Log upload/download events

## 10. Testing

* Unit and integration tests for DB save, file save, endpoints
* Test permissions and validations

## 11. Documentation

* Swagger/OpenAPI documentation for clients
* Describe endpoints, required headers, auth, response formats

## 12. Deployment

* Use Docker with Java runtime image
* Mount `./dev_files` as volume
* Keep container stateless
* No external Tomcat needed

## 13. Scaling / Future

* Optionally replace local `dev_files` with cloud storage (S3, MinIO) for multi-container production deployments

---

# Tips for Workflow

* Keep **service layer** separate from controllers for easier testing
* Store **relative paths** in DB for flexibility
* Always validate folders and filenames before saving
* Use Spring profiles (`dev`, `prod`) for environment-specific paths and DB credentials
* Ensure security against directory traversal (`../`) and unauthorized access

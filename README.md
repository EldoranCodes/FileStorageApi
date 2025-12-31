# File Storage API

A RESTful file storage service built with Spring Boot and PostgreSQL that allows external applications to upload, manage, and delete files through API key authentication.

## Overview

This application provides a centralized file storage solution where external applications can upload files without managing storage themselves. The service handles authentication, validation, storage, and metadata tracking.

### Key Features

- **API Key Authentication**: Secure access using hashed API keys
- **Batch Upload Support**: Upload single or multiple files in one request
- **File Management**: Upload, retrieve, and delete files and batches
- **Metadata Tracking**: Track file information in PostgreSQL database
- **Disk Storage**: Files stored on filesystem with UUID-based naming
- **User Isolation**: Each user (application) has isolated file storage

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.4.12
- **Language**: Java 17
- **Database**: PostgreSQL (with H2 for testing)
- **Build Tool**: Maven
- **Security**: BCrypt for API key hashing

### Application Structure

```
FileStorageApi/
├── src/main/java/com/nard/FileStorageApi/
│   ├── config/          # Configuration classes
│   │   ├── DataInitializer.java    # Demo data setup
│   │   ├── FileStorage.java        # File storage config
│   │   └── WebConfig.java         # Web/Interceptor config
│   ├── controller/      # REST Controllers
│   │   ├── UploadController.java   # File upload endpoint
│   │   ├── BatchController.java    # Batch operations
│   │   └── FileController.java     # File operations
│   ├── dao/            # Data Access Objects (Repositories)
│   ├── dto/            # Data Transfer Objects
│   ├── interceptor/    # API Key authentication interceptor
│   ├── model/          # JPA Entities
│   │   ├── Account.java
│   │   ├── User.java
│   │   ├── UploadBatch.java
│   │   └── StoredFile.java
│   └── service/        # Business Logic
│       ├── ApiKeyService.java
│       ├── UploadService.java
│       ├── BatchService.java
│       └── FileService.java
└── src/main/resources/
    └── application.properties
```

### Database Schema

#### `accounts`
- Stores account information
- Fields: `id`, `name`, `email`, `created_at`

#### `users` (applications)
- Represents external applications using the API
- Fields: `id`, `name`, `status` (ACTIVE/INACTIVE), `accounts_id`, `created_at`, `api_key` (hashed)
- Relationship: Many-to-One with `accounts`

#### `upload_batches`
- Groups multiple file uploads together
- Fields: `id`, `created_at`, `status` (PENDING/SUCCESS/FAILED), `users_id`
- Relationship: Many-to-One with `users`, One-to-Many with `stored_file`

#### `stored_file`
- Stores file metadata
- Fields: `id`, `original_name`, `uuid`, `upload_timestamp`, `batch_id`, `storage_path`
- Relationship: Many-to-One with `upload_batches`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (see [Database Setup](#database-setup) below)
- Docker (optional, for PostgreSQL)

### Database Setup

The application requires a PostgreSQL database. We provide a Docker-based setup for easy local development.

#### Option 1: Using Docker (Recommended)

Navigate to the `db` folder and follow the instructions:

```bash
cd db
chmod +x run.sh
./run.sh
```

This will:
- Pull the Bitnami PostgreSQL Docker image
- Create and start a PostgreSQL container
- Set up the database with the following defaults:
  - **Database**: `filestorage`
  - **User**: `nard`
  - **Password**: `mypassword`
  - **Port**: `5432`

For detailed instructions, see [db/README.md](db/README.md).

#### Option 2: Manual PostgreSQL Setup

1. Install PostgreSQL on your system
2. Create a database:
   ```sql
   CREATE DATABASE filestorage;
   CREATE USER nard WITH PASSWORD 'mypassword';
   GRANT ALL PRIVILEGES ON DATABASE filestorage TO nard;
   ```

### Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/filestorage
spring.datasource.username=nard
spring.datasource.password=mypassword
```

### Running the Application

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   Or using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

3. The application will start on `http://localhost:8080`

### Demo Data

On first startup, the application automatically creates:
- A demo account
- A demo user with name: `demo-app`
- API Key: `demo-api-key-12345` (check logs for the actual key)

**Important**: The API key is displayed in the console logs on first startup. Save it for testing!

## API Documentation

### Authentication

All endpoints require API key authentication via the `Authorization` header:

```
Authorization: ApiKey <your-api-key>
```

### Endpoints

#### 1. Upload Files

**POST** `/upload`

Upload single or multiple files.

**Request**:
- Headers: `Authorization: ApiKey <key>`
- Body: `multipart/form-data` with files named `files`

**Example using curl**:
```bash
curl -X POST http://localhost:8080/upload \
  -H "Authorization: ApiKey demo-api-key-12345" \
  -F "files=@/path/to/file1.txt" \
  -F "files=@/path/to/file2.txt"
```

**Response**:
```json
{
  "batchId": 1,
  "status": "SUCCESS",
  "files": [
    {
      "fileId": 1,
      "originalName": "file1.txt",
      "path": "users/demo-app/uuid-123",
      "uuid": "uuid-123",
      "uploadTimestamp": "2025-12-31T10:00:00"
    }
  ]
}
```

#### 2. Get Batch Files

**GET** `/files/{batchId}`

Retrieve all files in a batch.

**Request**:
- Headers: `Authorization: ApiKey <key>`
- Path Parameter: `batchId`

**Example**:
```bash
curl -X GET http://localhost:8080/files/1 \
  -H "Authorization: ApiKey demo-api-key-12345"
```

**Response**:
```json
{
  "batchId": 1,
  "status": "SUCCESS",
  "files": [
    {
      "fileId": 1,
      "originalName": "file1.txt",
      "path": "users/demo-app/uuid-123",
      "uuid": "uuid-123",
      "uploadTimestamp": "2025-12-31T10:00:00"
    }
  ]
}
```

#### 3. Delete Batch

**DELETE** `/delete/batch/{batchId}`

Delete a batch and all associated files.

**Request**:
- Headers: `Authorization: ApiKey <key>`
- Path Parameter: `batchId`

**Example**:
```bash
curl -X DELETE http://localhost:8080/delete/batch/1 \
  -H "Authorization: ApiKey demo-api-key-12345"
```

**Response**:
```json
{
  "success": true,
  "message": "Batch and all files deleted successfully"
}
```

#### 4. Delete Single File

**DELETE** `/delete/file/{fileId}`

Delete a single file.

**Request**:
- Headers: `Authorization: ApiKey <key>`
- Path Parameter: `fileId`

**Example**:
```bash
curl -X DELETE http://localhost:8080/delete/file/1 \
  -H "Authorization: ApiKey demo-api-key-12345"
```

**Response**:
```json
{
  "success": true,
  "message": "File deleted successfully"
}
```

### Error Responses

#### 401 Unauthorized
```json
{
  "error": "Invalid API key"
}
```

#### 403 Forbidden
Returned when trying to access another user's files.

#### 404 Not Found
Returned when batch or file doesn't exist.

## File Storage

Files are stored on the filesystem with the following structure:

```
{file.root-storage}/
└── users/
    └── {user-name}/
        └── {uuid}
```

Where:
- `{file.root-storage}` is configured in `application.properties` (default: `/home/nard/myProj/FileStorageApi/uploads`)
- `{user-name}` is the application name (e.g., "demo-app")
- `{uuid}` is a unique identifier for each file

## Testing

### Running Tests

```bash
mvn clean test
```

### Test Coverage

The project includes:
- **Unit Tests**: Service layer tests with mocked dependencies
- **Integration Tests**: Full REST controller tests with real database (H2 in-memory)

Test results: **34 tests, all passing**

### Test Configuration

Tests use H2 in-memory database (configured in `src/test/resources/application-test.properties`) to avoid requiring a running PostgreSQL instance.

## Project Structure

```
FileStorageApi/
├── db/                          # PostgreSQL Docker setup
│   ├── README.md               # Database setup instructions
│   └── run.sh                  # Docker setup script
├── src/
│   ├── main/
│   │   ├── java/               # Source code
│   │   └── resources/          # Configuration files
│   └── test/                   # Test code
├── uploads/                     # File storage directory (created on startup)
├── pom.xml                     # Maven dependencies
└── README.md                   # This file
```

## Configuration Properties

Key configuration options in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `file.root-storage` | Base directory for file storage | `/home/nard/myProj/FileStorageApi/uploads` |
| `file.upload-max-size` | Maximum file size | `50MB` |
| `spring.servlet.multipart.max-file-size` | Max file size per upload | `50MB` |
| `spring.jpa.hibernate.ddl-auto` | Database schema management | `update` |

## Security

- **API Keys**: Stored as BCrypt hashes in the database
- **Authentication**: Required for all endpoints via interceptor
- **Authorization**: Users can only access their own files/batches
- **File Naming**: UUID-based naming prevents path traversal attacks

## Development

### Adding New Users

Users are created through the database. To add a new user programmatically, you can:

1. Create an account
2. Create a user with a hashed API key
3. Set status to `ACTIVE`

Example (using BCrypt):
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedKey = encoder.encode("your-plain-api-key");
// Save to database
```

## Troubleshooting

### Application won't start

1. **Database connection error**: Ensure PostgreSQL is running and credentials are correct
2. **Port already in use**: Change `server.port` in `application.properties` or stop the conflicting service
3. **File storage directory**: Ensure the directory exists and is writable

### Tests failing

- Ensure you're using the test profile (automatically applied)
- Check that H2 dependency is in `pom.xml`
- Run `mvn clean test` to clear any cached issues

### File upload issues

- Check file size limits in `application.properties`
- Verify storage directory permissions
- Check application logs for detailed error messages

## Future Enhancements

- Multiple API keys per user / key rotation
- File quotas per user / batch
- File type and size validation
- Download endpoint
- Enhanced logging and auditing
- Admin endpoint to generate API keys
- File versioning
- Search and filtering capabilities

## License

This project is for educational purposes.

## Support

For issues or questions, check the logs or review the test files for usage examples.


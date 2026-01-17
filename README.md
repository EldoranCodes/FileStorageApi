# File Storage API
A RESTful API service for handling file uploads, storage, and retrieval. Designed for developers who need file attachment functionality in their applications without building their own file storage infrastructure.

## Our Goal

This API was built to solve a common problem: **developers always need file upload functionality, but building it from scratch is repetitive and time-consuming.**

**The Problem:**
- Every web application needs file uploads
- Each developer builds the same file storage logic
- Security, naming, organization - all repeated work

**Our Solution:**
- Centralized file storage API
- Developers only need to save one field: `storedName` (UUID)
- All file management handled by the API
- Secure, scalable, and easy to integrate

**Developer Workflow:**
1. Upload file → Get `storedName` UUID
2. Save `storedName` in your database (link to transaction/entity)
3. Retrieve file anytime using `storedName` + `apiKey`
4. No file system management needed!

**Value Proposition:**
- ✅ No file system code to write
- ✅ No security vulnerabilities to worry about
- ✅ No file naming conflicts
- ✅ No storage location decisions
- ✅ Just save UUID and call API

---
## Overview

This API provides a centralized file storage solution where developers (consumers) can:
- Upload files with automatic UUID-based naming
- Organize files by application workspace (`appName`)
- Retrieve files securely using API keys
- List and manage their uploaded files
- Soft delete files with cleanup capabilities

**Key Benefits:**
- No file system management needed
- Automatic file naming (UUID-based, no conflicts)
- Built-in security validation
- Workspace isolation per application
- Simple integration - just save the `storedName` UUID

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use included `mvnw` wrapper)
- **Postman** (or any HTTP client for testing)

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd FileStorageApi
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or if you have Maven installed:
   ```bash
   mvn spring-boot:run
   ```

3. **Verify the application is running**
   - API Base URL: `http://localhost:8100`
   - Health check: `http://localhost:8100/hello` (should return "Hello, World")

## Seed Data

On startup, the application automatically creates demo data:

- **Consumer (Developer):**
  - Name: `nard`
  - Status: `active`
  - Role: `admin`

- **API Key:**
  - API Key: `abc123`
  - App Name: `demoApp`
  - Owner: Consumer ID (auto-generated)

**Use this API key (`abc123`) for testing all endpoints.**

## H2 Database Console

The application uses H2 in-memory database for development. Access the console:

- **URL:** `http://localhost:8100/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (leave empty)

**Note:** Data is reset on application restart since it's in-memory.

## API Reference

### Base URL
```
http://localhost:8100
```

### Authentication
All file operations require an `apiKey` query parameter. Use the seed API key `abc123` for testing.

### Response Format

All endpoints return a standardized `ApiResponse`:

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful message",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

---

### 1. Upload File

Upload a file to the storage system.

**Endpoint:** `POST /upload/file`

**Query Parameters:**
- `apiKey` (required) - Your API key

**Form Data:**
- `file` (required) - The file to upload (multipart/form-data)

**Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "id": 1,
    "originalName": "document.pdf",
    "storedName": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf",
    "filePath": "/path/to/file",
    "createdAt": "2026-01-19T10:30:00",
    "owner": 1,
    "appName": "demoApp",
    "fileSize": 102400,
    "contentType": "application/pdf",
    "deletedAt": null
  }
}
```

**Important:** Save the `storedName` (UUID) in your database to reference this file later.

---

### 2. List Files

Get all files uploaded with your API key.

**Endpoint:** `GET /upload/files`

**Query Parameters:**
- `apiKey` (required) - Your API key

**Response:**
```json
{
  "success": true,
  "message": "Files retrieved successfully",
  "data": [
    {
      "id": 1,
      "originalName": "document.pdf",
      "storedName": "a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf",
      "contentType": "application/pdf",
      "fileSize": 102400,
      "createdAt": "2026-01-19T10:30:00",
      "streamUrl": "/upload/file?storedName=a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf"
    }
  ]
}
```

**Note:** The `streamUrl` is a template. You need to add `apiKey` parameter when calling it.

---

### 3. Stream/Download File

Retrieve a file by its stored name (UUID).

**Endpoint:** `GET /upload/file`

**Query Parameters:**
- `apiKey` (required) - Your API key
- `storedName` (required) - The UUID filename returned from upload

**Response:**
- Returns the file as a binary stream
- Content-Type header matches the file's MIME type
- Content-Disposition header includes original filename

**Status Codes:**
- `200 OK` - File retrieved successfully
- `401 Unauthorized` - Invalid API key
- `404 Not Found` - File not found or deleted

---

### 4. Soft Delete File

Mark a file as deleted (soft delete). File can be restored until cleanup runs.

**Endpoint:** `DELETE /upload/file`

**Query Parameters:**
- `apiKey` (required) - Your API key
- `storedName` (required) - The UUID filename to delete

**Response:**
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": null
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "File not found or invalid API key",
  "data": null
}
```

---

## Postman Collection Examples

### 1. Upload File

**Method:** `POST`  
**URL:** `http://localhost:8100/upload/file?apiKey=abc123`

**Body Tab:** `form-data`
- Key: `file` (Type: File)
- Value: Select a file from your computer

**Expected Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "id": 1,
    "originalName": "test.pdf",
    "storedName": "550e8400-e29b-41d4-a716-446655440000.pdf",
    ...
  }
}
```

**Save the `storedName` value for next requests!**

---

### 2. List Files

**Method:** `GET`  
**URL:** `http://localhost:8100/upload/files?apiKey=abc123`

**Expected Response:**
```json
{
  "success": true,
  "message": "Files retrieved successfully",
  "data": [
    {
      "id": 1,
      "originalName": "test.pdf",
      "storedName": "550e8400-e29b-41d4-a716-446655440000.pdf",
      "streamUrl": "/upload/file?storedName=550e8400-e29b-41d4-a716-446655440000.pdf"
    }
  ]
}
```

---

### 3. Download/Stream File

**Method:** `GET`  
**URL:** `http://localhost:8100/upload/file?apiKey=abc123&storedName=550e8400-e29b-41d4-a716-446655440000.pdf`

**Note:** Replace `550e8400-e29b-41d4-a716-446655440000.pdf` with the actual `storedName` from your upload response.

**Expected Response:**
- Binary file content
- Headers include Content-Type and original filename

**In Postman:**
- Click "Send and Download" to save the file
- Or view in "Preview" tab for images/PDFs

---

### 4. Delete File

**Method:** `DELETE`  
**URL:** `http://localhost:8100/upload/file?apiKey=abc123&storedName=550e8400-e29b-41d4-a716-446655440000.pdf`

**Expected Response:**
```json
{
  "success": true,
  "message": "File deleted successfully",
  "data": null
}
```

---

### 5. Test API Key Validation

**Method:** `GET`  
**URL:** `http://localhost:8100/apikeyTest`  
**Header:** `x-api-key: abc123`

**Expected Response:**
```json
{
  "success": true,
  "message": "Success validating apikey",
  "data": {
    "id": 1,
    "apiKey": "abc123",
    "appName": "demoApp",
    "owner": 1,
    ...
  }
}
```

---

## File Storage Structure

Files are organized in the following structure:

```
uploads/
  └── {appName}/
      └── MM-dd-yyyy/
          └── {uuid}.{extension}
```

**Example:**
```
uploads/
  └── demoApp/
      └── 01-19-2026/
          └── a1b2c3d4-e5f6-7890-abcd-ef1234567890.pdf
```

- Each `appName` has its own workspace
- Files are organized by upload date
- Files are stored with UUID names to prevent conflicts


## Error Handling

Common error scenarios:

**Invalid API Key:**
```json
{
  "success": false,
  "message": "Invalid Api Key!",
  "data": null
}
```

**File Not Found:**
```json
{
  "success": false,
  "message": "File not found or invalid API key",
  "data": null
}
```

**Invalid Filename:**
```json
{
  "success": false,
  "message": "File name Contains invalid characters",
  "data": null
}
```

**File Size Limit:**
- Maximum file size: 50MB (configurable in `application.properties`)

---

## Configuration

Key configuration in `application.properties`:

```properties
server.port=8100
file.upload-dir=uploads
file.upload-max-size=50MB
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

---

## Notes

- Files are stored locally in the `uploads/` directory
- Database is H2 in-memory (data resets on restart)
- API keys are workspace-scoped (one API key = one app workspace)
- Files are soft-deleted by default (can be restored until cleanup)
- All file operations require valid API key authentication

---

## License

[Your License Here]





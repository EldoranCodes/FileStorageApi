# Step 1: Setup Application
- Spring Boot project via initializer
- Dependencies: web, data-jpa, postgresql, spring-boot-starter-test
- Create entities: users, api_keys, upload_batches, stored_file
- Configure DB connection (PostgreSQL)
- Add application.properties / env variable for base upload path
- Test entities with spring-boot-test
- Add self-test bean (CommandLineRunner or @PostConstruct)
- Check base directory exists & writable
- Verify at least 1 demo user + API key

# Step 2: Build Services
- FileService / UploadService
- Validate API key → resolve user
- Create upload batch
- For each file:
- Generate UUID
- Save to disk (basePath/users/<user.name>/<uuid>)
- Create stored_file record
- Update batch status
- Return JSON response
- BatchService
- Fetch batch info + files
- Delete batch + associated files
- SingleFileService
- Fetch file info
- Delete single file

# Step 3: Build Controllers
- UploadController → /upload
- BatchController → /files/{batchId}, /delete/batch/{batchId}
- FileController → /delete/file/{fileId}

Example json response
{
  "batchId": 123,
  "status": "SUCCESS",
  "files": [
    { "fileId": 1, "originalName": "file.txt", "path": "users/demo/uuid" }
  ]
}


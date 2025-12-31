# 1. Start your application
mvn spring-boot:run

# 2. Upload a file
curl -X POST http://localhost:8080/upload \
  -H "Authorization: ApiKey demo-api-key-12345" \
  -F "files=@/path/to/your/file.txt"


curl -X POST http://localhost:8080/upload \
  -H "Authorization: ApiKey demo-api-key-12345" \
  -F "files=@wallhaven-zpyv7j_1920x1200.png" \
  -F "files=@testfileExcel.xls"

curl -X POST http://localhost:8080/upload \
  -H "Authorization: ApiKey demo-api-key-12345" \
  -F "~/Downloads/wallhaven-zpyv7j_1920x1200.png"

# 3. Get batch files (replace {batchId} with actual ID from upload response)
curl -X GET http://localhost:8080/files/{batchId} \
  -H "Authorization: ApiKey demo-api-key-12345"

# 4. Delete batch
curl -X DELETE http://localhost:8080/delete/batch/{batchId} \
  -H "Authorization: ApiKey demo-api-key-12345"

# 5. Delete single file
curl -X DELETE http://localhost:8080/delete/file/{fileId} \
  -H "Authorization: ApiKey demo-api-key-12345"

# Test invalid API key
curl -X GET http://localhost:8080/files/1 \
  -H "Authorization: ApiKey invalid-key"
# Should return 401 Unauthorized



--resoneses
{
    "batchId": 3,
    "status": "SUCCESS",
    "files": [
        {
            "fileId": 4,
            "originalName": "John_Leonard_Salinas_Resume.pdf",
            "path": "users/demo-app/8d411689-fdb9-4e5b-b432-b2959c2c9691",
            "uuid": "8d411689-fdb9-4e5b-b432-b2959c2c9691",
            "uploadTimestamp": "2025-12-31T11:51:15.191744525"
        },
        {
            "fileId": 5,
            "originalName": "7293.png",
            "path": "users/demo-app/56c67f2c-1794-4765-905e-da2ef7e6cdb6",
            "uuid": "56c67f2c-1794-4765-905e-da2ef7e6cdb6",
            "uploadTimestamp": "2025-12-31T11:51:15.194933966"
        },
        {
            "fileId": 6,
            "originalName": "TEST SCRIPT - 0074970 SAMG - DEV REMOVAL OF SPECIAL CHARACTER LIMITS ON REMARKS FIELD (EVALUATOR PROFILE)-4-2.xlsx",
            "path": "users/demo-app/739142f9-fc44-4451-9b15-f0da02045fb4",
            "uuid": "739142f9-fc44-4451-9b15-f0da02045fb4",
            "uploadTimestamp": "2025-12-31T11:51:15.198252580"
        }
    ]
}

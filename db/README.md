# PostgreSQL Local Setup with Docker (Bitnami)

This project provides a simple way to run a local PostgreSQL database using Docker and the Bitnami PostgreSQL image. The `run.sh` script pulls the image, creates a container, and starts the database.

---

## Prerequisites

1. **Docker**
   Make sure Docker is installed and running on your machine.

   * Install on Linux: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)
   * Install on Windows/macOS: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/)

2. **psql** (PostgreSQL client)
   Optional, for testing the connection from your terminal.

---

## Script Usage

1. **Make the script executable** (only needed once):

```bash
chmod +x run.sh
```

2. **Run the script**:

```bash
./run.sh
```

3. The script will:

* Pull the latest Bitnami PostgreSQL image
* Stop and remove any existing container with the same name
* Run a new PostgreSQL container
* Output the container name and connection info

### Note: volume will be initialized, if setting it up again might need to remove the same volume name

If you need to reset the database (e.g., to start fresh):

```bash
# Stop and remove the running container
docker stop filestorage-postgres
docker rm filestorage-postgres

# Remove the persistent volume (WARNING: This deletes all data!)
docker volume rm pg_data

# Rerun the script to re-initialize the database
./run.sh
```

**Warning**: Removing the volume will delete all database data. Use with caution!

---

## Default Environment Variables

The following variables are defined in `run.sh` and can be customized:

```bash
IMAGE_NAME="bitnami/postgresql:latest"  # Docker image
CONTAINER_NAME="filestorage-postgres"    # Name of the Docker container
POSTGRES_PASSWORD="mypassword"           # Password for the PostgreSQL user
POSTGRES_USER="nard"                     # PostgreSQL username
POSTGRES_DB="filestorage"                # Database name
HOST_PORT=5432                           # Port on localhost
```

**To connect to the database using psql:**

```bash
psql -h localhost -U <POSTGRES_USER> -d <POSTGRES_DB> -p <HOST_PORT>
```

Example using defaults:

```bash
psql -h localhost -U nard -d filestorage -p 5432
Password: mypassword
```

## Connecting the Spring Boot Application

The File Storage API is pre-configured to connect to this database. The connection settings in `src/main/resources/application.properties` match the defaults:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/filestorage
spring.datasource.username=nard
spring.datasource.password=mypassword
```

**Important**: If you change the database credentials in `run.sh`, make sure to update `application.properties` accordingly.

---

## Verifying the Connection

After starting the container, you can verify it's running:

```bash
# Check container status
docker ps | grep filestorage-postgres

# View container logs
docker logs filestorage-postgres

# Test connection with psql
psql -h localhost -U nard -d filestorage -p 5432
```

## Notes

* The container uses a persistent volume (`pg_data`) to store database data
* Data persists across container restarts
* If you change the `POSTGRES_USER`, `POSTGRES_PASSWORD`, or `POSTGRES_DB` in `run.sh`, make sure to update `src/main/resources/application.properties` in the main application
* The default configuration matches the Spring Boot application settings

## Troubleshooting

**Container won't start:**
- Check if port 5432 is already in use: `lsof -i :5432`
- Check Docker is running: `docker ps`

**Connection refused:**
- Ensure the container is running: `docker ps`
- Check the container logs: `docker logs filestorage-postgres`

**Permission denied:**
- Make sure the script is executable: `chmod +x run.sh`

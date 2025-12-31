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
# Stop and remove the running container
  docker stop local-postgres
  docker rm local-postgres

# Remove the persistent volume
  docker volume rm pg_data

# Rerun the script to re-initialize the database
  ./run.sh

---

## Default Environment Variables

The following variables are defined in `run.sh` and can be customized:

```bash
IMAGE_NAME="bitnami/postgresql:latest"  # Docker image
CONTAINER_NAME="local-postgres"         # Name of the Docker container
POSTGRES_PASSWORD="mysecretpassword"    # Password for the PostgreSQL user
POSTGRES_USER="user"                    # PostgreSQL username
POSTGRES_DB="mydb"                      # Database name
HOST_PORT=5432                           # Port on localhost
```

**To connect to the database using psql:**

```bash
psql -h localhost -U <POSTGRES_USER> -d <POSTGRES_DB> -p <HOST_PORT>
```

Example using defaults:

```bash
psql -h localhost -U user -d mydb -p 5432
Password: mysecretpassword
```

---

## Notes

* If you change the `POSTGRES_USER`, `POSTGRES_PASSWORD`, or `POSTGRES_DB`, make sure to update your application connection settings accordingly.
* For persistence across container restarts, you can add a Docker volume in `run.sh`:

```bash
-v pg_data:/bitnami/postgresql
```

---

This README keeps it simple and ready for any developer on your team.

If you want, I can **also include an example section showing how to connect your Java Spring Boot project to this local DB**—that makes it plug-and-play. Do you want me to add that?

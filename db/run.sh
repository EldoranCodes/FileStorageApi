#!/bin/bash

# Configuration
IMAGE_NAME="bitnami/postgresql:latest"
CONTAINER_NAME="filestorage-postgres"
POSTGRES_PASSWORD="mypassword"
POSTGRES_USER="nard"
POSTGRES_DB="filestorage"
HOST_PORT=5432
VOLUME_NAME="pg_data" # Docker volume for persistence

# Pull the latest Bitnami PostgreSQL image
echo "Pulling Docker image $IMAGE_NAME..."
docker pull $IMAGE_NAME

# Check if container already exists
if [ "$(docker ps -a -q -f name=$CONTAINER_NAME)" ]; then
  echo "Container $CONTAINER_NAME already exists. Stopping and removing it..."
  docker stop $CONTAINER_NAME
  docker rm $CONTAINER_NAME
fi

# Create volume if it doesn't exist
if [ -z "$(docker volume ls -q -f name=$VOLUME_NAME)" ]; then
  echo "Creating Docker volume $VOLUME_NAME..."
  docker volume create $VOLUME_NAME
fi

# Run PostgreSQL container with persistent volume
echo "Running PostgreSQL container..."
docker run -d \
  --name $CONTAINER_NAME \
  -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
  -e POSTGRES_USER=$POSTGRES_USER \
  -e POSTGRES_DB=$POSTGRES_DB \
  -p $HOST_PORT:5432 \
  -v $VOLUME_NAME:/bitnami/postgresql \
  $IMAGE_NAME

# Output the container name and connection info
echo "PostgreSQL container is running as '$CONTAINER_NAME'"
echo "Connect using: psql -h localhost -U $POSTGRES_USER -d $POSTGRES_DB -p $HOST_PORT"
echo "Data is persisted in Docker volume: $VOLUME_NAME"

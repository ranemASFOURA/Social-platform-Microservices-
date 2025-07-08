#!/bin/bash
echo "Starting all infrastructure services..."

docker compose -f redis/docker-compose.yml up -d
docker compose -f nginx/docker-compose.yml up -d
docker compose -f mysql-post/docker-compose.yml up -d
docker compose -f mongo-follow/docker-compose.yml up -d
docker compose -f mongo-cluster/docker-compose.yml up -d
docker compose -f MinIO/docker-compose.yml up -d
docker compose -f kafka/docker-compose.yml up -d
docker compose -f elasticsearch/docker-compose.yml up -d

echo "All services started."

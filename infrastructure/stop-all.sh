#!/bin/bash
echo "Stopping all infrastructure services..."

docker compose -f elk-docker/docker-compose.yml stop
docker compose -f kafka/docker-compose.yml stop
docker compose -f MinIO/docker-compose.yml stop
docker compose -f mongo-cluster/docker-compose.yml stop
docker compose -f mongo-follow/docker-compose.yml stop
docker compose -f mysql-post/docker-compose.yml stop
docker compose -f nginx/docker-compose.yml stop
docker compose -f redis/docker-compose.yml stop

echo "All services stopped."

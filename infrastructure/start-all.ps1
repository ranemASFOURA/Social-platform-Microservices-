Write-Host "Starting Mongo Cluster..."
cd .\mongo-cluster
docker-compose up -d
Start-Sleep -Seconds 10

Write-Host "Starting Kafka..."
cd ..\kafka
docker-compose up -d
Start-Sleep -Seconds 10

Write-Host "Starting MinIO..."
cd ..\MinIO
docker-compose up -d
Start-Sleep -Seconds 5

Write-Host "Starting ELK Stack..."
cd ..\elk-docker
docker-compose up -d
Start-Sleep -Seconds 10

Write-Host "All services started successfully."

#!/bin/bash

cd "$(dirname "$0")"



podman-compose up -d

echo "All services started"
echo "Frontend: http://localhost"
echo "MinIO Console: http://localhost:9001"
echo "MinIO API: http://localhost:9000"

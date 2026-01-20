#!/bin/bash

set -e  # Exit on error

echo "========================================"
echo "Building all modules without parent POM"
echo "========================================"
echo

# Step 1: Build contracts first (they are dependencies for services)
echo "[1/2] Building contracts..."
echo

cd events-contract-restaurant
./mvnw clean install -DskipTests
cd ..

cd api-contract-restaurant
./mvnw clean install -DskipTests
cd ..

echo
echo "Contracts built successfully!"
echo

# Step 2: Build all services in parallel
echo "[2/2] Building services in parallel..."
echo

(cd analytics-service-restaurant && ./mvnw clean package -DskipTests && echo "Analytics-service built successfully!") &
PID1=$!

(cd audit-service-restaurant && ./mvnw clean package -DskipTests && echo "Audit-service built successfully!") &
PID2=$!

(cd notification-service-restaurant && ./mvnw clean package -DskipTests && echo "Notification-service built successfully!") &
PID3=$!

(cd rest-restaurant && ./mvnw clean package -DskipTests && echo "Rest-restaurant built successfully!") &
PID4=$!

# Wait for all background processes
wait $PID1
wait $PID2
wait $PID3
wait $PID4

echo
echo "========================================"
echo "All modules built successfully!"
echo "========================================"




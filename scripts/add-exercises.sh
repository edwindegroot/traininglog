#!/bin/bash

curl -X POST http://localhost:8080/api/exercises   -H "Content-Type: application/json"   -d '{"name":"Squat"}'
curl -X POST http://localhost:8080/api/exercises   -H "Content-Type: application/json"   -d '{"name":"Bench press"}'
curl -X POST http://localhost:8080/api/exercises   -H "Content-Type: application/json"   -d '{"name":"Deadlift"}'

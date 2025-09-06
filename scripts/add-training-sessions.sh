#!/bin/bash

curl -X POST http://localhost:8080/api/training-sessions   -H "Content-Type: application/json"   -d '{"notes":"ok", "username":"edwin"}'
curl -X POST http://localhost:8080/api/training-sessions   -H "Content-Type: application/json"   -d '{"notes":"some other notes", "username":"edwin"}'
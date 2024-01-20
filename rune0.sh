#!/bin/sh
trap "lsof -ti tcp:8090 -sTCP:LISTEN | xargs kill" SIGINT
docker-compose -f docker-compose.kafka.yml up -d --build \
&& gradle :kafkatool-restapi:run | sed -e 's/^/[REST-API] /' & gradle :kafkatool-web:run | sed -e 's/^/[WEB-APP] /'

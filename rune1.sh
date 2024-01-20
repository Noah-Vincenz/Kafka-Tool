#!/bin/sh
trap "lsof -ti tcp:8090 -sTCP:LISTEN | xargs kill" SIGINT
export ENV=E1
gradle :kafkatool-restapi:run | sed -e 's/^/[REST-API] /' & gradle :kafkatool-web:run | sed -e 's/^/[WEB-APP] /'

#!/bin/bash
#
# Start script for alphabetical-company-search-consumer

PORT=8080

exec java -jar -Dserver.port="${PORT}" "alphabetical-company-search-consumer.jar"
#!/bin/bash

if [ -n "$SPRING_DATASOURCE_URL" ]; then
  if [ "${SPRING_DATASOURCE_URL#jdbc:}" = "$SPRING_DATASOURCE_URL" ]; then
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
  fi
  echo "Using SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL%%@*}@****" >&2
elif [ -n "$DB_HOST" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_HOST:${DB_PORT:-5432}/$DB_NAME"
  echo "Constructed SPRING_DATASOURCE_URL from components" >&2
fi

if [ -z "$SPRING_DATASOURCE_URL" ]; then
  echo "ERROR: SPRING_DATASOURCE_URL not set and insufficient components provided" >&2
  exit 1
fi

exec java -jar app.jar --spring.profiles.active=prod

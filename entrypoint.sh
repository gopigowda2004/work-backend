#!/bin/bash

if [ ! -z "$SPRING_DATASOURCE_URL" ]; then
  if [[ ! "$SPRING_DATASOURCE_URL" =~ ^jdbc: ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
  fi
elif [ ! -z "$DB_HOST" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_HOST:${DB_PORT:-5432}/$DB_NAME"
fi

exec java -jar app.jar --spring.profiles.active=prod

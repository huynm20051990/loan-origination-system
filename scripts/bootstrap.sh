#!/bin/bash

# Inside the container, these are the hostnames (service names)
DB_USER="home-user-prod"
export PGPASSWORD=$POSTGRES_PWD

echo "-------------------------------------------------------"
echo "🚀 Starting Environment Bootstrap (Internal Network)"
echo "-------------------------------------------------------"

inject_sql() {
    local host=$1
    local user=$2
    local db=$3
    local folder_name=$4

    echo "🔍 Checking database connectivity: $host"

    # Wait for the DB using psql/pg_isready over the network
    until pg_isready -h "$host" -U "$user" -d "$db" > /dev/null 2>&1; do
      echo "   ... waiting for $host ($db) to be ready"
      sleep 2
    done

    for f in /database/"$folder_name"/*.sql; do
        if [ -f "$f" ]; then
            echo "   ✅ Injecting $(basename "$f") into $host"
            psql -h "$host" -U "$user" -d "$db" < "$f"
        fi
    done
}

# 1. Inject SQL into all 4 databases using service hostnames
inject_sql "postgres" "$DB_USER" "home-db" "init-home"
inject_sql "app-db" "$DB_USER" "app-db" "init-app"
inject_sql "credit-db" "$DB_USER" "credit-db" "init-credit"
inject_sql "notify-db" "$DB_USER" "notification-db" "init-notify"

echo "-------------------------------------------------------"
echo "📡 Registering Kafka Connectors"
echo "-------------------------------------------------------"

# 2. Call the registration script
if [ -f "/scripts/register-connectors.sh" ]; then
    /bin/bash /scripts/register-connectors.sh
else
    echo "⚠️  register-connectors.sh not found!"
fi
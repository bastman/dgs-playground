
#!/usr/bin/env sh

echo "======= create db and (user) roles ======="

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE ROLE app_rw WITH LOGIN PASSWORD 'app_rw';
    CREATE DATABASE app OWNER app_rw;
    CREATE DATABASE app_test OWNER app_rw;
    GRANT ALL ON DATABASE app TO app_rw;
    GRANT ALL ON DATABASE app_test TO app_rw;
EOSQL

#! /bin/bash
echo "creating db named ... "$USER"_DB"
createdb -h localhost -p $PGPORT $USER"_DB"
pg_ctl status

echo "Copying csv files ... "
sleep 1
cp ../data/*.csv /tmp/$USER/myDB/data/.

echo "Initializing tables .. "
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < ../sql/create.sql

echo "Initializing triggers .. "
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < ../sql/triggers.sql

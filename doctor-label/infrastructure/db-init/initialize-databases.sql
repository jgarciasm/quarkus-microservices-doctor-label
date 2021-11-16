CREATE ROLE admin WITH LOGIN PASSWORD 'admin';

CREATE ROLE label WITH LOGIN PASSWORD 'label' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
CREATE DATABASE labels_database;
GRANT ALL PRIVILEGES ON DATABASE labels_database TO label ;
GRANT ALL PRIVILEGES ON DATABASE labels_database TO admin;

CREATE ROLE doctorlabelling WITH LOGIN PASSWORD 'doctorlabelling' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
CREATE DATABASE doctorlabelling_database;
GRANT ALL PRIVILEGES ON DATABASE doctorlabelling_database TO doctorlabelling;
GRANT ALL PRIVILEGES ON DATABASE doctorlabelling_database TO admin;


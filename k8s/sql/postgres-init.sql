-- k8s/sql/postgres-init.sql

-- Crear usuario de aplicación (no superusuario)
CREATE USER castor_user WITH PASSWORD 'Castor2025*' CREATEDB;

-- Crear la base de datos propiedad del usuario
CREATE DATABASE castor_customers OWNER castor_user;

-- Conectarse a la base
\c castor_customers;

-- Revocar el acceso al schema public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO castor_user;

-- Crear tabla de clientes bajo su propiedad
CREATE TABLE public.customers (
	id uuid PRIMARY KEY,
	active boolean,
	email varchar(255),
	name varchar(255)
);

ALTER TABLE public.customers OWNER TO castor_user;

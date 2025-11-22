-- ============================================
-- POSTGRESQL DATABASE SETUP SCRIPT
-- ============================================

-- Limpiar conexiones activas a la base de datos
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'castor_customers';

-- Eliminar base de datos y usuario existentes
DROP DATABASE IF EXISTS castor_customers;
DROP ROLE IF EXISTS castor_user;

-- ============================================
-- CREACIÓN DE USUARIO Y BASE DE DATOS
-- ============================================

-- Crear usuario de aplicación
CREATE USER castor_user WITH PASSWORD 'Castor2025' CREATEDB;

-- Crear base de datos
CREATE DATABASE castor_customers OWNER castor_user;

-- ============================================
-- CONFIGURACIÓN DE ESQUEMA Y PERMISOS
-- ============================================

-- Conectarse a la nueva base de datos
\c castor_customers;

-- Configurar esquema public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO castor_user;
ALTER SCHEMA public OWNER TO castor_user;

-- ============================================
-- CREACIÓN DE TABLAS
-- ============================================

-- Tabla de clientes
CREATE TABLE public.customers (
    id uuid PRIMARY KEY,
    active boolean,
    email varchar(255),
    name varchar(255)
);

-- ============================================
-- CONFIGURACIÓN DE PERMISOS FINALES
-- ============================================

-- Asignar ownership y permisos
ALTER TABLE public.customers OWNER TO castor_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.customers TO castor_user;

-- Mensaje de confirmación
\echo '✅ PostgreSQL setup completado:'
\echo '   - Usuario castor_user creado'
\echo '   - Base de datos castor_customers creada'
\echo '   - Tabla customers creada y configurada'
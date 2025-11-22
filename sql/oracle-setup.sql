-- ============================================
-- ORACLE DATABASE SETUP SCRIPT
-- ============================================

-- Cambiar al PDB correcto (XEPDB1)
ALTER SESSION SET CONTAINER = xepdb1;

-- ============================================
-- CREACIÓN DE USUARIO Y PERMISOS
-- ============================================

-- Eliminar usuario existente (si aplica)
BEGIN
    EXECUTE IMMEDIATE 'DROP USER CASTOR_BILLING CASCADE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1918 THEN  -- ORA-01918: user does not exist
            RAISE;
        END IF;
END;
/

-- Crear usuario de aplicación
CREATE USER CASTOR_BILLING IDENTIFIED BY "Castor2025" 
QUOTA UNLIMITED ON USERS;

-- Otorgar permisos necesarios
GRANT CREATE SESSION,
       CREATE TABLE,
       CREATE SEQUENCE,
       CREATE TRIGGER,
       CREATE VIEW,
       CREATE PROCEDURE,
       UNLIMITED TABLESPACE
TO CASTOR_BILLING;

-- ============================================
-- CREACIÓN DE TABLAS
-- ============================================

-- Tabla principal de facturas
CREATE TABLE CASTOR_BILLING.INVOICES (
    ID RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    CUSTOMER_ID RAW(16) NOT NULL,
    SUBTOTAL NUMBER(18,2) NOT NULL,
    TAX NUMBER(18,2) NOT NULL,
    DISCOUNT NUMBER(18,2) NOT NULL,
    TOTAL NUMBER(18,2) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS
-- ============================================

-- Procedimiento para validar existencia de cliente
CREATE OR REPLACE PROCEDURE CASTOR_BILLING.VALIDATE_CUSTOMER_EXISTS (
    p_customer_id IN RAW
)
AS
BEGIN
    IF p_customer_id IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'El cliente es obligatorio');
    END IF;
END VALIDATE_CUSTOMER_EXISTS;
/

-- ============================================
-- CONFIRMACIÓN FINAL
-- ============================================

-- Mensaje de éxito
BEGIN
    DBMS_OUTPUT.PUT_LINE('✅ Oracle setup completado:');
    DBMS_OUTPUT.PUT_LINE('   - Usuario CASTOR_BILLING creado');
    DBMS_OUTPUT.PUT_LINE('   - Tabla INVOICES creada');
    DBMS_OUTPUT.PUT_LINE('   - Procedimiento VALIDATE_CUSTOMER_EXISTS creado');
END;
/
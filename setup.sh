#!/bin/bash

echo "============================================"
echo "   🧹🛑 LIMPIANDO TODO ANTES DE INICIAR      "
echo "============================================"

echo "🔸 Deteniendo Minikube (si estaba corriendo)..."
minikube stop >/dev/null 2>&1

echo "🔸 Eliminando TODO el cluster de Minikube..."
minikube delete --all --purge >/dev/null 2>&1

echo "🔸 Limpiando variables de entorno de Docker..."
eval "$(minikube docker-env -u 2>/dev/null)" >/dev/null 2>&1

echo "🔸 Eliminando PV y PVC (si hubiera alguno colgado)..."
kubectl delete pvc --all >/dev/null 2>&1
kubectl delete pv --all  >/dev/null 2>&1

echo "🔸 Eliminando volúmenes residuales del hostpath en Minikube..."
minikube ssh "sudo rm -rf /var/lib/minikube/hostpath-provisioner/*" >/dev/null 2>&1

echo "🔸 Eliminando carpeta persistente de Oracle si existía..."
minikube ssh "sudo rm -rf /mnt/data/oracle" >/dev/null 2>&1

echo "🔸 (Opcional) Limpiando /var/lib/minikube/data si existe..."
minikube ssh "sudo rm -rf /var/lib/minikube/data" >/dev/null 2>&1

echo "✔️ Sistema limpio (cluster, PVs, PVCs, hostpath, oracle-dir)."
echo ""

echo "============================================"
echo "   🚀 CREANDO NUEVO CLUSTER MINIKUBE        "
echo "============================================"

minikube start --cpus=6 --memory=12288 --addons=default-storageclass,storage-provisioner
echo "✔️ Minikube iniciado."
echo ""

echo "============================================"
echo "   🟦 PREPARANDO ALMACENAMIENTO ORACLE      "
echo "============================================"

minikube ssh "sudo mkdir -p /mnt/data/oracle && sudo chmod 777 /mnt/data/oracle"
echo "✔️ Carpeta creada (/mnt/data/oracle)."
echo ""

echo "============================================"
echo "   🐳 CONSTRUYENDO IMÁGENES SIN CACHÉ       "
echo "============================================"

echo "🔄 Enlazando Docker interno de Minikube..."
eval "$(minikube docker-env)"

echo "📦 Construyendo billing-service..."
docker build --no-cache -t billing-service:latest ./billing-service

echo "📦 Construyendo invoice-calculator..."
docker build --no-cache -t invoice-calculator:latest ./python-ms

echo "✔️ Imágenes construidas."
echo ""

echo "============================================"
echo "     📦 APLICANDO STORAGE (PV / PVC)        "
echo "============================================"

kubectl apply -f k8s/storageclass-oracle.yaml
kubectl apply -f k8s/pv-oracle.yaml
kubectl apply -f k8s/persistent-volume-claims.yaml

echo "✔️ Storage configurado."
echo ""

echo "============================================"
echo "      🔑 CONFIGMAPS Y SECRETS               "
echo "============================================"

kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml

echo "✔️ ConfigMaps y Secrets listos."
echo ""

echo "============================================"
echo "         📡 DESPLEGANDO DEPLOYMENTS         "
echo "============================================"

kubectl apply -f k8s/deployments.yaml
kubectl apply -f k8s/services.yaml

echo "✔️ Deployments y Services aplicados."
echo ""

echo "============================================"
echo "     ⏳ ESPERANDO A QUE POSTGRES SUBA       "
echo "============================================"

kubectl wait --for=condition=ready pod -l app=postgresql --timeout=240s

echo "============================================"
echo "     ⏳ ESPERANDO A QUE ORACLE SUBA         "
echo "============================================"

kubectl wait --for=condition=ready pod -l app=oracle-db --timeout=240s

echo "============================================"
echo " ⏳ ESPERANDO MENSAJE REAL DE 'READY' ORACLE"
echo "============================================"

ORACLE_POD=$(kubectl get pod -l app=oracle-db -o jsonpath='{.items[0].metadata.name}')

attempt=1
max_attempts=60

while [ $attempt -le $max_attempts ]; do
  if kubectl logs "$ORACLE_POD" 2>&1 | grep -q "DATABASE IS READY TO USE!"; then
    echo "✅ Oracle realmente está listo (listener + PDB arriba)."
    break
  fi

  echo "⏳ Intento $attempt/$max_attempts: aún no se ve el mensaje READY..."
  sleep 5
  attempt=$((attempt+1))
done

if [ $attempt -gt $max_attempts ]; then
  echo "❌ Oracle NO mostró 'DATABASE IS READY TO USE!' a tiempo."
  exit 1
fi

echo "============================================"
echo "   🔧 INICIALIZANDO POSTGRES (USUARIO/DB)   "
echo "============================================"

POSTGRES_POD=$(kubectl get pod -l app=postgresql -o jsonpath='{.items[0].metadata.name}')

kubectl exec "$POSTGRES_POD" -- bash -c "cat <<'EOF' | psql -U postgres
-- Limpieza previa por si quedó algo viejo
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'castor_customers';

DROP DATABASE IF EXISTS castor_customers;
DROP ROLE IF EXISTS castor_user;

-- Crear usuario y base de datos de aplicación
CREATE USER castor_user WITH PASSWORD 'Castor2025' CREATEDB;
CREATE DATABASE castor_customers OWNER castor_user;

-- Conectarnos a la nueva base
\c castor_customers;

-- Ajustar schema public
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO castor_user;
ALTER SCHEMA public OWNER TO castor_user;

-- Crear tabla de clientes
CREATE TABLE public.customers (
    id uuid PRIMARY KEY,
    active boolean,
    email varchar(255),
    name varchar(255)
);

ALTER TABLE public.customers OWNER TO castor_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.customers TO castor_user;
EOF
"

echo "✔️ PostgreSQL inicializado (usuario, DB, esquema, tabla, permisos)."
echo ""

echo "============================================"
echo "    🔧 INICIALIZANDO ORACLE (USUARIO/TABLA) "
echo "============================================"

# Obtener contraseña real desde el Secret
ORACLE_PASS=$(kubectl get secret database-secrets -o jsonpath='{.data.ORACLE_PASSWORD}' | base64 -d)

ORACLE_POD=$(kubectl get pod -l app=oracle-db -o jsonpath='{.items[0].metadata.name}')

kubectl exec "$ORACLE_POD" -- bash -c "cat <<'EOSQL' | sqlplus -s sys/$ORACLE_PASS@localhost:1521/xepdb1 as sysdba
-- Cambiar al PDB correcto
ALTER SESSION SET CONTAINER = xepdb1;

-- Borrar usuario si ya existía (evita ORA-01918)
BEGIN
    EXECUTE IMMEDIATE 'DROP USER CASTOR_BILLING CASCADE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -1918 THEN
            RAISE;
        END IF;
END;
/

-- Crear usuario de aplicación
CREATE USER CASTOR_BILLING IDENTIFIED BY \"Castor2025\" QUOTA UNLIMITED ON USERS;

-- Permisos necesarios
GRANT CREATE SESSION,
       CREATE TABLE,
       CREATE SEQUENCE,
       CREATE TRIGGER,
       CREATE VIEW,
       CREATE PROCEDURE,
       UNLIMITED TABLESPACE
TO CASTOR_BILLING;

-- Crear tabla INVOICES en su esquema
CREATE TABLE CASTOR_BILLING.INVOICES (
    ID RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    CUSTOMER_ID RAW(16) NOT NULL,
    SUBTOTAL NUMBER(18,2) NOT NULL,
    TAX NUMBER(18,2) NOT NULL,
    DISCOUNT NUMBER(18,2) NOT NULL,
    TOTAL NUMBER(18,2) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear procedimiento almacenado para validar cliente
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

EOSQL
"

echo "✔️ Oracle inicializado (usuario, tabla, procedimiento almacenado, permisos)."
echo ""

echo "============================================"
echo " 🔁 REINICIANDO BILLING-SERVICE (JDBC FRESCO)"
echo "============================================"

kubectl rollout restart deployment/billing-service
kubectl rollout status deployment/billing-service --timeout=180s

echo "✔️ Billing-service reiniciado."
echo ""

echo "============================================"
echo "   🎉✔️ DESPLIEGUE COMPLETO Y EXITOSO        "
echo "============================================"
#!/usr/bin/env bash

# setup.sh
# Despliega todo el entorno: Minikube + Oracle + PostgreSQL + Jobs + Microservicios

set -e  # si algo falla, el script se detiene

echo ""
echo "============================================"
echo "     🚀 INICIANDO DESPLIEGUE COMPLETO       "
echo "============================================"
echo ""

# ---------------------------------------
# 0. Verificar que minikube existe/funciona
# ---------------------------------------

echo "🔍 Verificando estado de Minikube..."

if ! command -v minikube >/dev/null 2>&1; then
  echo "❌ Error: minikube no está instalado o no está en el PATH."
  exit 1
fi

if ! minikube status >/dev/null 2>&1; then
  echo "⚙️  Minikube no tiene un clúster activo. Creando uno nuevo..."
  minikube start \
    --driver=docker \
    --cpus=6 \
    --memory=12288 \
    --disk-size=40g
else
  echo "✅ Minikube ya tiene un clúster. Asegurando que esté iniciado..."
  minikube start >/dev/null 2>&1 || true
fi

echo "✔️ Minikube está listo."
echo ""

# ---------------------------------------
# 1. Crear carpeta persistente para Oracle dentro de Minikube
# ---------------------------------------

echo "============================================"
echo "   🟦 Preparando almacenamiento para Oracle  "
echo "============================================"
echo ""

echo "🔧 Creando carpeta /mnt/data/oracle dentro del nodo Minikube..."

minikube ssh "sudo mkdir -p /mnt/data/oracle && sudo chmod 777 /mnt/data/oracle"

echo "✔️ Carpeta /mnt/data/oracle creada y con permisos 777."
echo ""

# ---------------------------------------
# 2. StorageClass + PV + PVC
# ---------------------------------------

echo "============================================"
echo "     📦 Configurando Storage (PV / PVC)     "
echo "============================================"
echo ""

echo "🔸 Aplicando StorageClass para Oracle..."
kubectl apply -f k8s/storageclass-oracle.yaml

echo "🔸 Aplicando PersistentVolume para Oracle..."
kubectl apply -f k8s/pv-oracle.yaml

echo "🔸 Aplicando PersistentVolumeClaims (PostgreSQL y Oracle)..."
kubectl apply -f k8s/persistent-volume-claims.yaml

echo "✔️ Storage configurado."
echo ""

# ---------------------------------------
# 3. ConfigMaps y Secrets
# ---------------------------------------

echo "============================================"
echo "      🔑 ConfigMaps y Secrets de DB         "
echo "============================================"
echo ""

echo "🔸 Aplicando ConfigMap principal (billing-config)..."
kubectl apply -f k8s/configmap.yaml

echo "🔸 Aplicando ConfigMap con SQL de Oracle..."
kubectl apply -f k8s/configmaps/oracle-sql-configmap.yaml

echo "🔸 Aplicando ConfigMap con SQL de PostgreSQL..."
kubectl apply -f k8s/configmaps/postgres-sql-configmap.yaml

echo "🔸 Aplicando Secrets (credenciales de BD)..."
kubectl apply -f k8s/secrets.yaml

echo "✔️ ConfigMaps y Secrets aplicados."
echo ""

# ---------------------------------------
# 4. Deployments + Services
# ---------------------------------------

echo "============================================"
echo "         📡 Desplegando Deployments         "
echo "============================================"
echo ""

echo "🔸 Aplicando Deployments (PostgreSQL, Oracle, servicios)..."
kubectl apply -f k8s/deployments.yaml

echo "🔸 Aplicando Services..."
kubectl apply -f k8s/services.yaml

echo "✔️ Deployments y Services aplicados."
echo ""

# ---------------------------------------
# 5. Esperar a que PostgreSQL y Oracle estén listas
# ---------------------------------------

echo "============================================"
echo "        ⏳ Esperando bases de datos         "
echo "============================================"
echo ""

echo "⏳ Esperando a que el pod de PostgreSQL esté READY..."
kubectl wait --for=condition=ready pod -l app=postgresql --timeout=180s

echo "⏳ Esperando a que el pod de Oracle esté READY..."
kubectl wait --for=condition=ready pod -l app=oracle-db --timeout=300s

echo "✔️ Bases de datos listas para inicialización."
echo ""

# ---------------------------------------
# 6. Ejecutar Jobs de inicialización SQL
# ---------------------------------------

echo "============================================"
echo "    🧩 Ejecutando Jobs de inicialización    "
echo "============================================"
echo ""

echo "🔸 Aplicando Job de Oracle..."
kubectl apply -f k8s/jobs/oracle-init-job.yaml

echo "🔸 Aplicando Job de PostgreSQL..."
kubectl apply -f k8s/jobs/postgres-init-job.yaml

echo "⏳ Esperando a que el Job de Oracle termine..."
kubectl wait --for=condition=complete job/oracle-init-job --timeout=180s

echo "⏳ Esperando a que el Job de PostgreSQL termine..."
kubectl wait --for=condition=complete job/postgres-init-job --timeout=180s

echo "✔️ Jobs de inicialización completados."
echo ""

# ---------------------------------------
# 7. Verificación final
# ---------------------------------------

echo "============================================"
echo "           🎉 DESPLIEGUE COMPLETO           "
echo "============================================"
echo ""

echo "📋 Pods en el clúster:"
kubectl get pods -o wide
echo ""

echo "📋 Services:"
kubectl get svc
echo ""

echo "👉 Para conectarte desde DBeaver:"
echo ""
echo "   PostgreSQL (dentro de Minikube):"
echo "     kubectl port-forward svc/postgresql 15432:5432"
echo "     Host: localhost"
echo "     Port: 15432"
echo "     DB:   castor_customers"
echo "     User: castor_user"
echo "     Pass: Castor2025*"
echo ""
echo "   Oracle XE (dentro de Minikube):"
echo "     kubectl port-forward svc/oracle-db 15210:1521"
echo "     Host: localhost"
echo "     Port: 15210"
echo "     Service: XEPDB1"
echo "     User (app): CASTOR_BILLING / Castor2025*"
echo "     User (admin): SYSTEM / Castor2025*"
echo ""
echo "🔥 Todo está listo."

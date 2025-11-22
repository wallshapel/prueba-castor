# 🎉 README — Guía Súper Épica del Proyecto Billing Service 🚀

Bienvenido al **Billing Multiverse™**, donde se mezclan Spring Boot, Kubernetes, Oracle, PostgreSQL, Node.js, SonarQube y magia negra (pero de la buena 😎).

Aquí tienes una guía **divertida**, clara y completa para:
- 🧪 ejecutar tests  
- 🔍 correr análisis de calidad  
- 🚀 desplegar todo en K8s  
- 🌐 consumir los endpoints  
- 🤖 usar el cliente Node.js  

Prepárate, porque lo que viene es *nivel Dios*.

---

# 🧪 1. Ejecutar Tests (Perfil `dev`)

## 0️⃣ Activar el perfil `dev`

Abre `application.yml` y:

- **Comenta temporalmente**:

```yaml
# active: ${SPRING_PROFILES_ACTIVE:prod}
```

- **Descomenta**:

```yaml
active: dev
```

💡 Esto le dice al proyecto:  
> "Hey bro, estamos en modo DEV, no PROD. Relax."

---

## 1️⃣ Asegurar que Oracle y PostgreSQL estén arriba 🏋️‍♂️

Pueden estar levantados con Docker, en tu PC, en tu NAS, en tu nevera…  
Lo importante es que respondan en sus puertos estándar.

---

## 2️⃣ Las bases de datos deben tener estructura y datos necesarios 🏗️

Incluye:

- usuarios 👤  
- contraseñas 🔐  
- tablas 📄  
- stored procedures 🧙‍♂️  

Todo está disponible en la carpeta:

```
/sql
```

Si usas credenciales diferentes a las de los .sql, actualízalas en:

```
src/main/resources/application-dev.yml
```

---

## 3️⃣ Ejecutar los tests

### 🟢 Opción A — Terminal:

```bash
mvn clean test
```

### 🟣 Opción B — IntelliJ IDEA

- Abre el proyecto `billing-service`
- Corre los tests dando clic en el ícono ▶ o desde el panel de Maven

💥 BOOM. Tests listos.

---

# 📊 2. Análisis de Cobertura + Calidad con SonarQube

En `billing-service/docs/img` encontrarás capturas:

- Reporte JaCoCo  
- Dashboard de SonarQube  

Para correr tu propio análisis:

---

## Requisitos:

- Perfil `dev` activo  
- Tests ya ejecutados  
- Servidor de SonarQube encendido (http://localhost:9000)  
- Token válido configurado  

---

## Ejecutar el análisis

Desde `billing-service`:

```bash
sonar-scanner
```

En `sonar-project.properties`, ajusta:

```properties
sonar.token=TU_TOKEN_AQUI
sonar.host.url=http://localhost:9000
```

🎯 Eso enviará el análisis directamente al dashboard.

---

# 🚀 3. Levantar TODO el proyecto con Kubernetes (Minikube Edition 🐳)

Este es el momento donde el setup.sh demuestra por qué merece respeto.

---

## 1️⃣ Requisitos

- Minikube instalado  
- Docker funcionando  
- Perfil PROD activo en `application.yml`:

```yaml
active: ${SPRING_PROFILES_ACTIVE:prod}
```

---

## 2️⃣ Dar permisos al script

```bash
chmod +x setup.sh
```

---

## 3️⃣ Ejecutarlo como Dios manda

```bash
./setup.sh
```

---

## 4️⃣ ¿Qué hace este script? (Spoiler: TODO 😅)

- Borra clusters viejos  
- Destruye PV, PVC y hostpaths zombificados  
- Limpia residuos ocultos de PostgreSQL y Oracle  
- Inicia un cluster nuevo  
- Compila imágenes Docker sin caché  
- Aplica todos los manifiestos de K8s  
- Crea usuarios, tablas, SP y permisos  
- Espera a que Oracle “despierte” (literalmente revisa logs)  
- Reinicia billing-service para asegurar JDBC fresco  

Al final verás:

```
🎉✔️ DESPLIEGUE COMPLETO Y EXITOSO
```

y podrás sentirte programador nivel ANCIANO SUPREMO.

---

# 🌐 4. Consumir Endpoints desde Fuera del Cluster

Primero abre un túnel:

```bash
kubectl port-forward deployment/billing-service 8081:8081
```

Ahora podrás consumir:

```
http://localhost:8081/api/...
```

Desde:

- Postman  
- Insomnia  
- Thunder Client  
- Curl  
- Tu abuelita con cURL  
- El cliente Node.js (ver abajo)  

---

# 🤖 5. Usar el Client Node.js Para Disparar TODOS los Endpoints

### 1️⃣ Entra a la carpeta:

```bash
cd node-client
```

### 2️⃣ Instala dependencias:

```bash
npm install
```

### 3️⃣ Ejecuta la fiesta:

```bash
node client.js
```

Este script:

- ejecuta todos los endpoints  
- imprime cada respuesta  
- te permite cambiar parámetros, IDs, JSON, etc.  

Es como Postman… pero divertido.

---

# ❤️ Gracias por usar esta guía

Si quedaste satisfecho:

- ⭐ eres un crack  
- 🔧 tu infraestructura también  
- 🐱 tu gato probablemente aprobó  
- 🚀 y tu proyecto ahora es imparable  

Fin ✨

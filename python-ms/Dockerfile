# ============================================================
# 1) Imagen base: Python 3.12 slim (ligera para producción)
# ============================================================
FROM python:3.12-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar requirements del proyecto
COPY requirements.txt .

# Instalar dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el código de FastAPI
COPY app ./app

# Exponer el puerto que utilizará Uvicorn
EXPOSE 5001

# Comando de arranque en producción (sin --reload)
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "5001"]

# 🚀 Guía de Deployment en Render - Backend Spring Boot

## 📋 Pre-requisitos

1. **Cuenta en Render**: Crea una cuenta en https://render.com
2. **Proyecto en GitHub**: Sube tu proyecto a un repositorio de GitHub
3. **Supabase configurado**: Asegúrate de tener tu proyecto de Supabase creado
4. **Variables de entorno**: Ten lista toda la información de configuración

## 🔧 Configuración del Proyecto

### 1. Archivos de configuración creados:

✅ **`render.yaml`** - Configuración del servicio web
✅ **`system.properties`** - Versión de Java (17)
✅ **`Dockerfile`** - Contenedor Docker para el deployment
✅ **`application.yml`** - Configurado para producción
✅ **`.dockerignore`** - Optimización del build

### 2. Variables de entorno necesarias:

Copia estas variables en el archivo `render-env-vars.txt` y luego en el panel de Render:

```bash
# Requeridas - Supabase
SUPABASE_URL=https://tusupabase.supabase.co
SUPABASE_ANON_KEY=tu-anon-key-aqui
SUPABASE_SERVICE_ROLE_KEY=tu-service-role-key-aqui

# Requeridas - JWT
JWT_SECRET=tu-secreto-jwt-muy-seguro-minimo-32-caracteres
JWT_EXP_MINUTES=60

# Opcionales - Tablas (valores por defecto)
FINANCE_TABLE_INCOME=ingresos
FINANCE_TABLE_EXPENSES=gastos
FINANCE_TABLE_MICRO=microgastos

# Opcionales - IA (si usas asesor financiero)
AI_API_URL=https://api.openai.com/v1/chat/completions
AI_API_KEY=tu-api-key-de-openai
AI_MODEL=gpt-3.5-turbo

# Opcional - Mercado Libre
MELI_SITE=MLA
```

## 🚀 Pasos para Deployar en Render

### Paso 1: Preparar el repositorio
```bash
# Asegúrate de que todos los archivos estén committeados
git add .
git commit -m "Preparación para deployment en Render"
git push origin main
```

### Paso 2: Crear el servicio en Render

1. **Inicia sesión en Render**: https://render.com
2. **Click en "New Web Service"**
3. **Conecta tu repositorio de GitHub**
4. **Selecciona tu proyecto**

### Paso 3: Configurar el servicio

**Configuración básica:**
- **Name**: `spring-boot-networking-backend`
- **Environment**: `Docker`
- **Dockerfile Path**: `./Dockerfile`
- **Build Command**: Dejar vacío (Render usa el Dockerfile)

**Configuración avanzada:**
- **Instance Type**: Free (o Starter si necesitas más recursos)
- **Auto-deploy**: Yes (recomendado)

### Paso 4: Configurar variables de entorno

1. **Ve a la sección "Environment"**
2. **Agrega todas las variables del archivo `render-env-vars.txt`**
3. **Asegúrate de que las variables requeridas estén completas**

### Paso 5: Health Check (Revisión de salud)

El servicio incluye endpoints de salud en:
- `/actuator/health` - Health check principal
- `/actuator/info` - Información del servicio

### Paso 6: Deploy

1. **Click en "Create Web Service"**
2. **Espera 5-10 minutos** para que el deployment termine
3. **Verifica los logs** para confirmar que todo está funcionando

## 🧪 Verificación del deployment

### Endpoints para probar:

```bash
# Health check
curl https://tu-servicio.onrender.com/actuator/health

# Test de auth
curl https://tu-servicio.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'

# Test de networking
curl https://tu-servicio.onrender.com/api/networking/profiles \
  -H "Authorization: Bearer TU_JWT_TOKEN"
```

## 🔍 Solución de problemas

### Problemas comunes:

1. **Build fallido**:
   - Verifica que el JAR exista: `target/supabase-auth-java-0.0.1-SNAPSHOT.jar`
   - Asegúrate de que el Dockerfile esté en la raíz

2. **Variables de entorno faltantes**:
   - Revisa que todas las variables requeridas estén configuradas
   - Verifica los logs de Render para errores específicos

3. **Puerto incorrecto**:
   - Render asigna el puerto automáticamente via `$PORT`
   - El servidor debe escuchar en `0.0.0.0:$PORT`

4. **Problemas de CORS**:
   - El backend ya tiene CORS configurado para producción
   - Asegúrate de configurar los orígenes permitidos

## 📊 Monitoreo

Una vez deployado, puedes:
- **Ver logs en tiempo real** en el dashboard de Render
- **Monitorear uso de recursos** (CPU, memoria, etc.)
- **Configurar alertas** para cuando el servicio falle
- **Ver métricas de salud** en `/actuator/health`

## 🔄 Actualizaciones futuras

Para actualizar el servicio:
1. Haz commit de tus cambios en GitHub
2. Render detectará automáticamente los cambios (si auto-deploy está activado)
3. El servicio se rebuildará y redeployará automáticamente

## 📞 Soporte

Si encuentras problemas:
- **Logs de Render**: Dashboard → Logs
- **Documentación**: https://render.com/docs
- **Comunidad**: https://community.render.com/

---

✅ **Tu backend está listo para deployar en Render con el nuevo módulo de Networking implementado!**
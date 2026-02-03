# 🏙️ EcoCity - Gestión de Incidencias Urbanas

**EcoCity** es una aplicación Android nativa diseñada para que los ciudadanos puedan reportar incidencias en la vía pública de forma rápida y eficiente. El proyecto combina el uso de hardware (Cámara), servicios de localización (Google Maps) y persistencia de datos local.

---

## 🚀 Funcionalidades Principales

* **🔐 Autenticación**: Sistema de acceso simple para usuarios.
* **📸 Captura Multimedia**: Integración con la cámara del dispositivo para documentar incidencias mediante fotos, gestionadas de forma segura a través de `FileProvider`.
* **📍 Geolocalización**: Uso de la API de Google Maps para seleccionar la ubicación exacta del problema y visualizarla posteriormente en el detalle.
* **🗄️ Persistencia SQLite**: Almacenamiento local robusto de todas las incidencias (título, descripción, nivel de importancia, ruta de imagen y coordenadas GPS).
* **♻️ Gestión Dinámica**: Listado de incidencias en un `RecyclerView` con soporte para:
    * Colores dinámicos según la prioridad (Alta, Media, Baja).
    * **Swipe-to-Delete**: Borrado intuitivo deslizando elementos con confirmación mediante `AlertDialog`.
    * 
---

## 🛠️ Aspectos Técnicos

### Programación de Servicios y Procesos (PSP)
* **Multihilo**: Implementación de `ExecutorService` para realizar operaciones pesadas (lectura/escritura en base de datos) en hilos secundarios, garantizando que la interfaz de usuario (UI Thread) nunca se bloquee.
* **Sincronización de UI**: Uso de `Handler` y `Looper.getMainLooper()` para actualizar la vista de forma segura tras completar tareas en segundo plano.

### Programación Multimedia y Dispositivos Móviles (PMDM)
* **Hardware**: Gestión de permisos en tiempo de ejecución para el uso de la cámara.
* **Google Maps SDK**: Implementación de mapas interactivos con marcadores personalizados.
* **Diseño Adaptativo**: Uso de `ConstraintLayout` para interfaces modernas y equilibradas.

---

## 📦 Instalación y Requisitos

1.  Clonar el repositorio.
2.  Abrir con **Android Studio** (Koala o superior).
3.  Añadir tu propia `API_KEY` de Google Maps en el archivo `local.properties` o `AndroidManifest.xml`.
4.  Asegurarse de tener configurado un dispositivo o emulador con **Android 8.0 (Oreo)** o superior.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje**: Java ☕
* **Base de Datos**: SQLite
* **Mapas**: Google Maps SDK for Android
* **Componentes**: Material Design, CardView, RecyclerView, FloatingActionButton.

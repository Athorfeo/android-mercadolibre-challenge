# android-mercadolibre-challenge

## Requerimientos

- **Android Studio**: Koala 2024.1.2 (Build #AI-241.18034.62.2412.12266719, built on August 22, 2024),
- **Java**: v17.0.11.

## Arquitectura

La arquitectura de la aplicacion es sigue los lineamientos descritos es [Android App Architecture](https://developer.android.com/topic/architecture), estructurado de la siguiente manera:

- **data**: Carpeta destinada a los `Repository`, `DataSource` y `DataStore`.
- di: Carpeta destinada a la inyeccion de dependencias.
- domain: Carpeta destinada a los `UseCase`.
- model: Carpeta que contiene los modelos y estados usados en las vistas.
- navigation: Carpeta destinada a los archivos de navigacion.
- network: Carpeta destinada al consumo de sevicios.
- ui: Carpeta destinada a las vistas y `ViewModel`.
- util: Carpeta destinada para los helper y utilizarios de la aplicacion.
- MainActivity.kt: `Activity` principal de la aplicacion.
- TemplateApp.kt: Clase `Application` del proyecto.

## Vistas

El proyecto cuenta con 3 vistas:

- QuerySearchScreen: Vista destinada al campo de busqueda.

![QuerySearchScreen](/docs/query-search-screen.jpg)

- ResultSearchScreen: Vista destinada a mostrar el resultado y paging de la busqueda.

![01 ResultSearchScreen](/docs/01-result-search-screen.jpg)
![02 ResultSearchScreen](/docs/02-result-search-screen.jpg)

- DetailItemScreen: Vista destinada a mostrar el detalle del item seleccionado.

![app-modules](/docs/detail-item-screen.jpg)

## Calidad del codigo

El proyecto cuenta con analisis de codigo estatico usando `DeteKt`:
![Detekt](/docs/detekt.jpg)

El proyecto cuenta con pruebas unitarias en java usando `Kover`:
![Detekt](/docs/kover.jpg)

El proyecto cuenta con pruebas unitarias a la UI:
![Detekt](/docs/ui-tests.jpg)
# API Documentation - League of Legends Data Test Endpoints

## Overview

Esta aplicación carga datos de League of Legends (campeones e ítems) desde archivos JSON al inicio y expone endpoints REST para consultarlos.

## Componentes Principales

### 1. Entidades (Model)
- **Champion**: Representa un campeón de League of Legends con todas sus estadísticas
- **Item**: Representa un ítem del juego con sus propiedades

### 2. Repositorios (Repository)
- **ChampionRepository**: Acceso a datos de campeones
- **ItemRepository**: Acceso a datos de ítems

### 3. Servicios (Service)
- **DataLoaderService**: Carga los datos JSON y los guarda en la base de datos H2

### 4. Configuración (Config)
- **DataLoaderRunner**: CommandLineRunner que ejecuta la carga de datos al iniciar la aplicación

### 5. Controller (Controller)
- **TestController**: Endpoints REST para probar la carga de datos

## Endpoints de la API

Base URL: `http://localhost:8080/api/test`

### Estadísticas Generales

#### GET /stats
Obtiene estadísticas sobre los datos cargados.

**Respuesta:**
```json
{
  "totalChampions": 168,
  "totalItems": 250,
  "timestamp": 1234567890
}
```

#### GET /health
Verifica que el controller está funcionando.

**Respuesta:**
```json
{
  "status": "UP",
  "message": "Test controller is running"
}
```

### Campeones

#### GET /champions
Obtiene todos los campeones.

**Respuesta:** Lista de campeones con todas sus propiedades.

#### GET /champions/{id}
Obtiene un campeón específico por ID.

**Ejemplo:** `GET /champions/Aatrox`

**Respuesta:**
```json
{
  "id": "Aatrox",
  "key": "266",
  "name": "Aatrox",
  "title": "the Darkin Blade",
  "blurb": "Once honored defenders of Shurima...",
  "version": "15.18.1",
  "partype": "Blood Well",
  "attackInfo": 8,
  "defenseInfo": 4,
  "magicInfo": 3,
  "difficultyInfo": 4,
  "tags": "Fighter",
  "hp": 650.0,
  "armor": 38.0,
  ...
}
```

#### GET /champions/search?name={name}
Busca campeones por nombre (case-insensitive).

**Ejemplo:** `GET /champions/search?name=ahri`

#### GET /champions/tag/{tag}
Obtiene campeones por tag.

**Ejemplo:** `GET /champions/tag/Mage`

**Tags disponibles:**
- Fighter
- Mage
- Assassin
- Tank
- Marksman
- Support

### Ítems

#### GET /items
Obtiene todos los ítems.

#### GET /items/{id}
Obtiene un ítem específico por ID.

**Ejemplo:** `GET /items/1001`

**Respuesta:**
```json
{
  "id": "1001",
  "name": "Boots",
  "description": "<mainText><stats>...",
  "plaintext": "Slightly increases Move Speed",
  "goldBase": 300,
  "goldTotal": 300,
  "goldSell": 210,
  "purchasable": true,
  "tags": "Boots",
  "flatMovementSpeedMod": 25.0,
  ...
}
```

#### GET /items/search?name={name}
Busca ítems por nombre (case-insensitive).

**Ejemplo:** `GET /items/search?name=sword`

#### GET /items/purchasable
Obtiene solo los ítems que se pueden comprar en la tienda.

#### GET /items/tag/{tag}
Obtiene ítems por tag.

**Ejemplo:** `GET /items/tag/Damage`

**Tags disponibles:**
- Damage
- Health
- Armor
- SpellBlock
- Mana
- AttackSpeed
- LifeSteal
- etc.

## Cómo Ejecutar la Aplicación

### Requisitos
- Java 21
- Maven

### Pasos

1. **Compilar la aplicación:**
   ```bash
   mvn clean compile
   ```

2. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

3. **Acceder a la API:**
   - API Base: http://localhost:8080/api/test
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:loldb`
     - Username: `sa`
     - Password: (dejar vacío)

### Verificar la Carga de Datos

Al iniciar la aplicación, verás logs como:
```
INFO  r.t.config.DataLoaderRunner - === Starting data load ===
INFO  r.t.service.DataLoaderService - Loading champions from JSON...
INFO  r.t.service.DataLoaderService - Loaded 168 champions
INFO  r.t.service.DataLoaderService - Loading items from JSON...
INFO  r.t.service.DataLoaderService - Loaded 250 items
INFO  r.t.config.DataLoaderRunner - === Data load completed successfully ===
```

## Ejemplos de Uso con curl

```bash
# Obtener estadísticas
curl http://localhost:8080/api/test/stats

# Buscar campeones llamados "Ahri"
curl http://localhost:8080/api/test/champions/search?name=ahri

# Obtener todos los magos
curl http://localhost:8080/api/test/champions/tag/Mage

# Buscar ítems con "sword" en el nombre
curl http://localhost:8080/api/test/items/search?name=sword

# Obtener solo ítems comprables
curl http://localhost:8080/api/test/items/purchasable
```

## Estructura de Datos

### Champion
Campos principales:
- `id`, `key`, `name`, `title`, `blurb`
- `version`, `partype`, `tags`
- Información: `attackInfo`, `defenseInfo`, `magicInfo`, `difficultyInfo`
- Estadísticas: `hp`, `mp`, `armor`, `spellblock`, `movespeed`, `attackdamage`, etc.

### Item
Campos principales:
- `id`, `name`, `description`, `plaintext`
- Gold: `goldBase`, `goldTotal`, `goldSell`, `purchasable`
- Recipe: `buildsFrom`, `buildsInto`
- Estadísticas: varios modificadores de stats (`flatHPPoolMod`, `flatArmorMod`, etc.)

## Base de Datos

La aplicación usa H2 en memoria, lo que significa que los datos se cargan cada vez que inicia la aplicación y se pierden al cerrarla. Esto es ideal para testing.

Para ver los datos en la consola H2:
1. Ir a http://localhost:8080/h2-console
2. Usar `jdbc:h2:mem:loldb` como JDBC URL
3. Username: `sa`, Password: (vacío)
4. Tablas disponibles: `CHAMPIONS`, `ITEMS`

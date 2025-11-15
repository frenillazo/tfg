# Sistema de Recomendaciones de Items - League of Legends

## Descripción

Sistema de recomendaciones de items que utiliza los algoritmos TOPSIS y TODIM para sugerir los mejores items según el estado actual de la partida.

## Arquitectura

### Algoritmos Implementados

1. **TOPSIS** (Technique for Order Preference by Similarity to Ideal Solution)
   - Normalización vectorial de la matriz de criterios
   - Cálculo de distancias a soluciones ideales positiva y negativa
   - Peso: 70% del score final

2. **TODIM** (Tomada de Decisão Interativa Multicritério)
   - Normalización min-max
   - Dominancia con aversión a pérdidas (λ=2.25)
   - Peso: 30% del score final

### Flujo del Sistema

```
JSON del juego → Análisis del campeón → Análisis enemigo → Filtrado de items
                                                               ↓
Top 5 items ← Combinación (70% TOPSIS + 30% TODIM) ← TODIM ← TOPSIS ← Matriz de criterios
```

### 12 Criterios Evaluados

| Criterio | Valor de Oro (por punto) |
|----------|--------------------------|
| Attack Damage | 35g |
| Ability Power | 21.75g |
| Attack Speed | 25g (por 1% AS) |
| Critical Chance | 40g (por 1% crit) |
| Armor | 20g |
| Magic Resist | 18g |
| Health | 2.67g (por HP) |
| Cooldown Reduction | 26.67g (por 1% CDR) |
| Armor Penetration | 30g (Lethality) |
| Magic Penetration | 31.11g |
| Life Steal | 27.5g (por 1% LS) |
| Movement Speed | 12g (por 1% MS) |

## Servicios Implementados

### 1. ChampionAnalysisService
- Analiza escalados de habilidades (AD/AP ratios)
- Extrae etiquetas de `levelTipLabels`
- Determina tipo de campeón: AD_FOCUSED, AP_FOCUSED, TANK, MIXED, UTILITY

### 2. EnemyAnalysisService
- Analiza composición del equipo enemigo
- Calcula promedios de Armor, MR, HP
- Detecta tipos de daño (físico, mágico, mixto)
- Identifica amenazas de CC

### 3. ItemFilterService
- Filtra items completos (depth >= 3 o sin upgrade)
- Solo items comprables (`inStore=true`, `purchasable=true`)
- Filtra por relevancia según perfil del campeón
- Excluye items ya poseídos

### 4. CriteriaMatrixService
- Construye matriz de 12 criterios
- Calcula Gold Efficiency
- Calcula pesos dinámicos según:
  - Tipo de escalado del campeón
  - Etiquetas de habilidades
  - Composición enemiga (resistencias, tipos de daño, CC)

### 5. TOPSISService
- Normalización vectorial
- Aplicación de pesos
- Cálculo de distancias eucl idianas
- Score de proximidad relativa

### 6. TODIMService
- Normalización min-max
- Dominancia por pares de alternativas
- Aversión a pérdidas (λ=2.25)
- Score de dominancia global

### 7. ItemRecommendationService
- Orquesta todo el flujo
- Combina resultados: `finalScore = 0.7*TOPSIS + 0.3*TODIM`
- Genera explicaciones
- Retorna Top 5 items

## API REST

### Endpoint Principal

**POST** `/api/recommendations/items`

**Request Body:** JSON del estado del juego (estructura de `example_data_game.json`)

**Response:**
```json
{
  "championName": "Annie",
  "championLevel": 1,
  "currentGold": 0.0,
  "championProfile": "AP_FOCUSED",
  "enemyAnalysis": {
    "averageArmor": 50.0,
    "averageMagicResist": 40.0,
    "physicalDamageChampions": 3,
    "magicDamageChampions": 2,
    "mixedDamageChampions": 0,
    "ccChampions": 2,
    "enemyChampions": ["Garen", "Yasuo", "Zed", "Lux", "Thresh"]
  },
  "recommendations": [
    {
      "rank": 1,
      "itemId": "3089",
      "itemName": "Rabadon's Deathcap",
      "finalScore": 0.89,
      "topsisScore": 0.92,
      "todimScore": 0.81,
      "goldTotal": 3600.0,
      "purchasable": true,
      "criteriaScores": {
        "abilityPower": 120.0,
        "attackDamage": 0.0,
        ...
      },
      "explanation": "Recommended for ap focused champions. Provides: Ability power (120.0), Magic penetration (15.0). Gold efficiency: 105.3%."
    },
    ...
  ],
  "processingTimeMs": 245
}
```

### Health Check

**GET** `/api/recommendations/health`

**Response:** `"Item Recommendation Service is running"`

## Cómo Usar

### 1. Cargar Datos en la Base de Datos

```bash
# Iniciar la aplicación
./mvnw spring-boot:run

# Cargar todos los datos (champions, items, spells)
curl -X POST http://localhost:8080/api/batch/load-all
```

### 2. Obtener Recomendaciones

```bash
curl -X POST http://localhost:8080/api/recommendations/items \
  -H "Content-Type: application/json" \
  -d @src/main/resources/data/example_data_game.json
```

### 3. Ver Resultados

El sistema retornará:
- Top 5 items recomendados
- Scores de TOPSIS y TODIM
- Score final combinado
- Explicación de cada recomendación
- Análisis del perfil del campeón
- Análisis del equipo enemigo
- Tiempo de procesamiento

## Ejemplo de Uso Programático

```java
@Autowired
private ItemRecommendationService recommendationService;

public void getRecommendations() {
    // Crear GameStateRequestDTO desde el JSON de la API de LoL
    GameStateRequestDTO gameState = parseGameStateFromAPI();

    // Obtener recomendaciones
    ItemRecommendationResponseDTO recommendations =
        recommendationService.recommendItems(gameState);

    // Usar las recomendaciones
    recommendations.getRecommendations().forEach(item -> {
        System.out.println("Rank " + item.getRank() + ": " + item.getItemName());
        System.out.println("Score: " + item.getFinalScore());
        System.out.println("Explanation: " + item.getExplanation());
    });
}
```

## Pesos Dinámicos

El sistema ajusta automáticamente los pesos de los criterios según:

### Tipo de Campeón
- **AD_FOCUSED**: +5 AD, +3 AS, +3 Crit, +2 ArPen, +2 LS
- **AP_FOCUSED**: +5 AP, +3 MagPen, +3 CDR
- **TANK**: +5 HP, +4 Armor, +4 MR
- **MIXED**: +3 AD, +3 AP, +2 HP
- **UTILITY**: +4 CDR, +3 MS, +2 HP

### Composición Enemiga
- Armor > 80: +3 ArmorPenetration
- MR > 60: +3 MagicPenetration
- Physical Threat > 0.6: +3 Armor
- Magical Threat > 0.6: +3 MagicResist
- CC Threat > 0.6: +2 HP, +1.5 MR

### Etiquetas de Habilidades
- Tags como "Damage", "AD Ratio": +1 AttackDamage
- Tags como "AP Ratio", "Magic": +1 AbilityPower
- Tags como "Cooldown", "CDR": +1.5 CooldownReduction
- Etc.

## Estructura de Archivos

```
src/main/java/rafa/tfg/
├── application/
│   ├── controller/
│   │   └── RecommendationController.java
│   └── dto/recommendation/
│       ├── GameStateRequestDTO.java
│       └── ItemRecommendationResponseDTO.java
├── domain/
│   ├── model/recommendation/
│   │   ├── ChampionProfile.java
│   │   ├── EnemyComposition.java
│   │   ├── ItemCandidate.java
│   │   └── WeightProfile.java
│   └── service/recommendation/
│       ├── ChampionAnalysisService.java
│       ├── EnemyAnalysisService.java
│       ├── ItemFilterService.java
│       ├── CriteriaMatrixService.java
│       ├── TOPSISService.java
│       ├── TODIMService.java
│       └── ItemRecommendationService.java
```

## Notas Técnicas

- **Gold Efficiency**: Se calcula como `(valor total de stats / costo de oro) * 100`
- **TOPSIS Weight**: 70% (más peso a la solución racional)
- **TODIM Weight**: 30% (considera la aversión a pérdidas del usuario)
- **Loss Aversion λ**: 2.25 (valor estándar de la teoría de prospectos)
- **Normalización TOPSIS**: Vectorial (raíz de suma de cuadrados)
- **Normalización TODIM**: Min-Max
- **Todos los criterios son de beneficio**: Mayor valor es mejor

## Testing

Para probar con datos de ejemplo:

```bash
# Con el servidor corriendo
curl -X POST http://localhost:8080/api/recommendations/items \
  -H "Content-Type: application/json" \
  -d @src/main/resources/data/example_data_game.json \
  | jq '.'
```

El comando `jq '.'` formatea el JSON de respuesta para mejor legibilidad.

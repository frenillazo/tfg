#!/bin/bash

# Script para probar el sistema de recomendaciones

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}Sistema de Recomendaciones de Items${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""

# URL base de la API
BASE_URL="http://localhost:8080"

# Verificar si el servidor está corriendo
echo -e "${BLUE}[1/4] Verificando servidor...${NC}"
if curl -s "${BASE_URL}/api/recommendations/health" > /dev/null; then
    echo -e "${GREEN}✓ Servidor está activo${NC}"
else
    echo -e "${RED}✗ Servidor no está activo. Por favor inicia la aplicación con: ./mvnw spring-boot:run${NC}"
    exit 1
fi
echo ""

# Cargar datos si no están cargados
echo -e "${BLUE}[2/4] Cargando datos en la base de datos...${NC}"
echo "  - Cargando champions..."
curl -s -X POST "${BASE_URL}/api/batch/load-champions" > /dev/null
echo "  - Cargando items..."
curl -s -X POST "${BASE_URL}/api/batch/load-items" > /dev/null
echo "  - Cargando spells..."
curl -s -X POST "${BASE_URL}/api/batch/load-spells" > /dev/null
echo -e "${GREEN}✓ Datos cargados${NC}"
echo ""

# Verificar stats de datos
echo -e "${BLUE}[3/4] Verificando datos cargados...${NC}"
STATS=$(curl -s "${BASE_URL}/api/data/stats")
echo "$STATS" | jq '.'
echo ""

# Probar el sistema de recomendaciones
echo -e "${BLUE}[4/4] Obteniendo recomendaciones de items...${NC}"
echo ""

RESPONSE=$(curl -s -X POST "${BASE_URL}/api/recommendations/items" \
  -H "Content-Type: application/json" \
  -d @src/main/resources/data/example_data_game.json)

# Verificar si hay errores
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al obtener recomendaciones${NC}"
    exit 1
fi

# Mostrar resultados
echo -e "${GREEN}✓ Recomendaciones obtenidas exitosamente${NC}"
echo ""
echo -e "${BLUE}Resultados:${NC}"
echo "$RESPONSE" | jq '.'
echo ""

# Mostrar Top 3 items recomendados
echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}Top 3 Items Recomendados:${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""

for i in 0 1 2; do
    RANK=$(echo "$RESPONSE" | jq -r ".recommendations[$i].rank // \"N/A\"")
    if [ "$RANK" != "N/A" ]; then
        ITEM_NAME=$(echo "$RESPONSE" | jq -r ".recommendations[$i].itemName")
        FINAL_SCORE=$(echo "$RESPONSE" | jq -r ".recommendations[$i].finalScore")
        TOPSIS_SCORE=$(echo "$RESPONSE" | jq -r ".recommendations[$i].topsisScore")
        TODIM_SCORE=$(echo "$RESPONSE" | jq -r ".recommendations[$i].todimScore")
        GOLD=$(echo "$RESPONSE" | jq -r ".recommendations[$i].goldTotal")
        EXPLANATION=$(echo "$RESPONSE" | jq -r ".recommendations[$i].explanation")

        echo -e "${GREEN}#${RANK}: ${ITEM_NAME}${NC}"
        echo "   Score Final: ${FINAL_SCORE}"
        echo "   TOPSIS: ${TOPSIS_SCORE} | TODIM: ${TODIM_SCORE}"
        echo "   Costo: ${GOLD}g"
        echo "   ${EXPLANATION}"
        echo ""
    fi
done

# Mostrar análisis del campeón y enemigo
CHAMPION=$(echo "$RESPONSE" | jq -r '.championName')
PROFILE=$(echo "$RESPONSE" | jq -r '.championProfile')
AVG_ARMOR=$(echo "$RESPONSE" | jq -r '.enemyAnalysis.averageArmor')
AVG_MR=$(echo "$RESPONSE" | jq -r '.enemyAnalysis.averageMagicResist')
PHYS_DMG=$(echo "$RESPONSE" | jq -r '.enemyAnalysis.physicalDamageChampions')
MAG_DMG=$(echo "$RESPONSE" | jq -r '.enemyAnalysis.magicDamageChampions')
PROCESSING_TIME=$(echo "$RESPONSE" | jq -r '.processingTimeMs')

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}Análisis de la Partida:${NC}"
echo -e "${BLUE}======================================${NC}"
echo ""
echo "Campeón: ${CHAMPION}"
echo "Perfil: ${PROFILE}"
echo ""
echo "Equipo Enemigo:"
echo "  - Armor Promedio: ${AVG_ARMOR}"
echo "  - Magic Resist Promedio: ${AVG_MR}"
echo "  - Campeones de Daño Físico: ${PHYS_DMG}"
echo "  - Campeones de Daño Mágico: ${MAG_DMG}"
echo ""
echo "Tiempo de Procesamiento: ${PROCESSING_TIME}ms"
echo ""

echo -e "${GREEN}✓ Prueba completada exitosamente${NC}"

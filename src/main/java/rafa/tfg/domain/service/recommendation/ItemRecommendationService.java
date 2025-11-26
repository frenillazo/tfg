package rafa.tfg.domain.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.application.dto.recommendation.GameStateRequestDTO;
import rafa.tfg.application.dto.recommendation.ItemRecommendationResponseDTO;
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.model.recommendation.ChampionProfile;
import rafa.tfg.domain.model.recommendation.EnemyComposition;
import rafa.tfg.domain.model.recommendation.ItemCandidate;
import rafa.tfg.domain.model.recommendation.WeightProfile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal de recomendaciones de items
 * Orquesta todo el flujo del sistema de recomendaciones usando TOPSIS y TODIM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRecommendationService {

    private final ChampionAnalysisService championAnalysisService;
    private final EnemyAnalysisService enemyAnalysisService;
    private final ItemFilterService itemFilterService;
    private final CriteriaMatrixService criteriaMatrixService;
    private final TOPSISService topsisService;
    private final TODIMService todimService;

    // Pesos para combinación de algoritmos
    private static final double TOPSIS_WEIGHT = 0.70;
    private static final double TODIM_WEIGHT = 0.30;

    /**
     * Genera recomendaciones de items basadas en el estado del juego
     */
    public ItemRecommendationResponseDTO recommendItems(GameStateRequestDTO gameState) {
        long startTime = System.currentTimeMillis();

        log.info("Starting item recommendation process");

        // Paso 1: Extraer información del jugador activo
        GameStateRequestDTO.ActivePlayerDTO activePlayer = gameState.getActivePlayer();
        String activePlayerTeam = extractActivePlayerTeam(gameState);
        String championName = extractChampionName(gameState, activePlayer.getSummonerName());

        log.info("Active player: {} playing {}", activePlayer.getSummonerName(), championName);

        // Paso 2: Analizar perfil del campeón
        ChampionProfile championProfile = championAnalysisService.analyzeChampion(activePlayer, championName);

        // Paso 3: Analizar composición enemiga
        EnemyComposition enemyComposition = enemyAnalysisService.analyzeEnemyTeam(
                gameState.getAllPlayers(),
                activePlayerTeam);

        // Paso 4: Filtrar items candidatos
        List<Item> candidateItems = itemFilterService.filterCandidateItems(
                championProfile,
                extractActivePlayerItems(gameState),
                activePlayer.getCurrentGold());

        if (candidateItems.isEmpty()) {
            log.warn("No candidate items found");
            return buildEmptyResponse(championName, activePlayer.getLevel(), activePlayer.getCurrentGold(),
                    championProfile, enemyComposition, startTime);
        }

        log.info("Processing {} candidate items", candidateItems.size());

        // Paso 5: Construir matriz de criterios
        List<ItemCandidate> itemCandidates = criteriaMatrixService.buildCriteriaMatrix(candidateItems);

        // Paso 6: Calcular pesos dinámicos
        WeightProfile weights = criteriaMatrixService.calculateDynamicWeights(
                championProfile, enemyComposition);

        // Paso 7: Aplicar TOPSIS (normalización vectorial + distancias a ideales)
        itemCandidates = topsisService.applyTOPSIS(itemCandidates, weights);

        // Paso 8: Aplicar TODIM (normalización min-max + dominancia con aversión a pérdidas)
        // Necesitamos recalcular la matriz porque TOPSIS la modifica
        List<ItemCandidate> todimCandidates = criteriaMatrixService.buildCriteriaMatrix(candidateItems);
        todimCandidates = todimService.applyTODIM(todimCandidates, weights);

        // Copiar scores TODIM a los candidatos originales
        for (int i = 0; i < itemCandidates.size(); i++) {
            itemCandidates.get(i).setTodimScore(todimCandidates.get(i).getTodimScore());
        }

        // Paso 9: Combinar resultados (70% TOPSIS + 30% TODIM)
        calculateFinalScores(itemCandidates);

        // Paso 10: Ordenar por score final y tomar Top 5
        List<ItemCandidate> topCandidates = itemCandidates.stream()
                .sorted(Comparator.comparingDouble(ItemCandidate::getFinalScore).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Paso 11: Construir respuesta
        long processingTime = System.currentTimeMillis() - startTime;

        ItemRecommendationResponseDTO response = buildResponse(
                championName,
                activePlayer.getLevel(),
                activePlayer.getCurrentGold(),
                championProfile,
                enemyComposition,
                topCandidates,
                weights,
                processingTime);

        log.info("Item recommendation process completed in {}ms. Top recommendation: {}",
                processingTime,
                topCandidates.isEmpty() ? "none" : topCandidates.get(0).getItemName());

        return response;
    }

    /**
     * Calcula scores finales combinando TOPSIS y TODIM
     */
    private void calculateFinalScores(List<ItemCandidate> candidates) {
        for (ItemCandidate candidate : candidates) {
            double topsisScore = candidate.getTopsisScore() != null ? candidate.getTopsisScore() : 0.0;
            double todimScore = candidate.getTodimScore() != null ? candidate.getTodimScore() : 0.0;

            double finalScore = (TOPSIS_WEIGHT * topsisScore) + (TODIM_WEIGHT * todimScore);

            candidate.setFinalScore(finalScore);
        }
    }

    /**
     * Extrae el equipo del jugador activo
     */
    private String extractActivePlayerTeam(GameStateRequestDTO gameState) {
        String summonerName = gameState.getActivePlayer().getSummonerName();

        return gameState.getAllPlayers().stream()
                .filter(p -> p.getSummonerName().equals(summonerName))
                .map(GameStateRequestDTO.PlayerDTO::getTeam)
                .findFirst()
                .orElse("ORDER");
    }

    /**
     * Extrae el nombre del campeón del jugador activo
     */
    private String extractChampionName(GameStateRequestDTO gameState, String summonerName) {
        return gameState.getAllPlayers().stream()
                .filter(p -> p.getSummonerName().equals(summonerName))
                .map(GameStateRequestDTO.PlayerDTO::getChampionName)
                .findFirst()
                .orElse("Unknown");
    }

    /**
     * Extrae los items actuales del jugador activo
     */
    private List<GameStateRequestDTO.ItemDTO> extractActivePlayerItems(GameStateRequestDTO gameState) {
        String summonerName = gameState.getActivePlayer().getSummonerName();

        return gameState.getAllPlayers().stream()
                .filter(p -> p.getSummonerName().equals(summonerName))
                .map(GameStateRequestDTO.PlayerDTO::getItems)
                .findFirst()
                .orElse(Collections.emptyList());
    }

    /**
     * Construye la respuesta con las recomendaciones
     */
    private ItemRecommendationResponseDTO buildResponse(
            String championName,
            Integer level,
            Double currentGold,
            ChampionProfile championProfile,
            EnemyComposition enemyComposition,
            List<ItemCandidate> topCandidates,
            WeightProfile weights,
            long processingTime) {

        // Convertir enemyComposition a DTO
        ItemRecommendationResponseDTO.EnemyAnalysisDTO enemyAnalysisDTO =
                ItemRecommendationResponseDTO.EnemyAnalysisDTO.builder()
                        .averageArmor(enemyComposition.getAverageArmor())
                        .averageMagicResist(enemyComposition.getAverageMagicResist())
                        .physicalDamageChampions(enemyComposition.getPhysicalDamageChampions())
                        .magicDamageChampions(enemyComposition.getMagicDamageChampions())
                        .mixedDamageChampions(enemyComposition.getMixedDamageChampions())
                        .ccChampions(enemyComposition.getChampionsWithHardCC())
                        .enemyChampions(enemyComposition.getEnemyChampionNames())
                        .build();

        // Convertir candidatos a DTOs
        List<ItemRecommendationResponseDTO.RecommendedItemDTO> recommendations = new ArrayList<>();
        int rank = 1;

        for (ItemCandidate candidate : topCandidates) {
            String explanation = generateExplanation(candidate, championProfile, enemyComposition, weights);

            ItemRecommendationResponseDTO.RecommendedItemDTO itemDTO =
                    ItemRecommendationResponseDTO.RecommendedItemDTO.builder()
                            .rank(rank++)
                            .itemId(candidate.getItemId())
                            .itemName(candidate.getItemName())
                            .finalScore(candidate.getFinalScore())
                            .topsisScore(candidate.getTopsisScore())
                            .todimScore(candidate.getTodimScore())
                            .goldTotal(candidate.getItem().getGoldTotal().doubleValue())
                            .purchasable(candidate.getPurchasable())
                            .criteriaScores(candidate.getCriteria())
                            .explanation(explanation)
                            .build();

            recommendations.add(itemDTO);
        }

        return ItemRecommendationResponseDTO.builder()
                .championName(championName)
                .championLevel(level)
                .currentGold(currentGold)
                .championProfile(championProfile.getScalingType().toString())
                .enemyAnalysis(enemyAnalysisDTO)
                .recommendations(recommendations)
                .processingTimeMs(processingTime)
                .build();
    }

    /**
     * Genera explicación de por qué se recomienda un item
     */
    private String generateExplanation(
            ItemCandidate candidate,
            ChampionProfile championProfile,
            EnemyComposition enemyComposition,
            WeightProfile weights) {

        StringBuilder explanation = new StringBuilder();

        // Razón principal basada en el perfil del campeón
        explanation.append(String.format("Recommended for %s champions. ",
                championProfile.getScalingType().toString().toLowerCase().replace("_", " ")));

        // Top 3 stats del item
        List<Map.Entry<String, Double>> topStats = candidate.getCriteria().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .filter(e -> e.getValue() > 0)
                .collect(Collectors.toList());

        if (!topStats.isEmpty()) {
            explanation.append("Provides: ");
            List<String> statDescriptions = topStats.stream()
                    .map(e -> String.format("%s (%.1f)", formatCriterionName(e.getKey()), e.getValue()))
                    .collect(Collectors.toList());
            explanation.append(String.join(", ", statDescriptions));
            explanation.append(". ");
        }

        // Contexto enemigo
        if (enemyComposition.getPhysicalThreat() > 0.6) {
            explanation.append("Armor recommended against physical damage. ");
        }
        if (enemyComposition.getMagicalThreat() > 0.6) {
            explanation.append("Magic resist recommended against magic damage. ");
        }

        // Gold efficiency
        explanation.append(String.format("Gold efficiency: %.1f%%.", candidate.getGoldEfficiency()));

        return explanation.toString();
    }

    /**
     * Formatea nombres de criterios para mostrar
     */
    private String formatCriterionName(String key) {
        return key.replaceAll("([A-Z])", " $1")
                .toLowerCase()
                .trim()
                .substring(0, 1).toUpperCase() +
                key.replaceAll("([A-Z])", " $1")
                        .toLowerCase()
                        .trim()
                        .substring(1);
    }

    /**
     * Construye respuesta vacía cuando no hay candidatos
     */
    private ItemRecommendationResponseDTO buildEmptyResponse(
            String championName,
            Integer level,
            Double currentGold,
            ChampionProfile championProfile,
            EnemyComposition enemyComposition,
            long startTime) {

        ItemRecommendationResponseDTO.EnemyAnalysisDTO enemyAnalysisDTO =
                ItemRecommendationResponseDTO.EnemyAnalysisDTO.builder()
                        .averageArmor(enemyComposition.getAverageArmor())
                        .averageMagicResist(enemyComposition.getAverageMagicResist())
                        .physicalDamageChampions(enemyComposition.getPhysicalDamageChampions())
                        .magicDamageChampions(enemyComposition.getMagicDamageChampions())
                        .mixedDamageChampions(enemyComposition.getMixedDamageChampions())
                        .ccChampions(enemyComposition.getChampionsWithHardCC())
                        .enemyChampions(enemyComposition.getEnemyChampionNames())
                        .build();

        return ItemRecommendationResponseDTO.builder()
                .championName(championName)
                .championLevel(level)
                .currentGold(currentGold)
                .championProfile(championProfile.getScalingType().toString())
                .enemyAnalysis(enemyAnalysisDTO)
                .recommendations(Collections.emptyList())
                .processingTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }
}

package rafa.tfg.application.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO de respuesta con las recomendaciones de items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRecommendationResponseDTO {

    private String championName;
    private Integer championLevel;
    private Double currentGold;
    private String championProfile;
    private EnemyAnalysisDTO enemyAnalysis;
    private List<RecommendedItemDTO> recommendations;
    private Long processingTimeMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnemyAnalysisDTO {
        private Double averageArmor;
        private Double averageMagicResist;
        private Integer physicalDamageChampions;
        private Integer magicDamageChampions;
        private Integer mixedDamageChampions;
        private Integer ccChampions;
        private List<String> enemyChampions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedItemDTO {
        private Integer rank;
        private String itemId;
        private String itemName;
        private Double finalScore;
        private Double topsisScore;
        private Double todimScore;
        private Double goldTotal;
        private Boolean purchasable;
        private Map<String, Double> criteriaScores;
        private String explanation;
    }
}

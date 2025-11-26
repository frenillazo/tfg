package rafa.tfg.domain.model.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Perfil de escalado de un campeón basado en sus habilidades
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionProfile {

    private String championName;
    private String championId;
    private Integer championLevel;

    // Ratios de escalado
    private Double totalAdRatio;
    private Double totalApRatio;
    private Double totalBonusAdRatio;
    private Double totalHealthRatio;
    private Double totalArmorRatio;
    private Double totalMrRatio;

    // Contadores de habilidades
    private Integer abilitiesWithAdScaling;
    private Integer abilitiesWithApScaling;
    private Integer totalAbilities;

    // Etiquetas extraídas de levelTipLabels
    private List<String> abilityTags;

    // Perfil calculado
    private ChampionScalingType scalingType;

    // Stats actuales del jugador
    private Double currentAd;
    private Double currentAp;
    private Double currentAttackSpeed;
    private Double currentCdr;

    public enum ChampionScalingType {
        AD_FOCUSED,      // >70% AD scaling
        AP_FOCUSED,      // >70% AP scaling
        MIXED,           // Balance entre AD y AP
        TANK,            // Escala con HP/Armor/MR
        UTILITY          // Cooldown/Support focused
    }

    /**
     * Determina el tipo de escalado dominante
     */
    public ChampionScalingType determineScalingType() {
        double totalScaling = currentAd + currentAp;

        if (totalScaling == 0) {
            return ChampionScalingType.UTILITY;
        }

        double adPercentage = currentAd / totalScaling;
        double apPercentage = currentAp / totalScaling;

        if (adPercentage > 0.6) {
            return ChampionScalingType.AD_FOCUSED;
        } else if (apPercentage > 0.6) {
            return ChampionScalingType.AP_FOCUSED;
        } else if (totalHealthRatio + totalArmorRatio + totalMrRatio > totalScaling) {
            return ChampionScalingType.TANK;
        } else if (adPercentage > 0.3 && apPercentage > 0.3) {
            return ChampionScalingType.MIXED;
        } else {
            return ChampionScalingType.UTILITY;
        }
    }
}

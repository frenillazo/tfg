package rafa.tfg.domain.model.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Análisis de la composición del equipo enemigo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnemyComposition {

    private List<String> enemyChampionNames;
    private Integer enemyTeamSize;

    // Promedios de resistencias
    private Double averageArmor;
    private Double averageMagicResist;
    private Double averageHealth;

    // Contadores de tipos de daño
    private Integer physicalDamageChampions;
    private Integer magicDamageChampions;
    private Integer mixedDamageChampions;

    // Contadores de CC
    private Integer championsWithHardCC;
    private Integer championsWithSlows;
    private Integer totalCCAbilities;

    // Threat analysis
    private Double physicalThreat;  // 0-1 scale
    private Double magicalThreat;   // 0-1 scale
    private Double ccThreat;        // 0-1 scale

    /**
     * Calcula el nivel de amenaza física (0-1)
     */
    public Double calculatePhysicalThreat() {
        if (enemyTeamSize == 0) return 0.0;
        return physicalDamageChampions.doubleValue() / enemyTeamSize;
    }

    /**
     * Calcula el nivel de amenaza mágica (0-1)
     */
    public Double calculateMagicalThreat() {
        if (enemyTeamSize == 0) return 0.0;
        return magicDamageChampions.doubleValue() / enemyTeamSize;
    }

    /**
     * Calcula el nivel de amenaza de CC (0-1)
     */
    public Double calculateCCThreat() {
        if (enemyTeamSize == 0) return 0.0;
        return Math.min(1.0, championsWithHardCC.doubleValue() / enemyTeamSize);
    }
}

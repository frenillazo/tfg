package rafa.tfg.domain.model.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rafa.tfg.domain.model.Item;

import java.util.Map;

/**
 * Representa un item candidato con sus criterios evaluados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCandidate {

    private Item item;
    private String itemId;
    private String itemName;

    // Matriz de criterios (12 criterios)
    private Map<String, Double> criteria;

    // Scores de los algoritmos
    private Double topsisScore;
    private Double todimScore;
    private Double finalScore;

    // Información adicional
    private Double goldEfficiency;
    private Integer buildDepth;
    private Boolean purchasable;

    /**
     * Nombres de los 12 criterios usados en la matriz de decisión
     */
    public enum Criterion {
        ATTACK_DAMAGE("attackDamage", 35.0),           // Gold value per point
        ABILITY_POWER("abilityPower", 21.75),
        ATTACK_SPEED("attackSpeed", 25.0),             // Per 1% AS
        CRITICAL_CHANCE("criticalChance", 40.0),       // Per 1% crit
        ARMOR("armor", 20.0),
        MAGIC_RESIST("magicResist", 18.0),
        HEALTH("health", 2.67),                        // Per HP
        COOLDOWN_REDUCTION("cooldownReduction", 26.67), // Per 1% CDR (now Ability Haste)
        ARMOR_PENETRATION("armorPenetration", 30.0),   // Lethality value
        MAGIC_PENETRATION("magicPenetration", 31.11),
        LIFE_STEAL("lifeSteal", 27.5),                 // Per 1% LS
        MOVEMENT_SPEED("movementSpeed", 12.0);         // Per 1% MS

        private final String key;
        private final Double goldValue;

        Criterion(String key, Double goldValue) {
            this.key = key;
            this.goldValue = goldValue;
        }

        public String getKey() {
            return key;
        }

        public Double getGoldValue() {
            return goldValue;
        }

        public static Criterion[] getAllCriteria() {
            return values();
        }
    }
}

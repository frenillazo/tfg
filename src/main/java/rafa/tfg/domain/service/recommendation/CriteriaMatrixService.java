package rafa.tfg.domain.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.model.recommendation.ChampionProfile;
import rafa.tfg.domain.model.recommendation.EnemyComposition;
import rafa.tfg.domain.model.recommendation.ItemCandidate;
import rafa.tfg.domain.model.recommendation.WeightProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para construir la matriz de criterios y calcular pesos dinámicos
 */
@Service
@Slf4j
public class CriteriaMatrixService {

    /**
     * Construye la matriz de criterios para todos los items candidatos
     */
    public List<ItemCandidate> buildCriteriaMatrix(List<Item> items) {
        log.info("Building criteria matrix for {} items", items.size());

        return items.stream()
                .map(this::buildItemCandidate)
                .collect(Collectors.toList());
    }

    /**
     * Construye un candidato con sus criterios evaluados
     */
    private ItemCandidate buildItemCandidate(Item item) {
        Map<String, Double> criteria = new HashMap<>();

        // Extraer los 12 criterios del item
        criteria.put("attackDamage", getDoubleValue(item.getFlatPhysicalDamageMod()));
        criteria.put("abilityPower", getDoubleValue(item.getFlatMagicDamageMod()));
        criteria.put("attackSpeed", getDoubleValue(item.getPercentAttackSpeedMod()) * 100); // Convertir a %
        criteria.put("criticalChance", getDoubleValue(item.getFlatCritChanceMod()) * 100); // Convertir a %
        criteria.put("armor", getDoubleValue(item.getFlatArmorMod()));
        criteria.put("magicResist", getDoubleValue(item.getFlatSpellBlockMod()));
        criteria.put("health", getDoubleValue(item.getFlatHPPoolMod()));
        criteria.put("cooldownReduction", 0.0); // El CDR ahora es Ability Haste, necesitamos mapear
        criteria.put("armorPenetration", getDoubleValue(item.getPercentArmorMod()));
        criteria.put("magicPenetration", getDoubleValue(item.getPercentMPPoolMod()));
        criteria.put("lifeSteal", getDoubleValue(item.getPercentLifeStealMod()) * 100); // Convertir a %
        criteria.put("movementSpeed", calculateMovementSpeed(item));

        // Calcular Gold Efficiency
        double goldEfficiency = calculateGoldEfficiency(criteria, item.getGoldTotal());

        return ItemCandidate.builder()
                .item(item)
                .itemId(item.getItemId())
                .itemName(item.getName())
                .criteria(criteria)
                .goldEfficiency(goldEfficiency)
                .buildDepth(item.getDepth())
                .purchasable(item.getPurchasable())
                .build();
    }

    /**
     * Calcula la Gold Efficiency del item
     */
    private double calculateGoldEfficiency(Map<String, Double> criteria, Integer goldTotal) {
        if (goldTotal == null || goldTotal == 0) {
            return 0.0;
        }

        double totalGoldValue = 0.0;

        // Sumar el valor de oro de cada stat según ItemCandidate.Criterion
        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            String key = criterion.getKey();
            Double value = criteria.getOrDefault(key, 0.0);
            Double goldValue = criterion.getGoldValue();

            // Para porcentajes (AS, Crit, LS, MS), el valor ya está en %
            if (key.equals("attackSpeed") || key.equals("criticalChance") || key.equals("lifeSteal")) {
                totalGoldValue += (value * goldValue);
            } else if (key.equals("movementSpeed")) {
                // Movement speed se calcula diferente (valor total, no %)
                totalGoldValue += (value * goldValue);
            } else {
                totalGoldValue += (value * goldValue);
            }
        }

        // Gold Efficiency = (Valor total de stats / Costo de oro) * 100
        return (totalGoldValue / goldTotal) * 100.0;
    }

    /**
     * Calcula el movement speed total (flat + percent)
     */
    private double calculateMovementSpeed(Item item) {
        double flatMS = getDoubleValue(item.getFlatMovementSpeedMod());
        double percentMS = getDoubleValue(item.getFlatMovementSpeedMod()) * 100;
        return flatMS + percentMS;
    }

    /**
     * Calcula los pesos dinámicos según el perfil del campeón y composición enemiga
     */
    public WeightProfile calculateDynamicWeights(
            ChampionProfile championProfile,
            EnemyComposition enemyComposition) {

        log.info("Calculating dynamic weights for champion type: {} vs enemy threats: phys={}, mag={}, cc={}",
                championProfile.getScalingType(),
                enemyComposition.getPhysicalThreat(),
                enemyComposition.getMagicalThreat(),
                enemyComposition.getCcThreat());

        Map<String, Double> weights = new HashMap<>();

        // Inicializar pesos base (todos iguales)
        for (ItemCandidate.Criterion criterion : ItemCandidate.Criterion.values()) {
            weights.put(criterion.getKey(), 1.0);
        }

        // Ajustar pesos según el tipo de campeón
        adjustWeightsByChampionType(weights, championProfile);

        // Ajustar pesos según la composición enemiga
        adjustWeightsByEnemyComposition(weights, enemyComposition);

        // Ajustar pesos según las etiquetas de habilidades
        adjustWeightsByAbilityTags(weights, championProfile.getAbilityTags());

        // Crear y normalizar el perfil de pesos
        WeightProfile weightProfile = WeightProfile.builder()
                .weights(weights)
                .build();

        weightProfile.normalize();

        log.debug("Final normalized weights: {}", weights);

        return weightProfile;
    }

    /**
     * Ajusta pesos según el tipo de escalado del campeón
     */
    private void adjustWeightsByChampionType(Map<String, Double> weights, ChampionProfile profile) {
        switch (profile.getScalingType()) {
            case AD_FOCUSED:
                weights.put("attackDamage", weights.get("attackDamage") + 5.0);
                weights.put("attackSpeed", weights.get("attackSpeed") + 3.0);
                weights.put("criticalChance", weights.get("criticalChance") + 3.0);
                weights.put("armorPenetration", weights.get("armorPenetration") + 2.0);
                weights.put("lifeSteal", weights.get("lifeSteal") + 2.0);
                break;

            case AP_FOCUSED:
                weights.put("abilityPower", weights.get("abilityPower") + 5.0);
                weights.put("magicPenetration", weights.get("magicPenetration") + 3.0);
                weights.put("cooldownReduction", weights.get("cooldownReduction") + 3.0);
                break;

            case TANK:
                weights.put("health", weights.get("health") + 5.0);
                weights.put("armor", weights.get("armor") + 4.0);
                weights.put("magicResist", weights.get("magicResist") + 4.0);
                break;

            case MIXED:
                weights.put("attackDamage", weights.get("attackDamage") + 3.0);
                weights.put("abilityPower", weights.get("abilityPower") + 3.0);
                weights.put("health", weights.get("health") + 2.0);
                break;

            case UTILITY:
                weights.put("cooldownReduction", weights.get("cooldownReduction") + 4.0);
                weights.put("movementSpeed", weights.get("movementSpeed") + 3.0);
                weights.put("health", weights.get("health") + 2.0);
                break;
        }

        // Ajustar según ratios específicos
        if (profile.getTotalAdRatio() > 3.0) {
            weights.put("attackDamage", weights.get("attackDamage") + 2.0);
        }
        if (profile.getTotalApRatio() > 3.0) {
            weights.put("abilityPower", weights.get("abilityPower") + 2.0);
        }
    }

    /**
     * Ajusta pesos según la composición enemiga
     */
    private void adjustWeightsByEnemyComposition(Map<String, Double> weights, EnemyComposition composition) {
        // Si el enemigo tiene mucha armadura, priorizar penetración de armadura
        if (composition.getAverageArmor() > 80) {
            weights.put("armorPenetration", weights.get("armorPenetration") + 3.0);
        }

        // Si el enemigo tiene mucha MR, priorizar penetración mágica
        if (composition.getAverageMagicResist() > 60) {
            weights.put("magicPenetration", weights.get("magicPenetration") + 3.0);
        }

        // Si el enemigo hace daño físico, priorizar armadura
        if (composition.getPhysicalThreat() > 0.6) {
            weights.put("armor", weights.get("armor") + 3.0);
        }

        // Si el enemigo hace daño mágico, priorizar MR
        if (composition.getMagicalThreat() > 0.6) {
            weights.put("magicResist", weights.get("magicResist") + 3.0);
        }

        // Si el enemigo tiene mucho CC, priorizar salud y resistencias
        if (composition.getCcThreat() > 0.6) {
            weights.put("health", weights.get("health") + 2.0);
            weights.put("magicResist", weights.get("magicResist") + 1.5);
        }

        // Amenazas balanceadas
        if (composition.getPhysicalThreat() > 0.4 && composition.getMagicalThreat() > 0.4) {
            weights.put("health", weights.get("health") + 2.0);
        }
    }

    /**
     * Ajusta pesos según las etiquetas de habilidades
     */
    private void adjustWeightsByAbilityTags(Map<String, Double> weights, List<String> abilityTags) {
        if (abilityTags == null || abilityTags.isEmpty()) {
            return;
        }

        for (String tag : abilityTags) {
            String lowerTag = tag.toLowerCase();

            if (lowerTag.contains("damage") || lowerTag.contains("ad ratio")) {
                weights.put("attackDamage", weights.get("attackDamage") + 1.0);
            }
            if (lowerTag.contains("ap ratio") || lowerTag.contains("magic")) {
                weights.put("abilityPower", weights.get("abilityPower") + 1.0);
            }
            if (lowerTag.contains("cooldown") || lowerTag.contains("cdr")) {
                weights.put("cooldownReduction", weights.get("cooldownReduction") + 1.5);
            }
            if (lowerTag.contains("attack speed")) {
                weights.put("attackSpeed", weights.get("attackSpeed") + 1.0);
            }
            if (lowerTag.contains("armor") && !lowerTag.contains("penetration")) {
                weights.put("armor", weights.get("armor") + 1.0);
            }
            if (lowerTag.contains("health") || lowerTag.contains("hp")) {
                weights.put("health", weights.get("health") + 1.0);
            }
        }
    }

    /**
     * Obtiene valor Double de un objeto, retorna 0.0 si es null
     */
    private double getDoubleValue(Double value) {
        return value != null ? value : 0.0;
    }

    private double getDoubleValue(Integer value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}

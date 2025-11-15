package rafa.tfg.domain.service.recommendation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.application.dto.recommendation.GameStateRequestDTO;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.domain.model.recommendation.ChampionProfile;
import rafa.tfg.domain.port.SpellRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para analizar el perfil de escalado de un campeón
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChampionAnalysisService {

    private final SpellRepository spellRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Analiza el perfil de escalado del campeón activo
     */
    public ChampionProfile analyzeChampion(GameStateRequestDTO.ActivePlayerDTO activePlayer, String championName) {
        log.info("Analyzing champion profile for: {}", championName);

        // Obtener habilidades del campeón desde la BD
        List<Spell> championSpells = spellRepository.findByChampionId(championName);

        if (championSpells.isEmpty()) {
            log.warn("No spells found for champion: {}", championName);
            return createDefaultProfile(activePlayer, championName);
        }

        // Analizar escalados de las habilidades
        ChampionProfile.ChampionProfileBuilder profileBuilder = ChampionProfile.builder()
                .championName(championName)
                .championId(championName)
                .championLevel(activePlayer.getLevel())
                .currentAd(activePlayer.getChampionStats().getAttackDamage())
                .currentAp(activePlayer.getChampionStats().getAbilityPower())
                .currentAttackSpeed(activePlayer.getChampionStats().getAttackSpeed())
                .currentCdr(activePlayer.getChampionStats().getCooldownReduction());

        double totalAdRatio = 0.0;
        double totalApRatio = 0.0;
        double totalBonusAdRatio = 0.0;
        double totalHealthRatio = 0.0;
        double totalArmorRatio = 0.0;
        double totalMrRatio = 0.0;

        int abilitiesWithAd = 0;
        int abilitiesWithAp = 0;
        List<String> allTags = new ArrayList<>();

        for (Spell spell : championSpells) {
            // Extraer tags de levelTipLabels
            List<String> levelTipLabels = parseLevelTipLabels(spell.getLevelTipLabels());
            allTags.addAll(levelTipLabels);

            // Analizar vars para detectar escalados
            Map<String, Object> scalingRatios = extractScalingRatios(spell.getVars());

            if (scalingRatios.containsKey("totalad") || scalingRatios.containsKey("attackdamage")) {
                double adRatio = getScalingValue(scalingRatios, Arrays.asList("totalad", "attackdamage"));
                totalAdRatio += adRatio;
                if (adRatio > 0) abilitiesWithAd++;
            }

            if (scalingRatios.containsKey("bonusad")) {
                totalBonusAdRatio += getScalingValue(scalingRatios, Collections.singletonList("bonusad"));
            }

            if (scalingRatios.containsKey("spelldamage") || scalingRatios.containsKey("ap")) {
                double apRatio = getScalingValue(scalingRatios, Arrays.asList("spelldamage", "ap"));
                totalApRatio += apRatio;
                if (apRatio > 0) abilitiesWithAp++;
            }

            if (scalingRatios.containsKey("health") || scalingRatios.containsKey("bonushealth")) {
                totalHealthRatio += getScalingValue(scalingRatios, Arrays.asList("health", "bonushealth"));
            }

            if (scalingRatios.containsKey("armor") || scalingRatios.containsKey("bonusarmor")) {
                totalArmorRatio += getScalingValue(scalingRatios, Arrays.asList("armor", "bonusarmor"));
            }

            if (scalingRatios.containsKey("mr") || scalingRatios.containsKey("bonusmr")) {
                totalMrRatio += getScalingValue(scalingRatios, Arrays.asList("mr", "bonusmr"));
            }
        }

        // Construir perfil
        ChampionProfile profile = profileBuilder
                .totalAdRatio(totalAdRatio)
                .totalApRatio(totalApRatio)
                .totalBonusAdRatio(totalBonusAdRatio)
                .totalHealthRatio(totalHealthRatio)
                .totalArmorRatio(totalArmorRatio)
                .totalMrRatio(totalMrRatio)
                .abilitiesWithAdScaling(abilitiesWithAd)
                .abilitiesWithApScaling(abilitiesWithAp)
                .totalAbilities(championSpells.size())
                .abilityTags(allTags.stream().distinct().collect(Collectors.toList()))
                .build();

        // Determinar tipo de escalado
        profile.setScalingType(profile.determineScalingType());

        log.info("Champion profile created: scalingType={}, adRatio={}, apRatio={}",
                profile.getScalingType(), totalAdRatio, totalApRatio);

        return profile;
    }

    /**
     * Parsea levelTipLabels desde JSON string a lista
     */
    private List<String> parseLevelTipLabels(String levelTipLabelsJson) {
        if (levelTipLabelsJson == null || levelTipLabelsJson.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(levelTipLabelsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse levelTipLabels: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Extrae ratios de escalado desde el campo vars (JSON)
     */
    private Map<String, Object> extractScalingRatios(String varsJson) {
        if (varsJson == null || varsJson.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            List<Map<String, Object>> vars = objectMapper.readValue(varsJson, new TypeReference<List<Map<String, Object>>>() {});
            Map<String, Object> result = new HashMap<>();

            for (Map<String, Object> var : vars) {
                String link = (String) var.get("link");
                Object coeff = var.get("coeff");

                if (link != null && coeff != null) {
                    // Normalizar el link a lowercase para comparación
                    String normalizedLink = link.toLowerCase();

                    // Extraer el valor numérico
                    double coeffValue = 0.0;
                    if (coeff instanceof Number) {
                        coeffValue = ((Number) coeff).doubleValue();
                    } else if (coeff instanceof List) {
                        List<?> coeffList = (List<?>) coeff;
                        if (!coeffList.isEmpty() && coeffList.get(0) instanceof Number) {
                            coeffValue = ((Number) coeffList.get(0)).doubleValue();
                        }
                    }

                    result.put(normalizedLink, coeffValue);
                }
            }

            return result;
        } catch (Exception e) {
            log.warn("Failed to parse vars JSON: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * Obtiene valor de escalado probando múltiples keys
     */
    private double getScalingValue(Map<String, Object> scalingRatios, List<String> possibleKeys) {
        for (String key : possibleKeys) {
            Object value = scalingRatios.get(key.toLowerCase());
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        return 0.0;
    }

    /**
     * Crea un perfil por defecto cuando no hay datos de habilidades
     */
    private ChampionProfile createDefaultProfile(GameStateRequestDTO.ActivePlayerDTO activePlayer, String championName) {
        return ChampionProfile.builder()
                .championName(championName)
                .championId(championName)
                .championLevel(activePlayer.getLevel())
                .currentAd(activePlayer.getChampionStats().getAttackDamage())
                .currentAp(activePlayer.getChampionStats().getAbilityPower())
                .currentAttackSpeed(activePlayer.getChampionStats().getAttackSpeed())
                .currentCdr(activePlayer.getChampionStats().getCooldownReduction())
                .totalAdRatio(0.0)
                .totalApRatio(0.0)
                .totalBonusAdRatio(0.0)
                .totalHealthRatio(0.0)
                .totalArmorRatio(0.0)
                .totalMrRatio(0.0)
                .abilitiesWithAdScaling(0)
                .abilitiesWithApScaling(0)
                .totalAbilities(0)
                .abilityTags(Collections.emptyList())
                .scalingType(ChampionProfile.ChampionScalingType.UTILITY)
                .build();
    }
}

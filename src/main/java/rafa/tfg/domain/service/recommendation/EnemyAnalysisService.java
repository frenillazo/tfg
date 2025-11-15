package rafa.tfg.domain.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rafa.tfg.application.dto.recommendation.GameStateRequestDTO;
import rafa.tfg.domain.model.Champion;
import rafa.tfg.domain.model.Spell;
import rafa.tfg.domain.model.recommendation.EnemyComposition;
import rafa.tfg.domain.port.ChampionRepository;
import rafa.tfg.domain.port.SpellRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para analizar la composición del equipo enemigo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnemyAnalysisService {

    private final ChampionRepository championRepository;
    private final SpellRepository spellRepository;

    // Stats base promedio de campeones nivel 1-18
    private static final double BASE_ARMOR_LV1 = 30.0;
    private static final double BASE_MR_LV1 = 30.0;
    private static final double BASE_HP_LV1 = 600.0;
    private static final double ARMOR_PER_LEVEL = 4.0;
    private static final double MR_PER_LEVEL = 1.25;
    private static final double HP_PER_LEVEL = 95.0;

    /**
     * Analiza la composición del equipo enemigo
     */
    public EnemyComposition analyzeEnemyTeam(List<GameStateRequestDTO.PlayerDTO> allPlayers, String activePlayerTeam) {
        log.info("Analyzing enemy team composition. Active player team: {}", activePlayerTeam);

        // Filtrar jugadores enemigos
        List<GameStateRequestDTO.PlayerDTO> enemies = allPlayers.stream()
                .filter(player -> !player.getTeam().equals(activePlayerTeam))
                .collect(Collectors.toList());

        if (enemies.isEmpty()) {
            log.warn("No enemy players found");
            return createDefaultComposition();
        }

        log.info("Found {} enemy champions", enemies.size());

        // Inicializar contadores
        double totalArmor = 0.0;
        double totalMr = 0.0;
        double totalHp = 0.0;
        int physicalDmgCount = 0;
        int magicDmgCount = 0;
        int mixedDmgCount = 0;
        int hardCCCount = 0;
        int slowsCount = 0;
        int totalCCAbilities = 0;

        List<String> enemyChampionNames = enemies.stream()
                .map(GameStateRequestDTO.PlayerDTO::getChampionName)
                .collect(Collectors.toList());

        // Analizar cada campeón enemigo
        for (GameStateRequestDTO.PlayerDTO enemy : enemies) {
            String championName = enemy.getChampionName();
            int level = enemy.getLevel();

            // Obtener stats del campeón desde la BD
            Champion champion = championRepository.findByChampionId(championName).orElse(null);

            if (champion != null) {
                // Calcular stats estimadas basadas en el nivel
                double estimatedArmor = champion.getArmor() + (champion.getArmorPerLevel() * (level - 1));
                double estimatedMr = champion.getSpellBlock() + (champion.getSpellBlockPerLevel() * (level - 1));
                double estimatedHp = champion.getHp() + (champion.getHpPerLevel() * (level - 1));

                totalArmor += estimatedArmor;
                totalMr += estimatedMr;
                totalHp += estimatedHp;

                log.debug("Champion {}: Armor={}, MR={}, HP={}", championName, estimatedArmor, estimatedMr, estimatedHp);
            } else {
                // Usar valores base si no se encuentra el campeón
                totalArmor += BASE_ARMOR_LV1 + (ARMOR_PER_LEVEL * (level - 1));
                totalMr += BASE_MR_LV1 + (MR_PER_LEVEL * (level - 1));
                totalHp += BASE_HP_LV1 + (HP_PER_LEVEL * (level - 1));
                log.warn("Champion {} not found in database, using default stats", championName);
            }

            // Analizar tipo de daño y CC del campeón
            DamageProfile damageProfile = analyzeDamageType(championName);
            CCProfile ccProfile = analyzeCCCapabilities(championName);

            switch (damageProfile) {
                case PHYSICAL:
                    physicalDmgCount++;
                    break;
                case MAGICAL:
                    magicDmgCount++;
                    break;
                case MIXED:
                    mixedDmgCount++;
                    break;
            }

            if (ccProfile.hasHardCC) {
                hardCCCount++;
            }
            if (ccProfile.hasSlows) {
                slowsCount++;
            }
            totalCCAbilities += ccProfile.ccAbilityCount;
        }

        // Calcular promedios
        int enemyCount = enemies.size();
        double avgArmor = totalArmor / enemyCount;
        double avgMr = totalMr / enemyCount;
        double avgHp = totalHp / enemyCount;

        // Construir composición
        EnemyComposition composition = EnemyComposition.builder()
                .enemyChampionNames(enemyChampionNames)
                .enemyTeamSize(enemyCount)
                .averageArmor(avgArmor)
                .averageMagicResist(avgMr)
                .averageHealth(avgHp)
                .physicalDamageChampions(physicalDmgCount)
                .magicDamageChampions(magicDmgCount)
                .mixedDamageChampions(mixedDmgCount)
                .championsWithHardCC(hardCCCount)
                .championsWithSlows(slowsCount)
                .totalCCAbilities(totalCCAbilities)
                .build();

        composition.setPhysicalThreat(composition.calculatePhysicalThreat());
        composition.setMagicalThreat(composition.calculateMagicalThreat());
        composition.setCcThreat(composition.calculateCCThreat());

        log.info("Enemy composition: avgArmor={}, avgMR={}, phyDmg={}, magDmg={}, mixed={}, hardCC={}",
                avgArmor, avgMr, physicalDmgCount, magicDmgCount, mixedDmgCount, hardCCCount);

        return composition;
    }

    /**
     * Analiza el tipo de daño principal del campeón
     */
    private DamageProfile analyzeDamageType(String championName) {
        List<Spell> spells = spellRepository.findByChampionId(championName);

        if (spells.isEmpty()) {
            // Si no hay datos, usar heurística basada en tags del campeón
            Champion champion = championRepository.findByChampionId(championName).orElse(null);
            if (champion != null) {
                String tags = champion.getTags();
                if (tags != null) {
                    if (tags.contains("Mage") || tags.contains("Support")) {
                        return DamageProfile.MAGICAL;
                    } else if (tags.contains("Marksman") || tags.contains("Assassin") || tags.contains("Fighter")) {
                        return DamageProfile.PHYSICAL;
                    }
                }
            }
            return DamageProfile.PHYSICAL; // Default
        }

        // Analizar escalados de habilidades
        int adScalingCount = 0;
        int apScalingCount = 0;

        for (Spell spell : spells) {
            if (spell.getVars() != null && !spell.getVars().isEmpty()) {
                String vars = spell.getVars().toLowerCase();
                if (vars.contains("attackdamage") || vars.contains("bonusad") || vars.contains("totalad")) {
                    adScalingCount++;
                }
                if (vars.contains("spelldamage") || vars.contains("ap")) {
                    apScalingCount++;
                }
            }
        }

        // Determinar perfil
        if (adScalingCount > apScalingCount * 2) {
            return DamageProfile.PHYSICAL;
        } else if (apScalingCount > adScalingCount * 2) {
            return DamageProfile.MAGICAL;
        } else {
            return DamageProfile.MIXED;
        }
    }

    /**
     * Analiza las capacidades de CC del campeón
     */
    private CCProfile analyzeCCCapabilities(String championName) {
        List<Spell> spells = spellRepository.findByChampionId(championName);

        boolean hasHardCC = false;
        boolean hasSlows = false;
        int ccAbilityCount = 0;

        // Keywords para detectar CC
        String[] hardCCKeywords = {"stun", "root", "knock", "charm", "fear", "taunt", "suppress", "airborne", "sleep"};
        String[] slowKeywords = {"slow", "cripple"};

        for (Spell spell : spells) {
            String description = (spell.getDescription() + " " + spell.getTooltip()).toLowerCase();

            for (String keyword : hardCCKeywords) {
                if (description.contains(keyword)) {
                    hasHardCC = true;
                    ccAbilityCount++;
                    break;
                }
            }

            for (String keyword : slowKeywords) {
                if (description.contains(keyword)) {
                    hasSlows = true;
                    break;
                }
            }
        }

        return new CCProfile(hasHardCC, hasSlows, ccAbilityCount);
    }

    /**
     * Crea una composición por defecto
     */
    private EnemyComposition createDefaultComposition() {
        return EnemyComposition.builder()
                .enemyChampionNames(List.of())
                .enemyTeamSize(0)
                .averageArmor(50.0)
                .averageMagicResist(40.0)
                .averageHealth(1500.0)
                .physicalDamageChampions(0)
                .magicDamageChampions(0)
                .mixedDamageChampions(0)
                .championsWithHardCC(0)
                .championsWithSlows(0)
                .totalCCAbilities(0)
                .physicalThreat(0.0)
                .magicalThreat(0.0)
                .ccThreat(0.0)
                .build();
    }

    private enum DamageProfile {
        PHYSICAL,
        MAGICAL,
        MIXED
    }

    private static class CCProfile {
        boolean hasHardCC;
        boolean hasSlows;
        int ccAbilityCount;

        CCProfile(boolean hasHardCC, boolean hasSlows, int ccAbilityCount) {
            this.hasHardCC = hasHardCC;
            this.hasSlows = hasSlows;
            this.ccAbilityCount = ccAbilityCount;
        }
    }
}

package rafa.tfg.application.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO para recibir el estado actual del juego desde la API de League of Legends
 */
@Data
public class GameStateRequestDTO {

    private ActivePlayerDTO activePlayer;
    private List<PlayerDTO> allPlayers;
    private GameDataDTO gameData;

    @Data
    public static class ActivePlayerDTO {
        private Map<String, AbilityDTO> abilities;
        private ChampionStatsDTO championStats;
        private Double currentGold;
        private FullRunesDTO fullRunes;
        private Integer level;
        private String summonerName;
    }

    @Data
    public static class AbilityDTO {
        private Integer abilityLevel;
        private String displayName;
        private String id;
        private String rawDescription;
        private String rawDisplayName;
    }

    @Data
    public static class ChampionStatsDTO {
        private Double abilityPower;
        private Double armor;
        private Double armorPenetrationFlat;
        private Double armorPenetrationPercent;
        private Double attackDamage;
        private Double attackRange;
        private Double attackSpeed;
        private Double bonusArmorPenetrationPercent;
        private Double bonusMagicPenetrationPercent;
        private Double cooldownReduction;
        private Double critChance;
        private Double critDamage;
        private Double currentHealth;
        private Double healthRegenRate;
        private Double lifeSteal;
        private Double magicLethality;
        private Double magicPenetrationFlat;
        private Double magicPenetrationPercent;
        private Double magicResist;
        private Double maxHealth;
        private Double moveSpeed;
        private Double physicalLethality;
        private Double resourceMax;
        private Double resourceRegenRate;
        private String resourceType;
        private Double resourceValue;
        private Double spellVamp;
        private Double tenacity;
    }

    @Data
    public static class FullRunesDTO {
        private List<RuneDTO> generalRunes;
        private RuneDTO keystone;
        private RuneTreeDTO primaryRuneTree;
        private RuneTreeDTO secondaryRuneTree;
        private List<StatRuneDTO> statRunes;
    }

    @Data
    public static class RuneDTO {
        private String displayName;
        private Integer id;
        private String rawDescription;
        private String rawDisplayName;
    }

    @Data
    public static class RuneTreeDTO {
        private String displayName;
        private Integer id;
        private String rawDescription;
        private String rawDisplayName;
    }

    @Data
    public static class StatRuneDTO {
        private Integer id;
        private String rawDescription;
    }

    @Data
    public static class PlayerDTO {
        private String championName;
        private Boolean isBot;
        private Boolean isDead;
        private List<ItemDTO> items;
        private Integer level;
        private String position;
        private String rawChampionName;
        private Double respawnTimer;
        private PlayerRunesDTO runes;
        private ScoresDTO scores;
        private Integer skinID;
        private String summonerName;
        private SummonerSpellsDTO summonerSpells;
        private String team;
    }

    @Data
    public static class ItemDTO {
        private Integer canUse;
        private Integer consumable;
        private Integer count;
        private String displayName;
        private Integer itemID;
        private Integer price;
        private String rawDescription;
        private String rawDisplayName;
        private Integer slot;
    }

    @Data
    public static class PlayerRunesDTO {
        private RuneDTO keystone;
        private RuneTreeDTO primaryRuneTree;
        private RuneTreeDTO secondaryRuneTree;
    }

    @Data
    public static class ScoresDTO {
        private Integer assists;
        private Integer creepScore;
        private Integer deaths;
        private Integer kills;
        private Double wardScore;
    }

    @Data
    public static class SummonerSpellsDTO {
        private SummonerSpellDTO summonerSpellOne;
        private SummonerSpellDTO summonerSpellTwo;
    }

    @Data
    public static class SummonerSpellDTO {
        private String displayName;
        private String rawDescription;
        private String rawDisplayName;
    }

    @Data
    public static class GameDataDTO {
        private String gameMode;
        private Double gameTime;
        private String mapName;
        private Integer mapNumber;
        private String mapTerrain;
    }
}

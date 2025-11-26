package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO para leer datos de Item desde JSON
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemJsonDTO {

    private String name;
    private String description;
    private String colloq;
    private String plaintext;
    private List<String> into;
    private List<String> from;
    private String group;
    private String requiredChampion;
    private String requiredAlly;
    private GoldDTO gold;
    private StatsDTO stats;
    private Boolean consumed;
    private Integer stacks;
    private Integer depth;
    private Boolean inStore;
    private Boolean hideFromAll;
    private Integer specialRecipe;
    private Map<String, Boolean> maps;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoldDTO {
        private Integer base;
        private Integer total;
        private Integer sell;
        private Boolean purchasable;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatsDTO {
        @com.fasterxml.jackson.annotation.JsonProperty("FlatHPPoolMod")
        private Double flatHPPoolMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatMPPoolMod")
        private Double flatMPPoolMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentHPPoolMod")
        private Double percentHPPoolMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentMPPoolMod")
        private Double percentMPPoolMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatHPRegenMod")
        private Double flatHPRegenMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentHPRegenMod")
        private Double percentHPRegenMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatMPRegenMod")
        private Double flatMPRegenMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentMPRegenMod")
        private Double percentMPRegenMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatArmorMod")
        private Double flatArmorMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentArmorMod")
        private Double percentArmorMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatAttackSpeedMod")
        private Double flatAttackSpeedMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentAttackSpeedMod")
        private Double percentAttackSpeedMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatCritChanceMod")
        private Double flatCritChanceMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatPhysicalDamageMod")
        private Double flatPhysicalDamageMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatMagicDamageMod")
        private Double flatMagicDamageMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentLifeStealMod")
        private Double percentLifeStealMod;

        @com.fasterxml.jackson.annotation.JsonProperty("PercentSpellVampMod")
        private Double percentSpellVampMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatMovementSpeedMod")
        private Double flatMovementSpeedMod;

        @com.fasterxml.jackson.annotation.JsonProperty("FlatSpellBlockMod")
        private Double flatSpellBlockMod;
    }
}

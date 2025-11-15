package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

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
        private Double FlatHPPoolMod;
        private Double FlatMPPoolMod;
        private Double PercentHPPoolMod;
        private Double PercentMPPoolMod;
        private Double FlatHPRegenMod;
        private Double PercentHPRegenMod;
        private Double FlatMPRegenMod;
        private Double PercentMPRegenMod;
        private Double FlatArmorMod;
        private Double PercentArmorMod;
        private Double FlatAttackSpeedMod;
        private Double PercentAttackSpeedMod;
        private Double FlatCritChanceMod;
        private Double FlatPhysicalDamageMod;
        private Double FlatMagicDamageMod;
        private Double PercentLifeStealMod;
        private Double PercentSpellVampMod;
    }
}

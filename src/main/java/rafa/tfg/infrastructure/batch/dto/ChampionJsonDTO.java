package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO para leer datos de Champion desde JSON
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionJsonDTO {

    private String version;
    private String id;
    private String key;
    private String name;
    private String title;
    private String blurb;
    private InfoDTO info;
    private ImageDTO image;
    private List<String> tags;
    private String partype;
    private StatsDTO stats;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoDTO {
        private Integer attack;
        private Integer defense;
        private Integer magic;
        private Integer difficulty;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageDTO {
        private String full;
        private String sprite;
        private String group;
        private Integer x;
        private Integer y;
        private Integer w;
        private Integer h;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatsDTO {
        private Double hp;
        private Double hpperlevel;
        private Double mp;
        private Double mpperlevel;
        private Double movespeed;
        private Double armor;
        private Double armorperlevel;
        private Double spellblock;
        private Double spellblockperlevel;
        private Double attackrange;
        private Double hpregen;
        private Double hpregenperlevel;
        private Double mpregen;
        private Double mpregenperlevel;
        private Double crit;
        private Double critperlevel;
        private Double attackdamage;
        private Double attackdamageperlevel;
        private Double attackspeed;
        private Double attackspeedperlevel;
    }
}

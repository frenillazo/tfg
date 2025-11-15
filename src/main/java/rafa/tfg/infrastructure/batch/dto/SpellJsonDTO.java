package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO para leer datos de Spell (habilidades) desde championFull.json
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpellJsonDTO {

    private String id;                  // ID del spell (ej: "AatroxQ")
    private String name;                // Nombre de la habilidad
    private String description;         // Descripción corta
    private String tooltip;             // Tooltip detallado

    @JsonProperty("leveltip")
    private LevelTipDTO levelTip;       // Información de level tip

    @JsonProperty("maxrank")
    private Integer maxRank;

    private List<Double> cooldown;      // Array de cooldowns por nivel

    @JsonProperty("cooldownBurn")
    private String cooldownBurn;

    private List<Integer> cost;         // Array de costos por nivel

    @JsonProperty("costBurn")
    private String costBurn;

    @JsonProperty("datavalues")
    private Map<String, Object> dataValues;

    private List<List<Double>> effect;  // Matriz de efectos

    @JsonProperty("effectBurn")
    private List<String> effectBurn;

    private List<Object> vars;          // Variables (puede contener objetos complejos)

    @JsonProperty("costType")
    private String costType;

    @JsonProperty("maxammo")
    private String maxAmmo;

    private List<Long> range;        // Array de rangos por nivel

    @JsonProperty("rangeBurn")
    private String rangeBurn;

    private ImageDTO image;             // Información de imagen

    private String resource;            // Recurso mostrado

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LevelTipDTO {
        private List<String> label;
        private List<String> effect;
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
}

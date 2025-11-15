package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * DTO para leer datos de RunePath desde JSON
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunePathJsonDTO {

    private Integer id;
    private String key;
    private String icon;
    private String name;
    private List<SlotDTO> slots;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlotDTO {
        private List<RuneJsonDTO> runes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RuneJsonDTO {
        private Integer id;
        private String key;
        private String icon;
        private String name;
        private String shortDesc;
        private String longDesc;
    }
}

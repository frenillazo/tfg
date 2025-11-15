package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * DTO wrapper para el archivo JSON completo de campeones
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionDataWrapper {

    private String type;
    private String format;
    private String version;
    private Map<String, ChampionJsonDTO> data;
}

package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * DTO wrapper para el archivo JSON completo de items
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDataWrapper {

    private String type;
    private String version;
    private Map<String, ItemJsonDTO> data;
}

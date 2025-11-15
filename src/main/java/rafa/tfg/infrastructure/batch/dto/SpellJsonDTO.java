package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO para leer datos de Spell desde JSON
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpellJsonDTO {

    private Long id;
    private String name;
}

package rafa.tfg.infrastructure.batch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * DTO wrapper para el archivo JSON completo de spells
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpellDataWrapper {

    private List<SpellJsonDTO> spellBuffs;
}

package rafa.tfg.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Spell (Hechizo/Buff)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spell {

    private Long id;
    private Long spellId;        // ID del spell en el juego
    private String name;
}

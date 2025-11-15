package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para Spell
 */
@Entity
@Table(name = "spells")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpellEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spell_id", unique = true, nullable = false)
    private Long spellId;

    @Column(name = "name", nullable = false)
    private String name;
}

package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA para Rune
 */
@Entity
@Table(name = "runes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rune_id", unique = true, nullable = false)
    private Integer runeId;

    @Column(name = "key", unique = true, nullable = false)
    private String key;

    @Column(name = "icon")
    private String icon;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_desc", columnDefinition = "TEXT")
    private String shortDesc;

    @Column(name = "long_desc", columnDefinition = "TEXT")
    private String longDesc;

    @Column(name = "slot_position")
    private Integer slotPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rune_path_id")
    private RunePathEntity runePath;
}

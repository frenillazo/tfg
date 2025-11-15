package rafa.tfg.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad JPA para RunePath
 */
@Entity
@Table(name = "rune_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunePathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path_id", unique = true, nullable = false)
    private Integer pathId;

    @Column(name = "rune_paths_key", unique = true, nullable = false)
    private String key;

    @Column(name = "icon")
    private String icon;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "runePath", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuneEntity> runes;
}

package rafa.tfg.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.infrastructure.persistence.entity.RuneEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para RuneEntity
 */
@Repository
public interface RuneJpaRepository extends JpaRepository<RuneEntity, Long> {

    Optional<RuneEntity> findByRuneId(Integer runeId);

    List<RuneEntity> findByRunePathId(Long runePathId);
}

package rafa.tfg.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.infrastructure.persistence.entity.RunePathEntity;

import java.util.Optional;

/**
 * Repositorio JPA para RunePathEntity
 */
@Repository
public interface RunePathJpaRepository extends JpaRepository<RunePathEntity, Long> {

    Optional<RunePathEntity> findByPathId(Integer pathId);
}

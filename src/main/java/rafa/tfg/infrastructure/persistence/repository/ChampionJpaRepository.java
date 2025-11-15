package rafa.tfg.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.infrastructure.persistence.entity.ChampionEntity;

import java.util.Optional;

/**
 * Repositorio JPA para ChampionEntity
 */
@Repository
public interface ChampionJpaRepository extends JpaRepository<ChampionEntity, Long> {

    Optional<ChampionEntity> findByChampionId(String championId);
}

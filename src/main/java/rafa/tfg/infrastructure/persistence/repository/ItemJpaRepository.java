package rafa.tfg.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.infrastructure.persistence.entity.ItemEntity;

import java.util.Optional;

/**
 * Repositorio JPA para ItemEntity
 */
@Repository
public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {

    Optional<ItemEntity> findByItemId(String itemId);
}

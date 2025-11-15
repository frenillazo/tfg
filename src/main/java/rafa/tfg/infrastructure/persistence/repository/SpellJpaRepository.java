package rafa.tfg.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.infrastructure.persistence.entity.SpellEntity;

import java.util.Optional;

/**
 * Repositorio JPA para SpellEntity
 */
@Repository
public interface SpellJpaRepository extends JpaRepository<SpellEntity, Long> {

    Optional<SpellEntity> findBySpellId(String spellId);
}

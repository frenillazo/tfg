package rafa.tfg.domain.port;

import rafa.tfg.domain.model.Spell;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de Spell en la capa de dominio
 */
public interface SpellRepository {

    Spell save(Spell spell);

    Optional<Spell> findById(Long id);

    Optional<Spell> findBySpellId(Long spellId);

    List<Spell> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

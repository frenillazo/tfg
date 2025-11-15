package rafa.tfg.domain.port;

import rafa.tfg.domain.model.Rune;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de Rune en la capa de dominio
 */
public interface RuneRepository {

    Rune save(Rune rune);

    Optional<Rune> findById(Long id);

    Optional<Rune> findByRuneId(Integer runeId);

    List<Rune> findAll();

    List<Rune> findByRunePathId(Long runePathId);

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

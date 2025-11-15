package rafa.tfg.domain.port;

import rafa.tfg.domain.model.RunePath;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de RunePath en la capa de dominio
 */
public interface RunePathRepository {

    RunePath save(RunePath runePath);

    Optional<RunePath> findById(Long id);

    Optional<RunePath> findByPathId(Integer pathId);

    List<RunePath> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

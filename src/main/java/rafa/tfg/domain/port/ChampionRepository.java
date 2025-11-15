package rafa.tfg.domain.port;

import rafa.tfg.domain.model.Champion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de Champion en la capa de dominio
 */
public interface ChampionRepository {

    Champion save(Champion champion);

    Optional<Champion> findById(Long id);

    Optional<Champion> findByChampionId(String championId);

    List<Champion> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

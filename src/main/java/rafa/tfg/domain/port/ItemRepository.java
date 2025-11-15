package rafa.tfg.domain.port;

import rafa.tfg.domain.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Puerto (interface) del repositorio de Item en la capa de dominio
 */
public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    Optional<Item> findByItemId(String itemId);

    List<Item> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();
}

package rafa.tfg.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.port.ItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para Item con operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Item> findByItemId(String itemId) {
        return itemRepository.findByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item update(Long id, Item item) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with id " + id + " not found");
        }
        item.setId(id);
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with id " + id + " not found");
        }
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return itemRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }
}

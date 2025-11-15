package rafa.tfg.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rafa.tfg.domain.model.Item;
import rafa.tfg.domain.port.ItemRepository;
import rafa.tfg.infrastructure.persistence.mapper.ItemMapper;
import rafa.tfg.infrastructure.persistence.repository.ItemJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto ItemRepository
 * Conecta la capa de dominio con la infraestructura JPA
 */
@Component
@RequiredArgsConstructor
public class ItemRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository jpaRepository;
    private final ItemMapper mapper;

    @Override
    public Item save(Item item) {
        var entity = mapper.toEntity(item);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Item> findByItemId(String itemId) {
        return jpaRepository.findByItemId(itemId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Item> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}

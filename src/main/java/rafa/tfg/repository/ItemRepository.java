package rafa.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByPurchasableTrue();

    List<Item> findByTagsContaining(String tag);
}

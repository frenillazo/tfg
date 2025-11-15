package rafa.tfg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rafa.tfg.model.Champion;

import java.util.List;

@Repository
public interface ChampionRepository extends JpaRepository<Champion, String> {

    List<Champion> findByNameContainingIgnoreCase(String name);

    List<Champion> findByTagsContaining(String tag);
}

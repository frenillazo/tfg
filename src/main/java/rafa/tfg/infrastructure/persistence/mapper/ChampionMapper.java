package rafa.tfg.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rafa.tfg.domain.model.Champion;
import rafa.tfg.infrastructure.persistence.entity.ChampionEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de MapStruct para convertir entre Champion (dominio) y ChampionEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface ChampionMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToString")
    ChampionEntity toEntity(Champion champion);

    @Mapping(target = "tags", source = "tags", qualifiedByName = "stringToList")
    Champion toDomain(ChampionEntity entity);

    List<Champion> toDomainList(List<ChampionEntity> entities);

    @Named("listToString")
    default String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }

    @Named("stringToList")
    default List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(str.split(","));
    }
}

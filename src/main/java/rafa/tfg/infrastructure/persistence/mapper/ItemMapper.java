package rafa.tfg.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import rafa.tfg.domain.model.Item;
import rafa.tfg.infrastructure.persistence.entity.ItemEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de MapStruct para convertir entre Item (dominio) y ItemEntity (JPA)
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "from", source = "from", qualifiedByName = "listToString")
    @Mapping(target = "into", source = "into", qualifiedByName = "listToString")
    ItemEntity toEntity(Item item);

    @Mapping(target = "from", source = "from", qualifiedByName = "stringToList")
    @Mapping(target = "into", source = "into", qualifiedByName = "stringToList")
    Item toDomain(ItemEntity entity);

    List<Item> toDomainList(List<ItemEntity> entities);

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

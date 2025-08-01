package com.loan.origination.system.microservices.rating.mapper;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RatingMapper {

  @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
  RatingEntity apiToEntity(Rating rating);

  @Mappings({@Mapping(target = "serviceAddress", ignore = true)})
  Rating entityToApi(RatingEntity ratingEntity);

  List<Rating> entityListToApiList(List<RatingEntity> entity);

  List<RatingEntity> apiListToEntityList(List<Rating> api);
}

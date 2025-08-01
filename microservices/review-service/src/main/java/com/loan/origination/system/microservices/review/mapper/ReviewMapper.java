package com.loan.origination.system.microservices.review.mapper;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.microservices.review.entity.ReviewEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  @Mappings({@Mapping(target = "serviceAddress", ignore = true)})
  Review entityToApi(ReviewEntity reviewEntity);

  @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
  ReviewEntity apiToEntity(Review review);

  List<Review> entityListToApiList(List<ReviewEntity> reviewEntities);

  List<ReviewEntity> apiListToEntityList(List<Review> reviews);
}

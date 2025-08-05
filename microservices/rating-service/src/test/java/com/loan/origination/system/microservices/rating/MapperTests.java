package com.loan.origination.system.microservices.rating;

import static org.junit.jupiter.api.Assertions.*;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import com.loan.origination.system.microservices.rating.mapper.RatingMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class MapperTests {
  private RatingMapper mapper = Mappers.getMapper(RatingMapper.class);

  @Test
  void mapperTests() {

    assertNotNull(mapper);

    Rating api = new Rating(1, 2, "a", 4, "C", "adr");

    RatingEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getRatingId(), entity.getRatingId());
    assertEquals(api.getAuthor(), entity.getAuthor());
    assertEquals(api.getRate(), entity.getRate());
    assertEquals(api.getContent(), entity.getContent());

    Rating api2 = mapper.entityToApi(entity);

    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getRatingId(), api2.getRatingId());
    assertEquals(api.getAuthor(), api2.getAuthor());
    assertEquals(api.getRate(), api2.getRate());
    assertEquals(api.getContent(), api2.getContent());
    assertNull(api2.getServiceAddress());
  }

  @Test
  void mapperListTests() {

    assertNotNull(mapper);

    Rating api = new Rating(1, 2, "a", 4, "C", "adr");
    List<Rating> apiList = Collections.singletonList(api);

    List<RatingEntity> entityList = mapper.apiListToEntityList(apiList);
    assertEquals(apiList.size(), entityList.size());

    RatingEntity entity = entityList.get(0);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getRatingId(), entity.getRatingId());
    assertEquals(api.getAuthor(), entity.getAuthor());
    assertEquals(api.getRate(), entity.getRate());
    assertEquals(api.getContent(), entity.getContent());

    List<Rating> api2List = mapper.entityListToApiList(entityList);
    assertEquals(apiList.size(), api2List.size());

    Rating api2 = api2List.get(0);

    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getRatingId(), api2.getRatingId());
    assertEquals(api.getAuthor(), api2.getAuthor());
    assertEquals(api.getRate(), api2.getRate());
    assertEquals(api.getContent(), api2.getContent());
    assertNull(api2.getServiceAddress());
  }
}

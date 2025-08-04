package com.loan.origination.system.microservices.product;

import static org.junit.jupiter.api.Assertions.*;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.microservices.product.entity.ProductEntity;
import com.loan.origination.system.microservices.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class MapperTests {
  private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void mapperTests() {

    assertNotNull(mapper);

    Product api = new Product(1, "n", "1", "sa");

    ProductEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getProductId(), entity.getProductId());
    assertEquals(api.getName(), entity.getName());
    assertEquals(api.getDescription(), entity.getDescription());

    Product api2 = mapper.entityToApi(entity);
    assertEquals(api.getProductId(), api2.getProductId());
    assertEquals(api.getName(), api2.getName());
    assertEquals(api.getDescription(), api2.getDescription());
    assertNull(api2.getServiceAddress());
  }
}

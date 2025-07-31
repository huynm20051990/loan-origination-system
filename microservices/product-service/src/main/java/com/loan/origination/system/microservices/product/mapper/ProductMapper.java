package com.loan.origination.system.microservices.product.mapper;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.microservices.product.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mappings({@Mapping(target = "serviceAddress", ignore = true)})
  Product entityToApi(ProductEntity productEntity);

  @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
  ProductEntity apiToEntity(Product product);
}

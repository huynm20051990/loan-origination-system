package com.loan.origination.system.microservices.product;

import static org.junit.jupiter.api.Assertions.*;

import com.loan.origination.system.microservices.product.entity.ProductEntity;
import com.loan.origination.system.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;

@DataMongoTest
public class PersistenceTests extends MongoDBTestBase {

  @Autowired private ProductRepository repository;
  private ProductEntity savedEntity;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
    ProductEntity entity = new ProductEntity(1, "A", "PA1");
    savedEntity = repository.save(entity).block();
    areProductEqual(entity, savedEntity);
  }

  @Test
  void getByProductId() {
    StepVerifier.create(repository.findByProductId(savedEntity.getProductId()))
        .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
        .verifyComplete();
  }

  @Test
  void duplicateError() {
    assertThrows(
        DuplicateKeyException.class,
        () -> {
          ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", "PN");
          repository.save(entity);
        });
  }

  @Test
  void create() {
    ProductEntity newEntity = new ProductEntity(2, "B", "PB");
    repository.save(newEntity);
    ProductEntity foundEntity = repository.findByProductId(newEntity.getProductId()).block();
    areProductEqual(newEntity, foundEntity);
    assertEquals(2, repository.count());
  }

  @Test
  void update() {
    savedEntity.setName("A1");
    repository.save(savedEntity);

    ProductEntity foundEntity = repository.findById(savedEntity.getId()).block();
    assertEquals(1, (long) foundEntity.getVersion());
    assertEquals("A1", foundEntity.getName());
  }

  @Test
  void delete() {
    repository.delete(savedEntity);
    assertFalse(repository.existsById(savedEntity.getId()).block());
  }

  @Test
  void optimisticLockError() {

    // Store the saved entity in two separate entity objects
    ProductEntity entity1 = repository.findById(savedEntity.getId()).block();
    ProductEntity entity2 = repository.findById(savedEntity.getId()).block();

    // Update the entity using the first entity object
    entity1.setName("n1");
    repository.save(entity1);

    // Update the entity using the second entity object.
    // This should fail since the second entity now holds an old version number, i.e. an Optimistic
    // Lock Error
    assertThrows(
        OptimisticLockingFailureException.class,
        () -> {
          entity2.setName("n2");
          repository.save(entity2);
        });

    // Get the updated entity from the database and verify its new state
    ProductEntity updatedEntity = repository.findById(savedEntity.getId()).block();
    assertEquals(1, (int) updatedEntity.getVersion());
    assertEquals("n1", updatedEntity.getName());
  }

  private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
    return (expectedEntity.getId().equals(actualEntity.getId()))
        && (expectedEntity.getVersion().equals(actualEntity.getVersion()))
        && (expectedEntity.getProductId() == actualEntity.getProductId())
        && (expectedEntity.getName().equals(actualEntity.getName()))
        && (expectedEntity.getDescription().equals(actualEntity.getDescription()));
  }
}

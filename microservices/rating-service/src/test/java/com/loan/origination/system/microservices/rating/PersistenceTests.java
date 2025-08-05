package com.loan.origination.system.microservices.rating;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import com.loan.origination.system.microservices.rating.repository.RatingRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

@DataMongoTest
public class PersistenceTests {

  @Autowired private RatingRepository repository;

  private RatingEntity savedEntity;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
    RatingEntity entity = new RatingEntity(1, 2, "a", 3, "c");
    savedEntity = repository.save(entity);
    assertEqualsRating(entity, savedEntity);
  }

  @Test
  void getByProductId() {
    List<RatingEntity> entityList = repository.findByProductId(savedEntity.getProductId());
    assertThat(entityList, hasSize(1));
    assertEqualsRating(savedEntity, entityList.get(0));
  }

  @Test
  void create() {
    RatingEntity newEntity = new RatingEntity(1, 3, "a", 3, "c");
    repository.save(newEntity);
    RatingEntity foundEntity = repository.findById(newEntity.getId()).get();
    assertEqualsRating(newEntity, foundEntity);
    assertEquals(2, repository.count());
  }

  @Test
  void update() {
    savedEntity.setAuthor("a2");
    repository.save(savedEntity);
    RatingEntity foundEntity = repository.findById(savedEntity.getId()).get();
    assertEquals(1, (long) foundEntity.getVersion());
    assertEquals("a2", foundEntity.getAuthor());
  }

  @Test
  void delete() {
    repository.delete(savedEntity);
    assertFalse(repository.existsById(savedEntity.getId()));
  }

  @Test
  void duplicateError() {
    assertThrows(
        DuplicateKeyException.class,
        () -> {
          RatingEntity entity = new RatingEntity(1, 2, "a", 3, "c");
          repository.save(entity);
        });
  }

  @Test
  void optimisticLockError() {

    // Store the saved entity in two separate entity objects
    RatingEntity entity1 = repository.findById(savedEntity.getId()).get();
    RatingEntity entity2 = repository.findById(savedEntity.getId()).get();

    // Update the entity using the first entity object
    entity1.setAuthor("a1");
    repository.save(entity1);

    //  Update the entity using the second entity object.
    // This should fail since the second entity now holds an old version number, i.e. an Optimistic
    // Lock Error
    assertThrows(
        OptimisticLockingFailureException.class,
        () -> {
          entity2.setAuthor("a2");
          repository.save(entity2);
        });

    // Get the updated entity from the database and verify its new sate
    RatingEntity updatedEntity = repository.findById(savedEntity.getId()).get();
    assertEquals(1, (int) updatedEntity.getVersion());
    assertEquals("a1", updatedEntity.getAuthor());
  }

  private void assertEqualsRating(RatingEntity expectedEntity, RatingEntity actualEntity) {
    assertEquals(expectedEntity.getId(), actualEntity.getId());
    assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
    assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
    assertEquals(expectedEntity.getRatingId(), actualEntity.getRatingId());
    assertEquals(expectedEntity.getAuthor(), actualEntity.getAuthor());
    assertEquals(expectedEntity.getRate(), actualEntity.getRate());
    assertEquals(expectedEntity.getContent(), actualEntity.getContent());
  }
}

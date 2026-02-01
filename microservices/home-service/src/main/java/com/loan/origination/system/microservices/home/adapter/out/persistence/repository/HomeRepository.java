package com.loan.origination.system.microservices.home.adapter.out.persistence.repository;

import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.HomeEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<HomeEntity, UUID> {
  @Query(
      value = "SELECT * FROM homes ORDER BY embedding <=> cast(:embedding as vector) LIMIT :limit",
      nativeQuery = true)
  List<HomeEntity> findNearestNeighbors(
      @Param("embedding") float[] embedding, @Param("limit") int limit);
}

package com.loan.origination.system.microservices.credit.adapter.out.persistence;

import com.loan.origination.system.microservices.credit.adapter.out.persistence.entity.CreditReportEntity;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.mapper.CreditPersistenceMapper;
import com.loan.origination.system.microservices.credit.adapter.out.persistence.repository.CreditReportRepository;
import com.loan.origination.system.microservices.credit.domain.model.CreditReport;
import com.loan.origination.system.microservices.credit.domain.port.out.CreditRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class CreditPersistenceAdapter implements CreditRepositoryPort {

  private final CreditReportRepository creditReportRepository;
  private final CreditPersistenceMapper mapper;

  public CreditPersistenceAdapter(
      CreditReportRepository jpaRepository, CreditPersistenceMapper mapper) {
    this.creditReportRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public void save(CreditReport creditReport, String aggregateId) {
    CreditReportEntity entity = mapper.toEntity(creditReport, aggregateId);
    // Map any missing fields like application_id if necessary
    creditReportRepository.save(entity);
  }
}

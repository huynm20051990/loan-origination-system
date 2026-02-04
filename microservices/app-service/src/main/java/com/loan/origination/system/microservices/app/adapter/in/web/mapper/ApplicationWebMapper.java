package com.loan.origination.system.microservices.app.adapter.in.web.mapper;

import com.loan.origination.system.api.core.application.dto.ApplicationResponseDTO;
import com.loan.origination.system.api.core.application.dto.ApplicationSubmissionRequestDTO;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import org.springframework.stereotype.Component;

@Component
public class ApplicationWebMapper {

  /** Maps the nested Request DTO to the Domain's Borrower Value Object. */
  public Borrower toBorrowerDomain(ApplicationSubmissionRequestDTO request) {
    return new Borrower(
        request.personal().fullName(),
        request.personal().email(),
        request.personal().phone(),
        request.personal().ssn(),
        request.financial().annualIncome(),
        request.financial().employer());
  }

  /** Maps the LoanApplication Domain model back to a Response DTO for the UI. */
  public ApplicationResponseDTO toResponse(Application domain) {
    return new ApplicationResponseDTO(
        domain.getId(),
        domain.getApplicationNumber(),
        domain.getStatus().name(),
        domain.getCreatedAt(),
        domain.getBorrower().fullName(),
        domain.getLoanAmount());
  }
}

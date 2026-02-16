package com.loan.origination.system.microservices.app.adapter.in.web.mapper;

import com.loan.origination.system.api.core.application.dto.ApplicationResponseDTO;
import com.loan.origination.system.api.core.application.dto.ApplicationSubmissionRequestDTO;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import org.springframework.stereotype.Component;

@Component
public class ApplicationWebMapper {

  public Borrower toBorrowerDomain(ApplicationSubmissionRequestDTO request) {
    return new Borrower(
        request.personal().fullName(),
        request.personal().email(),
        request.personal().phone(),
        request.identity().dob(),
        request.identity().ssn());
  }

  public ApplicationResponseDTO toResponse(Application domain) {
    return new ApplicationResponseDTO(
        domain.getId(),
        domain.getApplicationNumber(),
        domain.getStatus().name(),
        domain.getCreatedAt(),
        domain.getBorrower().fullName(),
        domain.getLoanAmount(),
        domain.getLoanPurpose());
  }
}

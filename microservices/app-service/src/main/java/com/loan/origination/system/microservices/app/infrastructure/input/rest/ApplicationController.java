package com.loan.origination.system.microservices.app.infrastructure.input.rest;

import com.loan.origination.system.api.core.application.dto.ApplicationResponseDTO;
import com.loan.origination.system.api.core.application.dto.ApplicationSubmissionRequestDTO;
import com.loan.origination.system.api.core.application.v1.ApplicationAPI;
import com.loan.origination.system.microservices.app.application.port.input.ApplicationUseCase;
import com.loan.origination.system.microservices.app.application.port.output.ApplicationRepositoryPort;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import com.loan.origination.system.microservices.app.infrastructure.input.rest.mapper.ApplicationWebMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ApplicationController implements ApplicationAPI {

  private final ApplicationUseCase loanApplicationUseCase;
  private final ApplicationRepositoryPort loanRepositoryPort; // For read operations
  private final ApplicationWebMapper mapper;

  public ApplicationController(
      ApplicationUseCase loanApplicationUseCase,
      ApplicationRepositoryPort loanRepositoryPort,
      ApplicationWebMapper mapper) {
    this.loanApplicationUseCase = loanApplicationUseCase;
    this.loanRepositoryPort = loanRepositoryPort;
    this.mapper = mapper;
  }

  @Override
  public ApplicationResponseDTO submitApplication(
      @Valid @RequestBody ApplicationSubmissionRequestDTO request) {
    Borrower borrower = mapper.toBorrowerDomain(request);

    Application application =
        loanApplicationUseCase.submit(
            request.homeId(),
            borrower,
            request.request().loanAmount(),
            request.request().loanPurpose());

    return mapper.toResponse(application);
  }

  @Override
  public ApplicationResponseDTO getApplicationById(@PathVariable UUID id) {
    return loanRepositoryPort
        .findById(id)
        .map(mapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
  }

  @Override
  public ApplicationResponseDTO getApplicationByNumber(@PathVariable String applicationNumber) {
    return loanRepositoryPort
        .findByApplicationNumber(applicationNumber)
        .map(mapper::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
  }

  @Override
  public List<ApplicationResponseDTO> getApplicationsByEmail(@RequestParam String email) {
    return loanRepositoryPort.findByEmail(email).stream().map(mapper::toResponse).toList();
  }

  @Override
  public void deleteApplication(UUID id) {
    if (!loanRepositoryPort.existsById(id)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Application not found with ID: " + id);
    }

    loanApplicationUseCase.delete(id);
  }
}

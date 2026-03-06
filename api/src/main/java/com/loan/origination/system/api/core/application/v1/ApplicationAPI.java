package com.loan.origination.system.api.core.application.v1;

import com.loan.origination.system.api.core.application.dto.ApplicationResponseDTO;
import com.loan.origination.system.api.core.application.dto.ApplicationSubmissionRequestDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/applications")
public interface ApplicationAPI {

  /** Atomic submission of the loan application. Triggers the Transactional Outbox pattern. */
  @PostMapping(consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  ApplicationResponseDTO submitApplication(@RequestBody ApplicationSubmissionRequestDTO request);

  /** Get application details by internal UUID. */
  @GetMapping(value = "/{id}", produces = "application/json")
  ApplicationResponseDTO getApplicationById(@PathVariable UUID id);

  /** Get application details by the human-readable application number (e.g., APP-2026-X). */
  @GetMapping(value = "/{applicationNumber}", produces = "application/json")
  ApplicationResponseDTO getApplicationByNumber(@PathVariable String applicationNumber);

  /**
   * List applications (e.g., for a user dashboard). Usually filtered by user context/email in a
   * real app.
   */
  @GetMapping(produces = "application/json")
  List<ApplicationResponseDTO> getApplicationsByEmail(@RequestParam String email);

  /** * Deletes an application by its internal UUID. Returns 204 No Content on success. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteApplication(@PathVariable UUID id);
}

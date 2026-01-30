package com.loan.origination.system.api.core.home.v1;

import com.loan.origination.system.api.core.home.dto.HomeRequestDTO;
import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/homes")
public interface HomeAPI {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  HomeResponseDTO addHome(@RequestBody HomeRequestDTO request);

  @GetMapping("/{id}")
  HomeResponseDTO getHome(@PathVariable UUID id);

  @GetMapping
  List<HomeResponseDTO> getAllHomes();

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteHome(@PathVariable UUID id);
}

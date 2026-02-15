package com.loan.origination.system.microservices.home.domain.model;

import java.util.List;

public record SearchIntent(
    String vibe, // The descriptive part (e.g., "cozy cottage")
    List<FilterItem> filters // The structured part (e.g., price < 500k)
    ) {}

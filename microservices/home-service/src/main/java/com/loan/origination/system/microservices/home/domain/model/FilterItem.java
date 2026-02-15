package com.loan.origination.system.microservices.home.domain.model;

public record FilterItem(
    String column, // e.g., "price", "beds"
    String operator, // e.g., "LT", "GT", "EQ"
    Object value // e.g., 500000
    ) {}

package com.loan.origination.system.microservices.chat.application.port.output;

/**
 * Port contract type representing a single home listing returned by the home-service search API.
 *
 * <p>This record is a pure data transfer object used exclusively at the output port boundary.
 * It is never persisted and carries no framework annotations to preserve domain purity.
 *
 * @param id        unique identifier of the listing
 * @param price     listing price in USD
 * @param beds      number of bedrooms
 * @param baths     number of bathrooms
 * @param sqft      total square footage
 * @param imageUrl  URL of the primary listing image
 * @param address   structured postal address (see {@link Address})
 * @param status    current listing status (e.g. "active", "pending", "sold")
 * @param description human-readable description of the property
 */
public record HomeResult(
        String id,
        double price,
        int beds,
        double baths,
        int sqft,
        String imageUrl,
        Address address,
        String status,
        String description) {

    /**
     * Structured postal address nested inside a {@link HomeResult}.
     *
     * @param street street number and name
     * @param city   city name
     * @param state  two-letter US state abbreviation
     * @param zip    five-digit ZIP code
     */
    public record Address(
            String street,
            String city,
            String state,
            String zip) {}
}

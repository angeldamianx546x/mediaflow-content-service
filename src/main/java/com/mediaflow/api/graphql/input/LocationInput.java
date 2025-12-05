package com.mediaflow.api.graphql.input;

import lombok.Data;

@Data
public class LocationInput {
    private String country;
    private String region;
    private String city;
    private float lat;
    private float lng;
}

package com.mediaflow.api.client;

import org.springframework.stereotype.Component;


@Component
public class IpInfoClient {
    // private final RestTemplate restTemplate;
    // private final String token;

    // public IpInfoClient(@Value("${ipinfo.token}") String token) {
    //     this.restTemplate = new RestTemplate();
    //     this.token = token;
    // }

    // public LocationResponse consultar(String ip) {
    //     String base = "https://ipinfo.io";
    //     String uri;
    //     if (ip == null || ip.isBlank()) {
    //         uri = base + "/json?token=" + token;
    //     } else {
    //         uri = base + "/" + ip + "/json?token=" + token;
    //     }
    //     try {
    //         return restTemplate.getForObject(uri, LocationResponse.class);
    //     } catch (Exception ex) {
    //         return null;
    //     }
    // }
}

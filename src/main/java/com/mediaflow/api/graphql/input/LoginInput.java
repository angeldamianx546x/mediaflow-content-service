package com.mediaflow.api.graphql.input;

import lombok.Data;

@Data
public class LoginInput {
    private String email;
    private String password;
}

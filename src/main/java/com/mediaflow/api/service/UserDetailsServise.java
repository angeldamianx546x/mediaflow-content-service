package com.mediaflow.api.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface  UserDetailsServise {
    UserDetails loadUserByUsername(String email);
}

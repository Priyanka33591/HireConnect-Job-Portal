package com.hireconnect.analytics.security;

import java.util.Set;

public record UserPrincipal(Long userId, String email, Set<String> roles) {
}


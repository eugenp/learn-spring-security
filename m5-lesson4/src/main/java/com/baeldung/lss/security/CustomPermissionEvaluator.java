package com.baeldung.lss.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public final class CustomPermissionEvaluator implements PermissionEvaluator {

    public CustomPermissionEvaluator() {
        super();
    }

    //

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        final UserDetails principal = (UserDetails) authentication.getPrincipal();
        if (principal.getUsername().startsWith("ORG")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        final UserDetails principal = (UserDetails) authentication.getPrincipal();
        if (principal.getUsername().startsWith("ORG")) {
            return true;
        }
        return false;
    }

}
package com.baeldung.lss.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

public class RealTimeLockAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext object) {
        Authentication authentication = authenticationSupplier.get();

        if(authentication == null) {
            return new AuthorizationDecision(false);
        }

        if (LockedUsers.isLocked(authentication.getName())) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(true);
    }
}

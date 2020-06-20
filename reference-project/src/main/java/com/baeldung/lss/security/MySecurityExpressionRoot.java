package com.baeldung.lss.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MySecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private Object target;

    public MySecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    //

    public boolean isAdmin() {
        if (!(getPrincipal() instanceof User)) {
            return false;
        }
        User principal = (User) getPrincipal();
        return principal.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    //

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }
}
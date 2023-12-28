package com.baeldung.lss.security;

import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

public class CustomMethodSecurityExpressionProvider {
	
	public boolean isAdmin(MethodSecurityExpressionOperations root) {
		return root.hasAuthority("ROLE_ADMIN");
	}

}

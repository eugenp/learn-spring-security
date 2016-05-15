package com.baeldung.lss.security;

import java.util.Set;

import com.google.common.collect.Sets;

public final class LockedUsers {

    private static final Set<String> lockedUsersSets = Sets.newHashSet();

    private LockedUsers() {
        //
    }

    //

    public static final boolean isLocked(final String username) {
        return lockedUsersSets.contains(username);
    }

    public static final void lock(final String username) {
        lockedUsersSets.add(username);
    }

}

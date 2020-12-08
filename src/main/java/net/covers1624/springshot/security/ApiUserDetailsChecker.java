package net.covers1624.springshot.security;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * Created by covers1624 on 25/11/20.
 */
public class ApiUserDetailsChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }
    }
}

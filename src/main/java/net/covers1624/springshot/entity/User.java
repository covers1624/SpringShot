package net.covers1624.springshot.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by covers1624 on 5/8/20.
 */

@Entity (name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column (unique = true)
    private String username;

    @Column (unique = true)
    private String email;

    private String password;

    private UserRole userRole = UserRole.USER;

    private boolean locked = false;

    private boolean enabled = false;

    private boolean placeholder = false;

    private String linkPrefix = "https://ss.ln-k.net/";

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(userRole.name()));
    }

    public long getId() { return id; }
    @Override public String getPassword() { return password; }
    public String getEmail() { return email; }
    @Override public String getUsername() { return username; }
    public UserRole getUserRole() { return userRole; }
    public boolean isLocked() { return locked; }
    @Override public boolean isAccountNonLocked() { return !locked; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    public boolean isPlaceholder() { return placeholder; }
    public String getLinkPrefix() { return linkPrefix; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setLinkPrefix(String linkPrefix) { this.linkPrefix = linkPrefix; }
    public void setPlaceholder(boolean placeholder) { this.placeholder = placeholder; }
}

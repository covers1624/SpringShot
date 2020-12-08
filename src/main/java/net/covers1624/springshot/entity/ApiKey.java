package net.covers1624.springshot.entity;

import javax.persistence.*;

/**
 * Created by covers1624 on 5/11/20.
 */
@Entity (name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    private String secret;

    @ManyToOne
    private User user;

    //@formatter:off
    public long getId() { return id; }
    public String getSecret() { return secret; }
    public User getUser() { return user; }
    public void setSecret(String secret) { this.secret = secret; }
    public void setUser(User user) { this.user = user; }
    //@formatter:on
}

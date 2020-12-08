package net.covers1624.springshot.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by covers1624 on 26/11/20.
 */
@Entity (name = "user_object")
public class UserObject {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (nullable = false, unique = true)
    private String address;

    @Column (nullable = false)
    private String name;

    @ManyToOne
    private User owner;

    @ManyToOne
    private UploadObject object;

    @Column (nullable = false)
    @Temporal (TemporalType.TIMESTAMP)
    private Date lastAccess = new Date();

    @CreationTimestamp
    @Column (nullable = false)
    @Temporal (TemporalType.TIMESTAMP)
    private Date created = new Date();


    public String getAddress() { return address; }
    public String getName() { return name; }
    public User getOwner() { return owner; }
    public UploadObject getObject() { return object; }
    public Date getLastAccess() { return lastAccess; }
    public Date getCreated() { return created; }
    public void setAddress(String address) { this.address = address; }
    public void setName(String name) { this.name = name; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setObject(UploadObject object) { this.object = object; }
    public void setLastAccess(Date lastAccess) { this.lastAccess = lastAccess; }
}

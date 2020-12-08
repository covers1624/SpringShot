package net.covers1624.springshot.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by covers1624 on 7/11/20.
 */
@Entity (name = "objects")
public class UploadObject {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (nullable = false, unique = true)
    private String hash;

    private int contentLength;

    private String contentType;

    private String extension;

    @ManyToMany
    private List<UserObject> owners = new ArrayList<>();


    public UploadObject() {
    }

    public UploadObject(String hash, int contentLength, String extension) {
        this.hash = hash;
        this.contentLength = contentLength;
        this.extension = extension;
    }

    public int getId() { return id; }
    public String getHash() { return hash; }
    public int getContentLength() { return contentLength; }
    public String getContentType() { return contentType; }
    public String getExtension() { return extension; }
    public List<UserObject> getOwners() { return owners; }
    public void setHash(String hash) { this.hash = hash; }
    public void setContentLength(int contentLength) { this.contentLength = contentLength; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setExtension(String extension) { this.extension = extension; }
    public void setOwners(List<UserObject> owners) { this.owners = owners; }
    public void addOwner(UserObject owner) { owners.add(owner); }
}

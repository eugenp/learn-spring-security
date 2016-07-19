package com.baeldung.lss.model.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "acl_object_identity", type = "acl_object_identity")
public class AclObjectIdentity {

    @Id
    private String id;
    private String objectIdClass;
    private String objectIdIdentity;
    private String parentObjectId;
    private String ownerId;
    private boolean entriesInheriting;

    //

    public AclObjectIdentity() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectIdClass() {
        return objectIdClass;
    }

    public void setObjectIdClass(String objectIdClass) {
        this.objectIdClass = objectIdClass;
    }

    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    public String getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(String parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isEntriesInheriting() {
        return entriesInheriting;
    }

    public void setEntriesInheriting(boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

}

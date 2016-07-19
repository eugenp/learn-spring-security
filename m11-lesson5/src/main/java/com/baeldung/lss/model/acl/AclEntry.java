package com.baeldung.lss.model.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "acl_entry", type = "acl_entry")
public class AclEntry {
    @Id
    private String id;
    private String objectIdentityId;
    private String sid;
    private int order;
    private int mask;
    private boolean granting;
    private boolean auditSuccess;
    private boolean auditFailure;

    //

    public AclEntry() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectIdentityId() {
        return objectIdentityId;
    }

    public void setObjectIdentityId(String objectIdentityId) {
        this.objectIdentityId = objectIdentityId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public boolean isGranting() {
        return granting;
    }

    public void setGranting(boolean granting) {
        this.granting = granting;
    }

    public boolean isAuditSuccess() {
        return auditSuccess;
    }

    public void setAuditSuccess(boolean auditSuccess) {
        this.auditSuccess = auditSuccess;
    }

    public boolean isAuditFailure() {
        return auditFailure;
    }

    public void setAuditFailure(boolean auditFailure) {
        this.auditFailure = auditFailure;
    }

}

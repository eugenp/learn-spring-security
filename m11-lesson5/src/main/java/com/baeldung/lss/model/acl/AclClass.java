package com.baeldung.lss.model.acl;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "acl_class", type = "acl_class")
public class AclClass {
    @Id
    private String id;

    private String className;

    //

    public AclClass() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}

package com.baeldung.lss.persistence.acl;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.acl.AclClass;

public interface AclClassRepository extends ElasticsearchRepository<AclClass, String> {
    AclClass findOneByClassName(String className);
}

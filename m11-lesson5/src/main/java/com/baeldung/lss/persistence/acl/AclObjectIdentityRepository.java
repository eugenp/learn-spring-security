package com.baeldung.lss.persistence.acl;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.acl.AclObjectIdentity;

public interface AclObjectIdentityRepository extends ElasticsearchRepository<AclObjectIdentity, String> {
    AclObjectIdentity findOneByObjectIdIdentityAndObjectIdClass(String objectIdIdentity, String objectIdClass);

    List<AclObjectIdentity> findByParentObjectId(String parentObjectId);

}

package com.baeldung.lss.persistence.acl;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.acl.AclEntry;

public interface AclEntryRepository extends ElasticsearchRepository<AclEntry, String> {
    void deleteByObjectIdentityId(String objectIdentityId);

    List<AclEntry> findByObjectIdentityId(String objectIdentityId);
}

package com.baeldung.lss.persistence.acl;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.acl.AclSid;

public interface AclSidRepository extends ElasticsearchRepository<AclSid, String> {
    List<AclSid> findBySidAndPrincipal(String sid, boolean principal);
}

package com.baeldung.lss.persistence;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.baeldung.lss.model.Possession;

public interface PossessionRepository extends ElasticsearchRepository<Possession, String> {

}

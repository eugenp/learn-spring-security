package com.baeldung.lss.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lss.model.Possession;

public interface PossessionRepository extends JpaRepository<Possession, Long> {

    Possession findByName(String name);

}

package com.baeldung.lss.persistence;

import com.baeldung.lss.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

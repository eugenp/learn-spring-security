package com.baeldung.lss.persistence;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baeldung.lss.persistence.dao.CustomUserDetailsRepository;
import com.baeldung.lss.persistence.dao.OrganizationRepository;
import com.baeldung.lss.persistence.dao.PrivilegeRepository;
import com.baeldung.lss.persistence.model.CustomUserDetails;
import com.baeldung.lss.persistence.model.Organization;
import com.baeldung.lss.persistence.model.Privilege;

import jakarta.annotation.PostConstruct;

@Component
public class SetupData {
    @Autowired
    private CustomUserDetailsRepository userRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @PostConstruct
    public void init() {
        initPrivileges();
        initOrganizations();
        initUsers();
    }

    private void initUsers() {
        final Privilege privilege1 = privilegeRepository.findByName("USER_READ_PRIVILEGE");
        final Privilege privilege2 = privilegeRepository.findByName("USER_WRITE_PRIVILEGE");
        //
        final CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("john");
        user1.setPassword("123");
        user1.setPrivileges(new HashSet<Privilege>(Arrays.asList(privilege1)));
        user1.setOrganization(organizationRepository.findByName("FirstOrg"));
        userRepository.save(user1);
        //
        final CustomUserDetails user2 = new CustomUserDetails();
        user2.setUsername("tom");
        user2.setPassword("111");
        user2.setPrivileges(new HashSet<Privilege>(Arrays.asList(privilege1, privilege2)));
        user2.setOrganization(organizationRepository.findByName("SecondOrg"));
        userRepository.save(user2);
        //
        //
        final CustomUserDetails user3 = new CustomUserDetails();
        user3.setUsername("user");
        user3.setPassword("pass");
        user3.setPrivileges(new HashSet<Privilege>(Arrays.asList(privilege1, privilege2)));
        user3.setOrganization(organizationRepository.findByName("FirstOrg"));
        userRepository.save(user3);
    }

    private void initOrganizations() {
        final Organization org1 = new Organization("FirstOrg");
        organizationRepository.save(org1);
        //
        final Organization org2 = new Organization("SecondOrg");
        organizationRepository.save(org2);

    }

    private void initPrivileges() {
        final Privilege privilege1 = new Privilege("USER_READ_PRIVILEGE");
        privilegeRepository.save(privilege1);
        //
        final Privilege privilege2 = new Privilege("USER_WRITE_PRIVILEGE");
        privilegeRepository.save(privilege2);
    }
}

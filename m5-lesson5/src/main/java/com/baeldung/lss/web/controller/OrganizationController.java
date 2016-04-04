package com.baeldung.lss.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baeldung.lss.persistence.model.Organization;

@Controller
public class OrganizationController {

    @Autowired
    private com.baeldung.lss.persistence.dao.OrganizationRepository organizationRepository;

    @PreAuthorize("isMember(#id)")
    @RequestMapping(method = RequestMethod.GET, value = "/organizations/{id}")
    @ResponseBody
    public Organization findOrgById(@PathVariable final long id) {
        return organizationRepository.findOne(id);
    }

    //
    //

    @PreAuthorize("hasAuthority('USER_READ_PRIVILEGE')")
    @RequestMapping(method = RequestMethod.GET, value = "/organizations")
    @ResponseBody
    public Organization findOrgByName(@RequestParam final String name) {
        return organizationRepository.findByName(name);
    }

}

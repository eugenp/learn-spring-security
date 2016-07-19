package com.baeldung.lss.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.baeldung.lss.model.Possession;
import com.baeldung.lss.persistence.PossessionRepository;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.LssPermissionService;

@Controller
@RequestMapping(value = "/possessions")
public class PossessionController {

    @Autowired
    private PossessionRepository possessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LssPermissionService permissionService;

    // API

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @PostAuthorize("hasPermission(returnObject, 'READ') or hasPermission(returnObject, 'ADMINISTRATION')")
    public Possession findOne(@PathVariable("id") final Long id) {
        return possessionRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid Possession possession, Authentication authentication) {
        possession.setOwner(userRepository.findByEmail(authentication.getName()));
        possession = possessionRepository.save(possession);
        System.out.println(possession);
        // permissionService.addPermissionForAuthority(possession, BasePermission.ADMINISTRATION, "ADMIN");
        permissionService.addPermissionForUser(possession, BasePermission.ADMINISTRATION, authentication.getName());
        return new ModelAndView("redirect:/user?message=Possession created with id " + possession.getId());
    }

    //

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@ModelAttribute final Possession possession) {
        return "tl/possession";
    }

}
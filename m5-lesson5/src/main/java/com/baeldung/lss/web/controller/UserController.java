package com.baeldung.lss.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.persistence.model.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    //

    @PreAuthorize("hasPermission('User', 'read')")
    @RequestMapping
    public ModelAndView list() {
        final Iterable<User> users = this.userRepository.findAll();
        return new ModelAndView("tl/list", "users", users);
    }

    @PreAuthorize("hasPermission('User', 'read')")
    @RequestMapping("{id}")
    public ModelAndView view(@PathVariable("id") User user) {
        return new ModelAndView("tl/view", "user", user);
    }

    @PreAuthorize("hasPermission(#user, 'write')")
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid User user, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("tl/form", "formErrors", result.getAllErrors());
        }
        user = this.userRepository.save(user);
        redirect.addFlashAttribute("globalMessage", "Successfully created a new user");
        return new ModelAndView("redirect:/user/{user.id}", "user.id", user.getId());
    }

    @PreAuthorize("hasPermission('User', 'write')")
    @RequestMapping(value = "delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        this.userRepository.delete(id);
        return new ModelAndView("redirect:/user/");
    }

    @PreAuthorize("hasPermission('User', 'write')")
    @RequestMapping(value = "modify/{id}", method = RequestMethod.GET)
    public ModelAndView modifyForm(@PathVariable("id") User user) {
        return new ModelAndView("tl/form", "user", user);
    }

    // the form
    @PreAuthorize("hasPermission(#user, 'write')")
    @RequestMapping(params = "form", method = RequestMethod.GET)
    // @PreAuthorize("hasRole('ADMIN')")
    // @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    // @PreAuthorize("denyAll")
    // @PreAuthorize("principal.username=='user2'")
    public String createForm(@ModelAttribute User user) {
        return "tl/form";
    }

}

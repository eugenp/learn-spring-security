package com.baeldung.lss.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.baeldung.lss.model.User;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.ActiveUserService;
import com.baeldung.lss.service.IUserService;
import com.baeldung.lss.validation.EmailExistsException;

@Controller
@RequestMapping("/user")
class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private IUserService userService;

    //

    @RequestMapping
    public ModelAndView list() {
        final List<User> users = activeUserService.getActiveUsers()
            .stream()
            .map(s -> userService.findUserByEmail(s))
            .collect(Collectors.toList());
        // final Iterable<User> users = this.userRepository.findAll();

        return new ModelAndView("tl/list", "users", users);
    }

    @RequestMapping("{id}")
    public ModelAndView view(@PathVariable("id") final User user) {
        return new ModelAndView("tl/view", "user", user);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid final User user, final BindingResult result, final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("tl/form", "formErrors", result.getAllErrors());
        }
        try {
            if (user.getId() == null) {
                userService.registerNewUser(user);
                redirect.addFlashAttribute("globalMessage", "Successfully created a new user");
            } else {
                userService.updateExistingUser(user);
                redirect.addFlashAttribute("globalMessage", "Successfully updated the user");
            }
        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("tl/form", "user", user);
        }
        return new ModelAndView("redirect:/user/{user.id}", "user.id", user.getId());
    }

    @RequestMapping(value = "delete/{id}")
    public ModelAndView delete(@PathVariable("id") final Long id) {
        this.userRepository.findById(id)
            .ifPresent(user -> this.userRepository.delete(user));
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "modify/{id}", method = RequestMethod.GET)
    public ModelAndView modifyForm(@PathVariable("id") final User user) {
        return new ModelAndView("tl/form", "user", user);
    }

    // the form

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@ModelAttribute final User user) {
        return "tl/form";
    }

}

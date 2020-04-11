package com.baeldung.lss.web.controller;

import com.baeldung.lss.persistence.RoleRepository;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.security.ActiveUserService;
import com.baeldung.lss.service.IUserService;
import com.baeldung.lss.validation.EmailExistsException;
import com.baeldung.lss.web.model.Role;
import com.baeldung.lss.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private RoleRepository roleRepository;

    @RequestMapping
    public ModelAndView list() {

        final List<User> activeUsers = activeUserService.getActiveUsers()
                    .stream()
                    .map(s -> new User(s))
                    .collect(Collectors.toList());

        final Iterable<User> users = this.userRepository.findAll();
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("users",users);
        modelMap.put("activeUsers",activeUsers);
        return new ModelAndView("tl/list", modelMap);
    }

    @RequestMapping("{id}")
    @Secured("ROLE_ADMIN")
    public ModelAndView view(@PathVariable("id") User user) {
        return new ModelAndView("tl/view", "user", user);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView create(@Valid final User user, final BindingResult result, final RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return new ModelAndView("tl/form", "formErrors", result.getAllErrors());
        }
        try {
            if (user.getId() == null) {
                final Role userRole = roleRepository.findByName("USER");
                user.setRoles(Arrays.asList(userRole));
                user.setEnabled(true);
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
    @PreAuthorize("hasRole('ADMIN')")// admin can delete user
    public ModelAndView delete(@PathVariable("id") final Long id) {
        this.userRepository.findById(id)
                .ifPresent(user -> this.userRepository.delete(user));
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    //admin can create user. handled through custom AOP based secure method invocation.
    public String createForm(@ModelAttribute User user) {
        return "tl/form";
    }

    @RequestMapping(value = "modify/{id}", method = RequestMethod.GET)
    @PreAuthorize("isAdmin()")
    public ModelAndView modifyForm(@PathVariable("id") User user) {
        return new ModelAndView("tl/form", "user", user);
    }

}

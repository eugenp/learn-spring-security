package com.baeldung.lss.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.baeldung.lss.exception.ApplicationException;
import com.baeldung.lss.model.User;
import com.baeldung.lss.repository.UserRepository;

@Controller
@RequestMapping("/")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public ModelAndView list() {
		Iterable<User> users = this.userRepository.findAll();
		return new ModelAndView("users/list", "users", users);
	}

	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") User user) {
		return new ModelAndView("users/view", "user", user);
	}

	@GetMapping(params = { "form" })
	public String createForm(@ModelAttribute User user) {
		return "users/form";
	}

	@PostMapping
	public ModelAndView create(@Valid User user, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("users/form", "formErrors", result.getAllErrors());
		}
		user = this.userRepository.save(user);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new user");
		return new ModelAndView("redirect:/{user.id}", "user.id", user.getId());
	}

	@GetMapping("foo")
	public String foo() {
		throw new ApplicationException("Expected exception in controller");
	}

	@GetMapping("delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id) {
		this.userRepository.deleteUser(id);
		return new ModelAndView("redirect:/");
	}

	@GetMapping("modify/{id}")
	public ModelAndView modifyForm(@PathVariable("id") User user) {
		return new ModelAndView("users/form", "user", user);
	}

}

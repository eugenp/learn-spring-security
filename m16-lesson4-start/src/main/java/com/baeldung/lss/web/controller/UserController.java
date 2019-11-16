package com.baeldung.lss.web.controller;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    //

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Flux<User> list(final Model model) {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public Mono<User> view(@PathVariable("id") Long id, final Model model) {
        return userRepository.findUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<User> create(@Valid User user, BindingResult result, final Model model) {
        return userRepository.save(user);
    }

    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.userRepository.deleteUser(id);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAllUsers(final Model model) {

        return Flux.zip(Flux.interval(Duration.ofMillis(1000)), Flux.fromStream(Stream.generate(() -> "New Notification" + new Date())))
            .map(Tuple2::getT2);

    }

}

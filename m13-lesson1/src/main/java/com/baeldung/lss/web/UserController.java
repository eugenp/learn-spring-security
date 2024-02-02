package com.baeldung.lss.web;

import java.util.List;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.baeldung.lss.model.User;
import com.baeldung.lss.service.UserServiceInterface;

@Path("")
public class UserController {

    @EJB(mappedName = "userService")
    UserServiceInterface userService;

    @GET
    @Path("/users")
    @Produces("application/xml")
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users;
    }

}

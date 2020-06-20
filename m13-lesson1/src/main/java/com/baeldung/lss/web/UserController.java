package com.baeldung.lss.web;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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

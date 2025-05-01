package com.cabin.demo.handler;

import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.client.request.UserRequestDTO;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.demo.repository.UserRepository;

public class UserHandler {

    private static final UserRepository userService = new UserRepository(HibernateUtil.getSessionFactory());


    public static void getAllUsers(Request req, Response resp) {
        try {
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void getUserById(Request req, Response resp) {
        try {
            int userId = Integer.parseInt(req.getPathParam("id"));
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void addUser(Request req, Response resp) {
        try {
            UserRequestDTO userDTO = req.getBodyAs(UserRequestDTO.class);
            if (userDTO == null) throw new IllegalArgumentException("Invalid request body");
            boolean isCreated = userService.saveUser(userDTO);
            if (isCreated) {
                resp.setStatusCode(201);
                resp.writeBody("User created successfully");
            } else {
                resp.setStatusCode(400);
                resp.writeBody("Failed to create user");
            }
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}

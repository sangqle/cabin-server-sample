package com.cabin.demo.handler;

import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.request.UserRequestDTO;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.demo.repository.UserService;

public class UserHandler {

    private static final UserService userService = new UserService(HibernateUtil.getSessionFactory());

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
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}

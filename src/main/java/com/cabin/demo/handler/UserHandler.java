package com.cabin.demo.handler;

import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.dto.request.UserRequestDTO;
import com.cabin.demo.dto.response.UserResponseDTO;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.demo.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class UserHandler {

    private static final UserService userService = new UserService(HibernateUtil.getSessionFactory());

    public static void getAllUsers(Request req, Response resp) {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            ApiResponse<List<UserResponseDTO>> response = ApiResponse.success(users);
            resp.writeBody(response);
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void getUserById(Request req, Response resp) {
        try {
            int userId = Integer.parseInt(req.getPathParam("id"));
            UserResponseDTO user = userService.findUserById(userId);
            if (user != null) {
                ApiResponse<UserResponseDTO> response = ApiResponse.success(user);
                resp.writeBody(response);
            } else {
                throw new NoSuchElementException();
            }
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

            if (userService.findByEmail(userDTO.getEmail()) != null) {
                resp.setStatusCode(400);
                ApiResponse<String> response = ApiResponse.error("User with the same email already exists");
                resp.writeBody(response);
                return;
            }

            boolean success = userService.createUser(userDTO);
            if (success) {
                resp.setStatusCode(201);
                ApiResponse<String> response = ApiResponse.success("User created successfully");
                resp.writeBody(response);
            } else {
                throw new RuntimeException("Failed to insert user");
            }
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}

package com.cabin.express.handler;

import com.cabin.express.datasource.HibernateUtil;
import com.cabin.express.dto.request.UserRequestDTO;
import com.cabin.express.dto.response.UserResponseDTO;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import com.cabin.express.service.UserService;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

public class UserHandler {

    private static final UserService userService = new UserService(HibernateUtil.getSessionFactory());

    public static void getAllUsers(Request req, Response resp) throws IOException {
        List<UserResponseDTO> users = userService.getAllUsers();
        resp.writeJsonBody(users);
        resp.setStatusCode(200);
        resp.send();
    }

    public static void addUser(Request req, Response resp) {
        try {
            UserRequestDTO userRequestDTO = req.getBodyAs(UserRequestDTO.class);
            if (userRequestDTO == null || userRequestDTO.getName() == null || userRequestDTO.getEmail() == null) {
                JsonObject error = new JsonObject();
                error.addProperty("error", "Invalid request body");
                resp.writeJsonBody(error);
                resp.setStatusCode(400);
            }
            boolean user = userService.createUser(userRequestDTO);
            if (user) {
                resp.setStatusCode(201);
                resp.writeBody("{\"message\": \"User created successfully\"}");
            } else {
                resp.setStatusCode(500);
                resp.writeBody("{\"error\": \"Error creating user\"}");

            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatusCode(500);
            resp.writeBody("{\"error\": \"Error processing request\"}");
        }
        resp.send();
    }
}

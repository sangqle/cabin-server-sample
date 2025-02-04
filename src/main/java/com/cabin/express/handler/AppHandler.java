package com.cabin.express.handler;

import com.cabin.express.dao.UserDAO;
import com.cabin.express.dto.UserDTO;
import com.cabin.express.entity.User;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AppHandler {

    public static void hello(Request req, Response resp) {
        resp.writeBody("Hello, world!");
        resp.send();
    }

    public static void getUsers(Request req, Response resp) {
        try {
            UserDAO userDAO = new UserDAO();
            List<User> allUsers = userDAO.getAllUsers();
            HashMap<String, List<User>> data = new HashMap<>();
            data.put("users", allUsers);
            resp.writeJsonBody(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.send();
    }

    public static void addUser(Request req, Response resp) {
        try {
            UserDAO userDAO = new UserDAO();
            UserDTO userDTO = req.getBodyAs(UserDTO.class);
            if (userDTO == null || userDTO.getName() == null || userDTO.getEmail() == null) {
                resp.setStatusCode(400);
                resp.writeBody("{\"error\": \"Missing required fields: name and email\"}");
                resp.send();
                return;
            }
            User user = new User(userDTO.getName(), userDTO.getEmail());
            boolean success = userDAO.insertUser(user);
            if (success) {
                resp.setStatusCode(201);
                resp.writeBody("{\"message\": \"User created successfully\"}");
            } else {
                resp.setStatusCode(500);
                resp.writeBody("{\"error\": \"Failed to insert user\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatusCode(500);
            resp.writeBody("{\"error\": \"Error processing request\"}");
        }
        resp.send();
    }
}

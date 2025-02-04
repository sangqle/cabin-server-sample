package com.cabin.express.handler;

import com.cabin.express.dao.UserDAO;
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
}

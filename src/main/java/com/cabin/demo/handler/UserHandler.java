package com.cabin.demo.handler;

import com.cabin.demo.dto.ApiResponse;
import com.cabin.demo.dto.UserDto;
import com.cabin.demo.dto.client.request.UserLoginRequest;
import com.cabin.demo.dto.client.request.UserRequestDTO;
import com.cabin.demo.exception.GlobalExceptionHandler;
import com.cabin.demo.services.UserService;
import com.cabin.express.http.Request;
import com.cabin.express.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class UserHandler {

    private static final Logger _logger = LoggerFactory.getLogger(UserHandler.class);

    private static void sendSuccessResponse(Response resp, Object data) throws IOException {
        ApiResponse<Object> success = ApiResponse.success(data);
        resp.writeBody(success);
        resp.send();
    }

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
            _logger.error("Error while getting user by id: {}", e.getMessage());
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void register(Request req, Response resp) {
        try {
            UserRequestDTO userDTO = req.getBodyAs(UserRequestDTO.class);
            if (userDTO == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
                resp.setStatusCode(400);
                resp.writeBody("Invalid request body");
                return;
            }
            // Call the service to register the user
            int register = UserService.INSTANCE.register(userDTO);
            if (register > 0) {
                sendSuccessResponse(resp, "Register success");
            } else {
                resp.setStatusCode(500);
                resp.writeBody("User registration failed");
            }
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }

    public static void login(Request req, Response resp) {
        try {
            UserLoginRequest userLoginRequest = req.getBodyAs(UserLoginRequest.class);
            if (userLoginRequest == null || userLoginRequest.getEmail() == null || userLoginRequest.getPassword() == null) {
                resp.setStatusCode(400);
                resp.writeBody("Invalid request body");
                return;
            }
            // Call the service to login the user
            UserDto login = UserService.INSTANCE.login(userLoginRequest);
            if (login != null) {
                sendSuccessResponse(resp, login);
            } else {
                resp.setStatusCode(401);
                resp.writeBody("Invalid email or password");
            }
        } catch (Exception e) {
            _logger.error("Error while logging in: {}", e.getMessage());
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}

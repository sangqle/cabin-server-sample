package com.cabin.express.service;

import com.cabin.express.dao.UserDAO;
import com.cabin.express.dto.request.UserRequestDTO;
import com.cabin.express.dto.response.UserResponseDTO;
import com.cabin.express.entity.User;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public boolean createUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO.getName() == null || userRequestDTO.getEmail() == null) {
            throw new IllegalArgumentException("Missing required fields: name and email");
        }
        User user = new User(userRequestDTO.getName(), userRequestDTO.getEmail());
        return userDAO.save(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userDAO.findAll();
        return users.stream().map(UserResponseDTO::new).toList();
    }

    public UserResponseDTO findUserById(int userId) {
        User user = userDAO.findById(userId);
        return user != null ? new UserResponseDTO(user) : null;
    }

    public boolean updateUser(int userId, UserRequestDTO userRequestDTO) {
        User existingUser = userDAO.findById(userId);
        if (existingUser == null) {
            return false;
        }

        existingUser.setName(userRequestDTO.getName());
        existingUser.setEmail(userRequestDTO.getEmail());

        return userDAO.update(existingUser);
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userDAO.findByEmail(email);
        return user != null ? new UserResponseDTO(user) : null;
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteById(userId);
    }
}
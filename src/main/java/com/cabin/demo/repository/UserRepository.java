package com.cabin.demo.repository;

import com.cabin.demo.dao.UserDAO;
import com.cabin.demo.dto.request.UserRequestDTO;
import com.cabin.demo.entity.auth.User;
import org.hibernate.SessionFactory;

public class UserRepository {
    private final UserDAO userDAO;

    public UserRepository(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public boolean saveUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        // Map fields from userRequestDTO to user entity
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        // ... set other fields as needed
        return userDAO.save(user);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteById(userId);
    }

    public User getUserById(int userId) {
        return userDAO.findById(userId);
    }
}
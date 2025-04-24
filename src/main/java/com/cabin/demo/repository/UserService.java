package com.cabin.demo.repository;

import com.cabin.demo.dao.UserDAO;
import com.cabin.demo.dto.request.UserRequestDTO;
import org.hibernate.SessionFactory;

public class UserService {
    private final UserDAO userDAO;

    public UserService(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public boolean createUser(UserRequestDTO userRequestDTO) {
        return true;
    }
    public boolean deleteUser(int userId) {
        return userDAO.deleteById(userId);
    }
}
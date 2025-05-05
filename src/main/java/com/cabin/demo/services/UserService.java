package com.cabin.demo.services;

import com.cabin.demo.dao.UserDao;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.client.request.UserRequestDTO;
import com.cabin.demo.entity.auth.AuthProvider;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.entity.auth.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mindrot.jbcrypt.BCrypt.gensalt;
import static org.mindrot.jbcrypt.BCrypt.hashpw;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao = new UserDao(HibernateUtil.getSessionFactory());

    private UserService() {
    }

    public static final UserService INSTANCE = new UserService();

    public int register(UserRequestDTO userDto) {
        // Check if email already exists
        User existingUser = userDao.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            log.warn("Registration failed: Email already in use: {}", userDto.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        // Create user auth for password authentication
        UserAuth userAuth = new UserAuth();
        userAuth.setProvider(AuthProvider.LOCAL);
        userAuth.setProviderUserId(userDto.getEmail());
        userAuth.setPasswordHash(hashPassword(userDto.getPassword()));
        userAuth.setUser(user);

        // Setup bidirectional relationship
        user.getAuthMethods().add(userAuth);

        // Save the user (cascade will save userAuth)
        userDao.save(user);

        log.info("User registered successfully: {}", user.getEmail());
        return 1;
    }

    /**
     * Hash password using BCrypt
     */
    private String hashPassword(String password) {
        // This would ideally use BCrypt or similar
        return hashpw(password, gensalt(12));
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }
}

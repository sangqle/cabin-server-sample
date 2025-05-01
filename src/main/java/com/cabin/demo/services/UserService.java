package com.cabin.demo.services;

import com.cabin.demo.dao.UserDao;
import com.cabin.demo.datasource.HibernateUtil;
import com.cabin.demo.dto.UserDto;
import com.cabin.demo.entity.auth.User;
import com.cabin.demo.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao = new UserDao(HibernateUtil.getSessionFactory());

    private UserService() {
    }

    public static final UserService INSTANCE = new UserService();

//    public UserDto getUserById(Long id) {
//        User byId = userDao.findById(id);
//        if (byId == null) {
//            return null;
//        }
//        return UserMapper.INSTANCE.toDto(byId);
//    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }
}

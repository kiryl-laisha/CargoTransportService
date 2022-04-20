package com.laisha.cts.model.service.impl;

import com.laisha.cts.model.dao.impl.UserDaoImpl;
import com.laisha.cts.entity.User;
import com.laisha.cts.exception.DaoException;
import com.laisha.cts.exception.ServiceException;
import com.laisha.cts.model.service.UserService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger();
    private static final UserServiceImpl instance = new UserServiceImpl();
    private static final UserDaoImpl userDao = UserDaoImpl.getInstance();

    private UserServiceImpl() {
    }

    public static UserServiceImpl getInstance() {
        return instance;
    }

    @Override
    public Optional<User> authenticate(String login, String password) throws ServiceException {

        Optional<String> optionalPassword;
        try {
            optionalPassword = userDao.findUserPasswordByLogin(login);
        } catch (DaoException e) {
            throw new ServiceException("User authentication failed.", e);
        }
        if (optionalPassword.isEmpty()) {
            logger.log(Level.DEBUG, "Password don't exist for provided login.");
            return Optional.empty();
        }
        long userId;
        try {
            userId = userDao.findUserIdByLogin(login);
        } catch (DaoException e) {
            throw new ServiceException("User authentication failed.", e);
        }


        return Optional.empty();
    }
}

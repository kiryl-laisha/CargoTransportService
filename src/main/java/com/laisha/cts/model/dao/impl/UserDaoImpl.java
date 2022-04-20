package com.laisha.cts.model.dao.impl;

import com.laisha.cts.model.connection.ConnectionPool;
import com.laisha.cts.model.dao.BaseDao;
import com.laisha.cts.model.dao.ColumnName;
import com.laisha.cts.model.dao.UserDao;
import com.laisha.cts.entity.User;
import com.laisha.cts.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends BaseDao<User> implements UserDao {

    private static final Logger logger = LogManager.getLogger();
    private static final UserDaoImpl instance = new UserDaoImpl();
    private static final String SELECT_USER_PASSWORD_BY_LOGIN = "SELECT password FROM users WHERE login = ?";
    private static final String SELECT_USER_ID_BY_LOGIN = "SELECT user_id FROM users WHERE login = ?";

    private UserDaoImpl() {
    }

    public static UserDaoImpl getInstance() {
        return instance;
    }

    @Override
    public boolean insert(User user) throws DaoException {
        return false;
    }

    @Override
    public boolean delete(User user) throws DaoException {
        return false;
    }

    @Override
    public List<User> findAll() throws DaoException {
        return null;
    }

    @Override
    public User update(User user) throws DaoException {
        return null;
    }

    @Override
    public boolean authenticate(String login, String password) throws DaoException {


        return false;
    }

    @Override
    public Optional<String> findUserPasswordByLogin(String login) throws DaoException {

        Optional<String> optionalPassword = Optional.empty();
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_PASSWORD_BY_LOGIN)) {
            statement.setString(1, login);
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    optionalPassword = Optional.of(resultSet.getString(ColumnName.PASSWORD));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Finding user password by user login failed.", e);
        }
        return optionalPassword;
    }

    public long findUserIdByLogin(String login) throws DaoException{

        long userId = 0;
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_ID_BY_LOGIN)) {
            statement.setString(1, login);
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    userId = resultSet.getLong(ColumnName.USER_ID);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Finding user id by user login failed.", e);
        }
        return userId;
    }
}

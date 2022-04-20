package com.laisha.cargotransportservice.dao;

import com.laisha.cargotransportservice.exception.DaoException;

import java.util.Optional;

public interface UserDao {

    boolean authenticate(String login, String password) throws DaoException;

    Optional<String> findUserPasswordByLogin(String login) throws DaoException;

    long findUserIdByLogin(String login) throws DaoException;
}

package com.laisha.cts.model.service;

import com.laisha.cts.entity.User;
import com.laisha.cts.exception.ServiceException;

import java.util.Optional;

public interface UserService {

    Optional<User> authenticate(String login, String password) throws ServiceException;
}

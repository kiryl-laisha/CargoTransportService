package com.laisha.cargotransportservice.service;

import com.laisha.cargotransportservice.entity.User;
import com.laisha.cargotransportservice.exception.ServiceException;

import java.util.Optional;

public interface UserService {

    Optional<User> authenticate(String login, String password) throws ServiceException;
}

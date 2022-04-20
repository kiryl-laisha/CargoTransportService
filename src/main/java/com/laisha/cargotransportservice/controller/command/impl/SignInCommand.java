package com.laisha.cargotransportservice.controller.command.impl;

import com.laisha.cargotransportservice.controller.command.Command;
import com.laisha.cargotransportservice.controller.command.PagePath;
import com.laisha.cargotransportservice.controller.command.RequestParameter;
import com.laisha.cargotransportservice.controller.command.Router;
import com.laisha.cargotransportservice.entity.User;
import com.laisha.cargotransportservice.exception.CommandException;
import com.laisha.cargotransportservice.exception.ServiceException;
import com.laisha.cargotransportservice.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class SignInCommand implements Command {

    private static final UserServiceImpl userService = UserServiceImpl.getInstance();

    @Override
    public Router execute(HttpServletRequest request) throws CommandException {

        String login = request.getParameter(RequestParameter.LOGIN);
        String password = request.getParameter(RequestParameter.PASSWORD);
        Optional<User> optionalUser;
        try {
            optionalUser = userService.authenticate(login, password);
        } catch (ServiceException e) {
            throw new CommandException("Sign in command was failed.", e);
        }
        if (optionalUser.isEmpty()) {
            return new Router(PagePath.SIGN_IN);
        }
        return new Router(PagePath.USER_HOME);
    }
}

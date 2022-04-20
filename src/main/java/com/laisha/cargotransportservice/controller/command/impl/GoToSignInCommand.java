package com.laisha.cargotransportservice.controller.command.impl;

import com.laisha.cargotransportservice.controller.command.Command;
import com.laisha.cargotransportservice.controller.command.PagePath;
import com.laisha.cargotransportservice.controller.command.Router;
import com.laisha.cargotransportservice.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class GoToSignInCommand implements Command {

    @Override
    public Router execute(HttpServletRequest request) throws CommandException {
        return new Router(PagePath.SIGN_IN);
    }
}

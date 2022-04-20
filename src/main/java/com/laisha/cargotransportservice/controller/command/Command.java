package com.laisha.cargotransportservice.controller.command;

import com.laisha.cargotransportservice.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface Command {

    Router execute(HttpServletRequest request) throws CommandException;
}

package com.laisha.cargotransportservice.controller;

import com.laisha.cargotransportservice.controller.command.*;
import com.laisha.cargotransportservice.connection.ConnectionPool;
import com.laisha.cargotransportservice.exception.CommandException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(name = "CtsServlet", value = "/controller")
public class Controller extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();

/*
    public void init() {
    }
*/

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String stringCommandType = request.getParameter(RequestParameter.COMMAND);
        Command command = CommandType.defineCommand(stringCommandType);
        try {
            Router router = command.execute(request);
            switch (router.getRouterType()) {
                case FORWARD -> request.getRequestDispatcher(router.getPagePath()).forward(request, response);
                case REDIRECT -> response.sendRedirect(request.getContextPath() + router.getRouterType());
                default -> request.getRequestDispatcher(PagePath.ERROR_404).forward(request, response);
            }
        } catch (CommandException e) {
            logger.log(Level.WARN, "error", e);
            response.sendRedirect("");
        }
    }

    @Override
    public void destroy() {
        ConnectionPool.getInstance().destroyConnectionPool();
    }
}
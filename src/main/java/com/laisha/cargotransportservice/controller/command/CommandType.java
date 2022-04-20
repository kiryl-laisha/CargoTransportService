package com.laisha.cargotransportservice.controller.command;

import com.laisha.cargotransportservice.controller.command.impl.DefaultCommand;
import com.laisha.cargotransportservice.controller.command.impl.GoToSignInCommand;
import com.laisha.cargotransportservice.controller.command.impl.SignInCommand;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum CommandType {

    DEFAULT(new DefaultCommand()),
    SIGN_IN(new SignInCommand()),
    GO_TO_SIGN_IN(new GoToSignInCommand());

    private static final Logger logger = LogManager.getLogger();
    final Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public static Command defineCommand(String stringCommandType) {

        if (stringCommandType == null || stringCommandType.isBlank()) {
            logger.log(Level.WARN, "Provided string with command type is null or empty.");
            return CommandType.DEFAULT.command;
        }
        CommandType commandType;
        try {
            commandType = CommandType.valueOf(stringCommandType.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARN, "Command type don't present.", e);
            return CommandType.DEFAULT.command;
        }
        return commandType.command;
    }
}

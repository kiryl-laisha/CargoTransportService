package com.laisha.cargotransportservice.exception;

public class ConnectionCreatorException extends Exception{

    public ConnectionCreatorException() {
    }

    public ConnectionCreatorException(String message) {
        super(message);
    }

    public ConnectionCreatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionCreatorException(Throwable cause) {
        super(cause);
    }
}

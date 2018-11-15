package com.exception;

public class IllegalIndexSocketID extends  RuntimeException {
    public IllegalIndexSocketID() {
    }

    public IllegalIndexSocketID(String message) {
        super(message);
    }

    public IllegalIndexSocketID(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalIndexSocketID(Throwable cause) {
        super(cause);
    }

    public IllegalIndexSocketID(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

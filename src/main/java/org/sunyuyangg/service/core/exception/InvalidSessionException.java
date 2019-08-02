package org.sunyuyangg.service.core.exception;

public class InvalidSessionException extends Exception {
    public InvalidSessionException(String s) {
        super("Invalid session :" + s);
    }
}

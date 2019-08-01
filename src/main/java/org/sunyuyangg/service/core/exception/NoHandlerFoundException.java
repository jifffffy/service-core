package org.sunyuyangg.service.core.exception;

public class NoHandlerFoundException extends Exception {
    private String action;
    public NoHandlerFoundException(String action) {
        this.action = action;
    }

    @Override
    public String getMessage() {
        return "No handler support the " + action + ", please use help command! ";
    }
}

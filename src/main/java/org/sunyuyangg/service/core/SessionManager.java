package org.sunyuyangg.service.core;

import org.sunyuyangg.service.core.exception.InvalidSessionIDException;

import java.util.HashMap;

public interface SessionManager<T> {

    T getSession(int sessionId) throws InvalidSessionIDException;

    int addSession(T session);

    void deleteSession(int sessionId) throws InvalidSessionIDException;
}

package org.sunyuyangg.service.core.bean;

import org.sunyuyangg.service.core.exception.InvalidSessionIDException;


public interface SessionManager<T> {

    T getSession(int sessionId) throws InvalidSessionIDException;

    int addSession(T session);

    void deleteSession(int sessionId) throws InvalidSessionIDException;
}

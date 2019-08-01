package org.sunyuyangg.service.core;

public class Util {
    public static String getActionStr(String request) {
        int spaceIndex = request.indexOf(" ");
        String action;
        if (spaceIndex != -1) {
            action = request.substring(0, spaceIndex);
        } else {
            action = request;
        }
        return action.toUpperCase();
    }
}

package org.sunyuyangg.service.core.bean;

import org.sunyuyangg.service.core.exception.InvalidSessionIDException;

import java.util.Vector;

public class SampleSessionManager<T extends Session> extends Vector<T> implements SessionManager<T> {

    // This variable keeps track of the index of the first available free ID
    // This helps to keep the idices dense and acess time low.
    protected int nextNewSessionIndex;

    @Override
    public T getSession(int sessionId) throws InvalidSessionIDException{
        int index = findId(sessionId);
        return elementAt(index);
    }


    @Override
    public int addSession(T session) {
        int addIndex = nextNewSessionIndex;

        insertElementAt(session, addIndex - 1);
        nextNewSessionIndex = nextGap();

        return addIndex;
    }

    @Override
    public void deleteSession(int sessionId) throws InvalidSessionIDException {
        int index = findId(sessionId);
        removeElementAt(index);
        // update insert index
        if (sessionId < nextNewSessionIndex){
            nextNewSessionIndex = sessionId;
        }
    }


    protected int nextGap() {

        int index = nextNewSessionIndex + 1;

        while ((index <= elementCount) &&
                ((elementAt(index - 1)).getId() == index))
            index++;

        return index;
    }

    protected int findId(int id) throws InvalidSessionIDException {
        int offsetIndex = id;

        while ((offsetIndex >= 0) && (elementCount > 0)) {
            if (offsetIndex >= elementCount)
                offsetIndex = elementCount - 1;

            try {
                if ((elementAt(offsetIndex)).getId() == id)
                    return offsetIndex;
            } catch (ArrayIndexOutOfBoundsException e) {
                // This exception can occur if many sessions are being removed
                // simultaneously.  Can just ignore as the offsetIndex will be
                // reset at the top of the loop so that is no longer larger
                // than the highest index currently in the SessionList.
            }

            offsetIndex--;
        }

        throw new InvalidSessionIDException(id, "ID " + id + " is not in the Session List.");
    }
}

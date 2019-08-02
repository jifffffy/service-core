package org.sunyuyangg.service.core.exception;

/*****************************************************************************/
/* Software Testing Automation Framework (STAF)                              */
/* (C) Copyright IBM Corp. 2004                                              */
/*                                                                           */
/* This software is licensed under the Eclipse Public License (EPL) V1.0.    */
/*****************************************************************************/

/*****************************************************************************/
/*                                                                           */
/* Class: InvalidSessionIDException                                          */
/* Description: This Exception indicates that the id is not in the Session   */
/*              list                                                         */
/*                                                                           */
/*****************************************************************************/

public class InvalidSessionIDException extends Exception
{
    
/*****************************************************************************/
/*                                                                           */
/* Method: Constructor                                                       */
/* Description: Constructor method                                           */
/* Parameter: id - the invalid session id that was requested                 */
/*            s - additional message information                             */
/*                                                                           */
/*****************************************************************************/    

    public InvalidSessionIDException(int id, String s)
    {
        super("Invalid ID " + id + "\n" + s);
    }


}

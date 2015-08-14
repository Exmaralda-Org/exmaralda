package org.exmaralda.partitureditor.jexmaralda;

/*
 * JexmaraldaException.java
 *
 * Created on 12. April 2001, 09:48
 */



/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de)
 * @version 
 */
public class JexmaraldaException extends Exception {

    public int exceptionCode;
    
    /** Creates new JexmaraldaException */
    public JexmaraldaException() {
        super();
        exceptionCode = 0;
    }
    
    /** Creates new JexmaraldaException with the specified error message */
    public JexmaraldaException(String message){
        super(message);
        exceptionCode = 0;
    }
    
    public JexmaraldaException(int code, String message){
        super(message);
        exceptionCode = code;
    }
    
}
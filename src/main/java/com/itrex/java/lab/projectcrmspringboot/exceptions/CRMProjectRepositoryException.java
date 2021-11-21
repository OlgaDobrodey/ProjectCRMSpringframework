package com.itrex.java.lab.projectcrmspringboot.exceptions;

public class CRMProjectRepositoryException extends Exception {

    public CRMProjectRepositoryException(String message) {
        super(message);
    }

    public CRMProjectRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}

package org.teamchallenge.bookshop.exception;

import org.teamchallenge.bookshop.constants.ValidationConstants;

public class OldPasswordMatchException extends RuntimeException {
    public OldPasswordMatchException() {
        super(ValidationConstants.NEW_PASSWORD_CANNOT_BE_OLD_PASSWORD);
    }
}

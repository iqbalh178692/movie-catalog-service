package com.app.moviecatalog.v1.exception;

public class DuplicateMovieException extends RuntimeException {

    public DuplicateMovieException(String message) {
        super(message);
    }
}

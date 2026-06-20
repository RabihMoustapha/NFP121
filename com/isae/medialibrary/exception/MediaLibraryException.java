package com.isae.medialibrary.exception;

public class MediaLibraryException extends RuntimeException {
    public MediaLibraryException(String message) { super(message); }
    public MediaLibraryException(String message, Throwable cause) { super(message, cause); }
}
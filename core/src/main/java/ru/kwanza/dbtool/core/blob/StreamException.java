package ru.kwanza.dbtool.core.blob;

/**
 * @author: Ivan Baluk
 */
abstract class StreamException extends Exception {
    protected StreamException(String message) {
        super(message);
    }

    static class RecordNotFoundException extends StreamException {

        RecordNotFoundException(String message) {
            super(message);
        }
    }

    static class EmptyFieldException extends StreamException {

        EmptyFieldException(String message) {
            super(message);
        }
    }

}

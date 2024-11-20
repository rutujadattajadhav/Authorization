package com.rutuja.authorization.exceptions;
    public class MySqlConnectionClosedException extends RuntimeException {

        public MySqlConnectionClosedException() {
            super();
        }

        public MySqlConnectionClosedException(String message) {
            super(message);
        }

        public MySqlConnectionClosedException(String message, Throwable cause) {
            super(message, cause);
        }

        public MySqlConnectionClosedException(Throwable cause) {
            super(cause);
        }
    }

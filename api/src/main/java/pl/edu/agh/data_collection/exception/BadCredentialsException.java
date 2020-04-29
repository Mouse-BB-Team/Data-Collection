package pl.edu.agh.data_collection.exception;

public class BadCredentialsException extends Exception{
    public BadCredentialsException(ExceptionMessage message) {
        super(message.toString());
    }

    public enum ExceptionMessage{
        BAD_LOGIN_OR_PASSWORD("wrong login or password"),
        USER_ALREADY_EXISTS("user already exists");

        private final String message;

        ExceptionMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}

package com.japhet_sebastian.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

public class ErrorResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorId;
    private List<ErrorMessage> errors;

    public ErrorResponse(String errorId, ErrorMessage errorMessage) {
        this.errorId = errorId;
        this.errors = List.of(errorMessage);
    }

    public ErrorResponse(ErrorMessage errorMessage) {
        this(null, errorMessage);
    }

    public ErrorResponse(List<ErrorMessage> errors) {
        this.errorId = null;
        this.errors = errors;
    }

    public ErrorResponse() {
    }

    public static class ErrorMessage {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String path;
        private String message;

        public ErrorMessage(String path, String message) {
            this.path = path;
            this.message = message;
        }

        public ErrorMessage(String message) {
            this.path = null;
            this.message = message;
        }

        public ErrorMessage() {
        }

        public String getPath() {
            return path;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ErrorMessage that)) return false;
            return Objects.equals(getPath(), that.getPath()) && Objects.equals(getMessage(), that.getMessage());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPath(), getMessage());
        }
    }

    public String getErrorId() {
        return errorId;
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse that)) return false;
        return Objects.equals(getErrorId(), that.getErrorId()) && Objects.equals(getErrors(), that.getErrors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorId(), getErrors());
    }
}

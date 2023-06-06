package se.pbt.stepcounter.exception.handler;

public class ErrorResponse<T> {
    private int status;
    private String message;
    private long timeStamp;
    private T details;

    public ErrorResponse(int status, String message, long timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public ErrorResponse(int status, String message, long timeStamp, T details) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public T getDetails() {
        return details;
    }

    public void setDetails(T details) {
        this.details = details;
    }
}


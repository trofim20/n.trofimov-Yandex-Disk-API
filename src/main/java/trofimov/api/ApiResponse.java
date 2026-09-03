package trofimov.api;

import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final int statusCode;
    private final T body;

    public ApiResponse(int statusCode, T body) {
        this.statusCode = statusCode;
        this.body = body;
    }
}

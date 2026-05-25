package com.gimnasio.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean success;
    private String mensaje;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse() {}

    public ApiResponse(boolean success, String mensaje, T data) {
        this.success = success;
        this.mensaje = mensaje;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return new ApiResponse<>(true, mensaje, data);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

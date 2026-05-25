package com.gimnasio.dto;

public class MessageResponse {

    private String mensaje;

    public MessageResponse() {}

    public MessageResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}

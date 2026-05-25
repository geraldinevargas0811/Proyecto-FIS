package com.gimnasio.dto;

import java.util.List;

public class MembresiasGroupedResponse {
    private List<MembresiaResponse> activas;
    private List<MembresiaResponse> pendientes;
    private List<MembresiaResponse> vencidas;

    public MembresiasGroupedResponse() {}

    public MembresiasGroupedResponse(List<MembresiaResponse> activas, List<MembresiaResponse> pendientes, List<MembresiaResponse> vencidas) {
        this.activas = activas;
        this.pendientes = pendientes;
        this.vencidas = vencidas;
    }

    public List<MembresiaResponse> getActivas() { return activas; }
    public void setActivas(List<MembresiaResponse> activas) { this.activas = activas; }
    public List<MembresiaResponse> getPendientes() { return pendientes; }
    public void setPendientes(List<MembresiaResponse> pendientes) { this.pendientes = pendientes; }
    public List<MembresiaResponse> getVencidas() { return vencidas; }
    public void setVencidas(List<MembresiaResponse> vencidas) { this.vencidas = vencidas; }
}

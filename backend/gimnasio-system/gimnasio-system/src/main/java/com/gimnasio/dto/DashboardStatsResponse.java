package com.gimnasio.dto;

public class DashboardStatsResponse {
    private int totalClientes;
    private int totalInstructores;
    private int totalPagosPendientes;
    private int totalMembresiasActivas;

    public DashboardStatsResponse() {}

    public DashboardStatsResponse(int totalClientes, int totalInstructores, int totalPagosPendientes, int totalMembresiasActivas) {
        this.totalClientes = totalClientes;
        this.totalInstructores = totalInstructores;
        this.totalPagosPendientes = totalPagosPendientes;
        this.totalMembresiasActivas = totalMembresiasActivas;
    }

    public int getTotalClientes() { return totalClientes; }
    public void setTotalClientes(int totalClientes) { this.totalClientes = totalClientes; }
    public int getTotalInstructores() { return totalInstructores; }
    public void setTotalInstructores(int totalInstructores) { this.totalInstructores = totalInstructores; }
    public int getTotalPagosPendientes() { return totalPagosPendientes; }
    public void setTotalPagosPendientes(int totalPagosPendientes) { this.totalPagosPendientes = totalPagosPendientes; }
    public int getTotalMembresiasActivas() { return totalMembresiasActivas; }
    public void setTotalMembresiasActivas(int totalMembresiasActivas) { this.totalMembresiasActivas = totalMembresiasActivas; }
}

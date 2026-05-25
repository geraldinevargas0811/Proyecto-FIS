package com.gimnasio.dto;

import java.math.BigDecimal;

public class PlanResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionMeses;
    private BigDecimal precio;
    private String tipo;
    private String beneficios;
    private boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getDuracionMeses() { return duracionMeses; }
    public void setDuracionMeses(Integer duracionMeses) { this.duracionMeses = duracionMeses; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getBeneficios() { return beneficios; }
    public void setBeneficios(String beneficios) { this.beneficios = beneficios; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}

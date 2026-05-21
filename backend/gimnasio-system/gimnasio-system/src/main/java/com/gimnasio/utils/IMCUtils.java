package com.gimnasio.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IMCUtils {

    public static BigDecimal calcular(BigDecimal peso, BigDecimal altura) {
        if (altura == null || altura.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return peso.divide(altura.multiply(altura), 2, RoundingMode.HALF_UP);
    }

    public static String obtenerCategoria(BigDecimal imc) {
        if (imc == null) return "Sin datos";
        double val = imc.doubleValue();
        if (val < 18.5) return "Bajo peso";
        if (val < 25) return "Peso normal";
        if (val < 30) return "Sobrepeso";
        return "Obesidad";
    }
}
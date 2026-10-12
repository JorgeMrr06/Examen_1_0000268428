package mx.itson.luz.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Recibo de consumo local; los importes se calculan sin aritmética de punto flotante. */
public record Consumo(String id, Cliente cliente, String periodo,
        int lecturaAnterior, int lecturaActual, BigDecimal tarifa,
        BigDecimal cargoFijo, BigDecimal tasaIva) {
    public int kilovatiosHora() { return lecturaActual - lecturaAnterior; }
    public BigDecimal subtotal() {
        return tarifa.multiply(BigDecimal.valueOf(kilovatiosHora()))
                .add(cargoFijo).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal iva() { return subtotal().multiply(tasaIva).setScale(2, RoundingMode.HALF_UP); }
    public BigDecimal total() { return subtotal().add(iva()); }
}

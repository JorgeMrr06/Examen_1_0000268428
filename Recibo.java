package mx.itson.luz.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Comprobante de simulación; nunca contiene el número completo de tarjeta. */
public record Recibo(String folio, Consumo consumo, LocalDateTime fechaPago,
        String tarjetaEnmascarada, BigDecimal total) { }

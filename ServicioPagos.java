package mx.itson.luz.servicio;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.ErrorPago;
import mx.itson.luz.modelo.Recibo;

/** Simulador local: la tarjeta 4000 0000 0000 0002 simula rechazo. */
public final class ServicioPagos {
    private final ValidadorTarjeta validador;
    private final Map<String, Recibo> recibos = new LinkedHashMap<>();
    public ServicioPagos(ValidadorTarjeta validador) { this.validador = validador; }
    public synchronized Recibo pagar(Consumo consumo, String tarjeta) {
        if (consumo == null) throw new ErrorPago(ErrorPago.Codigo.SIN_SELECCION, "Selecciona un cliente antes de pagar.");
        if (recibos.containsKey(consumo.id())) throw new ErrorPago(ErrorPago.Codigo.DUPLICADO, "Este consumo ya está pagado en esta sesión.");
        String numero = validador.validar(tarjeta);
        if (numero.equals("4000000000000002")) throw new ErrorPago(ErrorPago.Codigo.RECHAZADO,
                "La tarjeta de prueba fue rechazada. El consumo sigue pendiente; puedes intentar otra tarjeta.");
        String folio = String.format("SIM-LUZ-%06d", recibos.size() + 1);
        Recibo recibo = new Recibo(folio, consumo, LocalDateTime.now(), validador.enmascarar(numero), consumo.total());
        recibos.put(consumo.id(), recibo);
        return recibo;
    }
    public synchronized Recibo reciboDe(Consumo consumo) { return recibos.get(consumo.id()); }
}

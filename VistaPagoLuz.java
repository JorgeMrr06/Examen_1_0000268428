package mx.itson.luz.vista;

import java.util.List;
import java.util.function.Consumer;
import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.Recibo;

public interface VistaPagoLuz {
    void establecerAcciones(Consumer<String> buscar, Consumer<Cliente> seleccionar, Consumer<String> pagar);
    void mostrarClientes(List<Cliente> clientes);
    void mostrarConsumo(Consumo consumo);
    void mostrarRecibo(Recibo recibo);
    void mostrarMensaje(String mensaje, boolean error);
    void habilitarPago(boolean habilitado);
    void limpiarTarjeta();
}

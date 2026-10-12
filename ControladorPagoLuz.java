package mx.itson.luz.controlador;

import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.ErrorPago;
import mx.itson.luz.modelo.Recibo;
import mx.itson.luz.repositorio.RepositorioClientes;
import mx.itson.luz.servicio.ServicioPagos;
import mx.itson.luz.vista.VistaPagoLuz;

/** Orquesta el caso de uso; no calcula importes ni dibuja componentes. */
public final class ControladorPagoLuz {
    private final RepositorioClientes repositorio;
    private final ServicioPagos pagos;
    private final VistaPagoLuz vista;
    private Consumo consumoSeleccionado;
    public ControladorPagoLuz(RepositorioClientes repositorio, ServicioPagos pagos, VistaPagoLuz vista) {
        this.repositorio = repositorio; this.pagos = pagos; this.vista = vista;
        vista.establecerAcciones(this::buscarClientes, this::seleccionarCliente, this::pagar);
        vista.habilitarPago(false);
    }
    public void buscarClientes(String criterio) {
        consumoSeleccionado = null;
        vista.habilitarPago(false);
        vista.limpiarTarjeta();
        vista.mostrarConsumo(null);
        var clientes = repositorio.buscarPorNumero(criterio);
        vista.mostrarClientes(clientes);
        if (criterio == null || criterio.isBlank()) vista.mostrarMensaje("Escribe el número de servicio para buscar un cliente.", false);
        else if (!criterio.trim().matches("\\d{1,12}")) vista.mostrarMensaje("El número de servicio admite hasta 12 dígitos.", true);
        else if (clientes.isEmpty()) vista.mostrarMensaje("No hay clientes que coincidan con ese número.", false);
        else vista.mostrarMensaje("Selecciona un cliente en la lista de la derecha.", false);
    }
    public void seleccionarCliente(Cliente cliente) {
        if (cliente == null) return;
        consumoSeleccionado = repositorio.consumoDe(cliente);
        vista.limpiarTarjeta();
        Recibo existente = pagos.reciboDe(consumoSeleccionado);
        if (existente != null) {
            vista.mostrarRecibo(existente);
            vista.habilitarPago(false);
            vista.mostrarMensaje("Este consumo ya está pagado. Se muestra su comprobante.", false);
        } else {
            vista.mostrarConsumo(consumoSeleccionado);
            vista.habilitarPago(true);
            vista.mostrarMensaje("Revisa el consumo e ingresa una tarjeta de prueba.", false);
        }
    }
    public void pagar(String tarjeta) {
        try {
            Recibo recibo = pagos.pagar(consumoSeleccionado, tarjeta);
            vista.mostrarRecibo(recibo);
            vista.habilitarPago(false);
            vista.limpiarTarjeta();
            vista.mostrarMensaje("Pago simulado registrado. El recibo ocupa la misma zona del consumo.", false);
        } catch (ErrorPago error) {
            vista.limpiarTarjeta();
            vista.mostrarMensaje(error.getMessage(), true);
        }
    }
}

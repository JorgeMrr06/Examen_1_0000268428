package mx.itson.luz.pruebas;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import mx.itson.luz.controlador.ControladorPagoLuz;
import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.ErrorPago;
import mx.itson.luz.modelo.Recibo;
import mx.itson.luz.repositorio.DatosClientes;
import mx.itson.luz.servicio.ServicioPagos;
import mx.itson.luz.servicio.ValidadorTarjeta;
import mx.itson.luz.vista.VistaPagoLuz;

/** Pruebas funcionales sin frameworks ni servicios externos. */
public final class PruebasLuz {
    private static int comprobaciones;
    public static void main(String[] args) throws Exception {
        DatosClientes datos = new DatosClientes();
        comprobar(datos.buscarPorNumero("").isEmpty(), "Sin criterio no expone todos los clientes");
        comprobar(datos.buscarPorNumero("12345678").size() == 3, "Búsqueda incremental por prefijo");
        comprobar(datos.buscarPorNumero("123456789").size() == 2, "Refinamiento incremental");
        comprobar(datos.buscarPorNumero("555").isEmpty(), "Número sin coincidencias");
        comprobar(datos.buscarPorNumero("abc").isEmpty(), "Servicio no numérico");
        comprobar(datos.buscarPorNumero("1234567890010").isEmpty(), "Servicio demasiado largo");
        Cliente cliente = datos.buscarPorNumero("123456789001").get(0);
        Consumo consumo = datos.consumoDe(cliente);
        comprobar(consumo.kilovatiosHora() == 150 && consumo.subtotal().equals(new BigDecimal("232.50"))
                && consumo.iva().equals(new BigDecimal("37.20")) && consumo.total().equals(new BigDecimal("269.70")),
                "Cálculo decimal completo: 150 kWh, subtotal232.50, IVA37.20, total269.70");
        ValidadorTarjeta validador = new ValidadorTarjeta();
        comprobar(validador.validar("4111-1111 1111-1111").equals("4111111111111111"), "Normalización y Luhn");
        esperar(ErrorPago.Codigo.TARJETA_INVALIDA, () -> validador.validar("4111111111111112"), "Verificador incorrecto");
        esperar(ErrorPago.Codigo.TARJETA_INVALIDA, () -> validador.validar("0000000000000000"), "Rechazo de solo ceros");
        esperar(ErrorPago.Codigo.TARJETA_INVALIDA, () -> validador.validar("123"), "Tarjeta demasiado corta");
        ServicioPagos pagos = new ServicioPagos(validador);
        esperar(ErrorPago.Codigo.SIN_SELECCION, () -> pagos.pagar(null, "4111111111111111"), "Pago sin selección");
        esperar(ErrorPago.Codigo.RECHAZADO, () -> pagos.pagar(consumo, "4000000000000002"), "Rechazo simulado");
        comprobar(pagos.reciboDe(consumo) == null, "Rechazo no registra pago");
        Recibo recibo = pagos.pagar(consumo, "4111111111111111");
        comprobar(recibo.total().equals(new BigDecimal("269.70")) && recibo.folio().equals("SIM-LUZ-000001"), "Pago y folio");
        comprobar(recibo.tarjetaEnmascarada().endsWith("1111") && !recibo.tarjetaEnmascarada().contains("411111"), "Tarjeta protegida en recibo");
        esperar(ErrorPago.Codigo.DUPLICADO, () -> pagos.pagar(consumo, "4111111111111111"), "Prevención de doble pago");
        comprobar(pagos.reciboDe(consumo) == recibo, "Comprobante recuperable durante sesión");
        probarControlador(datos);
        probarConcurrencia(consumo);
        System.out.println("RESULTADO: " + comprobaciones + " comprobaciones funcionales aprobadas.");
    }
    private static void probarControlador(DatosClientes datos) {
        VistaPrueba vista = new VistaPrueba();
        ServicioPagos pagos = new ServicioPagos(new ValidadorTarjeta());
        ControladorPagoLuz controlador = new ControladorPagoLuz(datos, pagos, vista);
        comprobar(!vista.habilitado, "Estado inicial sin pago");
        controlador.buscarClientes("123456789001");
        controlador.seleccionarCliente(vista.clientes.get(0));
        comprobar(vista.habilitado && vista.consumo != null, "Selección muestra consumo y permite pagar");
        controlador.pagar("123");
        comprobar(vista.error && vista.habilitado && vista.recibo == null, "Error de tarjeta permite corrección");
        controlador.buscarClientes("987");
        comprobar(!vista.habilitado && vista.consumo == null, "Editar servicio invalida selección previa");
        controlador.pagar("4111111111111111");
        comprobar(vista.error && vista.recibo == null, "Controlador impide pago de selección anterior");
        controlador.seleccionarCliente(vista.clientes.get(0));
        controlador.pagar("4111111111111111");
        Recibo primero = vista.recibo;
        comprobar(primero != null && !vista.habilitado && !vista.error, "Pago exitoso muestra recibo y bloquea botón");
        controlador.buscarClientes("987");
        controlador.seleccionarCliente(vista.clientes.get(0));
        comprobar(vista.recibo == primero && !vista.habilitado, "Reselección pagada recupera recibo existente");
    }
    private static void probarConcurrencia(Consumo consumo) throws Exception {
        ServicioPagos pagos = new ServicioPagos(new ValidadorTarjeta());
        CountDownLatch inicio = new CountDownLatch(1);
        AtomicInteger aceptados = new AtomicInteger();
        AtomicInteger duplicados = new AtomicInteger();
        Runnable intento = () -> {
            try { inicio.await(); pagos.pagar(consumo, "4111111111111111"); aceptados.incrementAndGet(); }
            catch (ErrorPago error) { if (error.codigo() == ErrorPago.Codigo.DUPLICADO) duplicados.incrementAndGet(); }
            catch (InterruptedException error) { Thread.currentThread().interrupt(); }
        };
        Thread a = new Thread(intento); Thread b = new Thread(intento);
        a.start(); b.start(); inicio.countDown(); a.join(); b.join();
        comprobar(aceptados.get() == 1 && duplicados.get() == 1, "Dos intentos concurrentes registran exactamente un pago");
    }
    private static void esperar(ErrorPago.Codigo codigo, Runnable accion, String nombre) {
        try { accion.run(); throw new AssertionError(nombre + ": faltó error"); }
        catch (ErrorPago error) { comprobar(error.codigo() == codigo, nombre); }
    }
    private static void comprobar(boolean condicion, String nombre) {
        if (!condicion) throw new AssertionError(nombre);
        comprobaciones++;
        System.out.println("OK " + comprobaciones + " | " + nombre);
    }
    private static final class VistaPrueba implements VistaPagoLuz {
        private List<Cliente> clientes;
        private Consumo consumo;
        private Recibo recibo;
        private boolean habilitado;
        private boolean error;
        public void establecerAcciones(Consumer<String> buscar, Consumer<Cliente> seleccionar, Consumer<String> pagar) { }
        public void mostrarClientes(List<Cliente> clientes) { this.clientes = clientes; }
        public void mostrarConsumo(Consumo consumo) { this.consumo = consumo; this.recibo = null; }
        public void mostrarRecibo(Recibo recibo) { this.recibo = recibo; }
        public void mostrarMensaje(String mensaje, boolean error) { this.error = error; }
        public void habilitarPago(boolean habilitado) { this.habilitado = habilitado; }
        public void limpiarTarjeta() { }
    }
}

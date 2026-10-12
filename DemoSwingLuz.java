package mx.itson.luz.pruebas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import mx.itson.luz.controlador.ControladorPagoLuz;
import mx.itson.luz.repositorio.DatosClientes;
import mx.itson.luz.servicio.ServicioPagos;
import mx.itson.luz.servicio.ValidadorTarjeta;
import mx.itson.luz.vista.PanelPagoLuz;

/** Ejecuta eventos Swing reales y exporta printAll sin abrir ni capturar escritorio. */
public final class DemoSwingLuz {
    private static Path destino;
    private static int frame;
    public static void main(String[] args) throws Exception {
        destino = Path.of(args.length == 0 ? "evidencias" : args[0]);
        Files.createDirectories(destino.resolve("frames"));
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        SwingUtilities.invokeAndWait(() -> {
            try { ejecutar(); }
            catch (Exception error) { throw new RuntimeException(error); }
        });
    }
    private static void ejecutar() throws Exception {
        PanelPagoLuz vista = new PanelPagoLuz();
        new ControladorPagoLuz(new DatosClientes(), new ServicioPagos(new ValidadorTarjeta()), vista);
        vista.setSize(1100, 900);
        JTextField servicio = (JTextField) buscar(vista, "numeroServicio");
        JList<?> lista = (JList<?>) buscar(vista, "listaClientes");
        JPasswordField tarjeta = (JPasswordField) buscar(vista, "tarjeta");
        JButton boton = (JButton) buscar(vista, "pagar");
        Component mismaZona = buscar(vista, "zonaConsumoRecibo");
        capturar(vista, "01_inicio.png");
        cuadro(vista, "Inicio: solicitud del número de servicio");
        for (String prefijo : new String[]{"1", "12", "12345678", "123456789", "123456789001"}) {
            servicio.setText(prefijo);
            cuadro(vista, "Búsqueda incremental: " + prefijo + " | coincidencias=" + lista.getModel().getSize());
            if (prefijo.equals("12345678")) capturar(vista, "02_busqueda.png");
        }
        if (lista.getModel().getSize() != 1) throw new AssertionError("El servicio completo debe tener una coincidencia");
        lista.setSelectedIndex(0);
        if (!boton.isEnabled()) throw new AssertionError("Seleccionar un cliente debe habilitar el pago");
        cuadro(vista, "Selección del cliente: consumo debajo | total 269.70 MXN");
        tarjeta.setText("4111111111111111");
        capturar(vista, "03_consumo_tarjeta.png");
        cuadro(vista, "Ingreso de tarjeta de prueba, oculta en pantalla");
        boton.doClick();
        if (boton.isEnabled()) throw new AssertionError("Un pago registrado debe deshabilitar pago");
        if (buscar(vista, "zonaConsumoRecibo") != mismaZona) throw new AssertionError("Debe reutilizarse la misma zona");
        capturar(vista, "04_recibo.png");
        cuadro(vista, "Pago registrado: folio SIM-LUZ-000001 | misma instancia y región del consumo");
        servicio.setText("987654321004");
        lista.setSelectedIndex(0);
        tarjeta.setText("4000000000000002");
        boton.doClick();
        if (!boton.isEnabled()) throw new AssertionError("El rechazo debe permitir reintentar");
        JLabel mensaje = (JLabel) buscar(vista, "mensajeEstado");
        if (!mensaje.getText().contains("rechazada")) throw new AssertionError("Falta aviso de rechazo");
        capturar(vista, "05_rechazo.png");
        cuadro(vista, "Flujo alterno: tarjeta de prueba rechazada; consumo pendiente");
        System.out.println("UI OK | " + frame + " cuadros PNG | búsqueda, selección, pago y rechazo por eventos reales Swing");
    }
    private static void cuadro(PanelPagoLuz vista, String evento) throws Exception {
        frame++;
        capturar(vista, "frames/frame_" + String.format("%03d", frame) + ".png");
        System.out.println("FRAME " + String.format("%03d", frame) + " | " + evento);
    }
    private static void capturar(PanelPagoLuz vista, String archivo) throws Exception {
        distribuir(vista);
        BufferedImage imagen = new BufferedImage(vista.getWidth(), vista.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graficos = imagen.createGraphics();
        graficos.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        vista.printAll(graficos);
        graficos.dispose();
        ImageIO.write(imagen, "png", destino.resolve(archivo).toFile());
    }
    private static void distribuir(Container contenedor) {
        contenedor.doLayout();
        for (Component componente : contenedor.getComponents()) if (componente instanceof Container hijo) distribuir(hijo);
    }
    private static Component buscar(Container contenedor, String nombre) {
        for (Component componente : contenedor.getComponents()) {
            if (nombre.equals(componente.getName())) return componente;
            if (componente instanceof Container hijo) {
                Component resultado = buscar(hijo, nombre);
                if (resultado != null) return resultado;
            }
        }
        return null;
    }
}

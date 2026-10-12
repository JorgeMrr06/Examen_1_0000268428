package mx.itson.luz.app;

import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import mx.itson.luz.controlador.ControladorPagoLuz;
import mx.itson.luz.repositorio.DatosClientes;
import mx.itson.luz.servicio.ServicioPagos;
import mx.itson.luz.servicio.ValidadorTarjeta;
import mx.itson.luz.vista.PanelPagoLuz;

public final class AplicacionLuz {
    private AplicacionLuz() { }
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--verificar")) {
            var repositorio = new DatosClientes();
            var consumo = repositorio.consumoDe(repositorio.buscarPorNumero("123456789001").get(0));
            var recibo = new ServicioPagos(new ValidadorTarjeta()).pagar(consumo, "4111111111111111");
            System.out.println("CU4 OK | " + recibo.folio() + " | " + recibo.total().toPlainString() + " MXN | " + recibo.tarjetaEnmascarada());
            return;
        }
        if (GraphicsEnvironment.isHeadless()) throw new IllegalStateException("La aplicación requiere un entorno gráfico. Usa --verificar para verificar el JAR sin ventana.");
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception error) { System.err.println("Se usa el aspecto Swing predeterminado."); }
            PanelPagoLuz vista = new PanelPagoLuz();
            new ControladorPagoLuz(new DatosClientes(), new ServicioPagos(new ValidadorTarjeta()), vista);
            JFrame ventana = new JFrame("CU 4 · Pago de luz · 0000268428");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(vista);
            ventana.pack();
            ventana.setMinimumSize(new java.awt.Dimension(1100, 930));
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}

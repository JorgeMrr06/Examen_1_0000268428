package mx.itson.luz.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.Recibo;

/** La misma instancia se utiliza para consumo y comprobante. */
public final class PanelDetalle extends JPanel {
    private static final Color TEXTO = new Color(26, 45, 65);
    private static final Color VERDE = new Color(0, 111, 96);
    public PanelDetalle() {
        setName("zonaConsumoRecibo");
        setLayout(new BorderLayout(20, 16));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 226, 232)), BorderFactory.createEmptyBorder(18, 22, 18, 22)));
        mostrarConsumo(null);
    }
    public void mostrarConsumo(Consumo consumo) {
        removeAll();
        add(titulo("3  ·  Detalle del consumo"), BorderLayout.NORTH);
        if (consumo == null) {
            JLabel vacio = dato("Selecciona un cliente para ver sus lecturas, consumo e importe.", 16);
            vacio.setHorizontalAlignment(JLabel.CENTER);
            vacio.setForeground(new Color(108, 124, 138));
            add(vacio, BorderLayout.CENTER);
        } else {
            JPanel informacion = cuadricula();
            informacion.add(dato("Cliente", consumo.cliente().nombre()));
            informacion.add(dato("Número de servicio", consumo.cliente().numeroServicio()));
            informacion.add(dato("Domicilio", consumo.cliente().domicilio()));
            informacion.add(dato("Periodo", consumo.periodo()));
            informacion.add(dato("Lecturas (anterior → actual)", consumo.lecturaAnterior() + " → " + consumo.lecturaActual() + " kWh"));
            informacion.add(dato("Consumo / tarifa de ejemplo", consumo.kilovatiosHora() + " kWh  ·  " + dinero(consumo.tarifa()) + " por kWh"));
            informacion.add(dato("Cargo fijo / subtotal", dinero(consumo.cargoFijo()) + " / " + dinero(consumo.subtotal())));
            informacion.add(dato("IVA de ejemplo (16%)", dinero(consumo.iva())));
            add(informacion, BorderLayout.CENTER);
            add(total("Total a pagar", consumo.total(), false), BorderLayout.SOUTH);
        }
        revalidate(); repaint();
    }
    public void mostrarRecibo(Recibo recibo) {
        removeAll();
        JLabel titulo = titulo("5  ·  Recibo de pago simulado");
        titulo.setForeground(VERDE);
        add(titulo, BorderLayout.NORTH);
        JPanel informacion = cuadricula();
        informacion.add(dato("Folio", recibo.folio()));
        informacion.add(dato("Estado", "PAGADO · SIMULACIÓN"));
        informacion.add(dato("Cliente", recibo.consumo().cliente().nombre()));
        informacion.add(dato("Número de servicio", recibo.consumo().cliente().numeroServicio()));
        informacion.add(dato("Periodo / consumo", recibo.consumo().periodo() + "  ·  " + recibo.consumo().kilovatiosHora() + " kWh"));
        informacion.add(dato("Fecha de operación", recibo.fechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        informacion.add(dato("Tarjeta", recibo.tarjetaEnmascarada()));
        informacion.add(dato("Subtotal / IVA", dinero(recibo.consumo().subtotal()) + " / " + dinero(recibo.consumo().iva())));
        add(informacion, BorderLayout.CENTER);
        add(total("Total registrado", recibo.total(), true), BorderLayout.SOUTH);
        revalidate(); repaint();
    }
    private JPanel cuadricula() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 26, 10));
        panel.setOpaque(false);
        return panel;
    }
    private JPanel dato(String etiqueta, String valor) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setOpaque(false);
        JLabel leyenda = dato(etiqueta, 12);
        leyenda.setForeground(new Color(104, 121, 134));
        panel.add(leyenda, BorderLayout.NORTH);
        panel.add(dato(valor, 15), BorderLayout.CENTER);
        return panel;
    }
    private JLabel titulo(String texto) {
        JLabel label = dato(texto, 18);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }
    private JLabel dato(String texto, int tamano) {
        JLabel label = new JLabel(texto);
        label.setForeground(TEXTO);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, tamano));
        return label;
    }
    private JPanel total(String etiqueta, BigDecimal importe, boolean pagado) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(pagado ? new Color(221, 246, 236) : new Color(237, 243, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        panel.add(dato(etiqueta, 16), BorderLayout.WEST);
        JLabel valor = titulo(dinero(importe));
        valor.setForeground(pagado ? VERDE : TEXTO);
        panel.add(valor, BorderLayout.EAST);
        return panel;
    }
    public static String dinero(BigDecimal importe) { return "$" + importe.toPlainString() + " MXN"; }
}

package mx.itson.luz.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;
import mx.itson.luz.modelo.Recibo;

public final class PanelPagoLuz extends JPanel implements VistaPagoLuz {
    private final JTextField numeroServicio = new JTextField();
    private final DefaultListModel<Cliente> modeloClientes = new DefaultListModel<>();
    private final JList<Cliente> listaClientes = new JList<>(modeloClientes);
    private final PanelDetalle zonaDetalle = new PanelDetalle();
    private final JPasswordField tarjeta = new JPasswordField();
    private final JButton pagar = new JButton("Pagar servicio de luz");
    private final JLabel mensaje = new JLabel("Escribe el número de servicio para buscar un cliente.");
    private final JLabel coincidencias = new JLabel("Sin búsqueda todavía");
    private Consumer<String> accionBuscar = valor -> { };
    private Consumer<Cliente> accionSeleccionar = valor -> { };
    private Consumer<String> accionPagar = valor -> { };
    private boolean actualizandoLista;

    public PanelPagoLuz() {
        setName("vistaPagoLuz");
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(243, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(22, 28, 20, 28));
        setPreferredSize(new Dimension(1100, 900));
        JPanel cabecera = new JPanel(new BorderLayout(0, 8));
        cabecera.setOpaque(false);
        JLabel titulo = etiqueta("Pago de servicio de luz", 27, true);
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(etiqueta("CU 4  ·  Jorge Alberto Murrieta Acedo  ·  ITSON 0000268428", 14, false), BorderLayout.CENTER);
        JLabel simulacion = etiqueta("SIMULADOR ACADÉMICO  |  Datos ficticios. Sin cobros reales.", 12, true);
        simulacion.setForeground(new Color(0, 111, 96));
        cabecera.add(simulacion, BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        JPanel busqueda = new JPanel(new GridLayout(1, 2, 18, 0));
        busqueda.setOpaque(false);
        busqueda.setPreferredSize(new Dimension(1044, 208));
        busqueda.setMaximumSize(new Dimension(Integer.MAX_VALUE, 208));
        JPanel izquierda = tarjetaPanel("1  ·  Número de servicio");
        JPanel instrucciones = new JPanel();
        instrucciones.setOpaque(false);
        instrucciones.setLayout(new BoxLayout(instrucciones, BoxLayout.Y_AXIS));
        numeroServicio.setName("numeroServicio");
        estilizarCampo(numeroServicio);
        numeroServicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        instrucciones.add(numeroServicio);
        instrucciones.add(Box.createVerticalStrut(12));
        instrucciones.add(etiqueta("Escribe hasta 12 dígitos. La lista se filtra al teclear.", 13, false));
        instrucciones.add(Box.createVerticalStrut(9));
        instrucciones.add(etiqueta("Ejemplo: 123456789001", 14, true));
        izquierda.add(instrucciones, BorderLayout.CENTER);
        JPanel derecha = tarjetaPanel("2  ·  Clientes encontrados");
        listaClientes.setName("listaClientes");
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaClientes.setFixedCellHeight(45);
        listaClientes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        listaClientes.setSelectionBackground(new Color(221, 234, 250));
        listaClientes.setSelectionForeground(new Color(26, 45, 65));
        listaClientes.setCellRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Cliente cliente) label.setText("<html><b>" + cliente.numeroServicio() + "</b><br>" + cliente.nombre() + "</html>");
                label.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
                return label;
            }
        });
        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(224, 230, 236)));
        derecha.add(scroll, BorderLayout.CENTER);
        coincidencias.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        coincidencias.setForeground(new Color(99, 116, 129));
        derecha.add(coincidencias, BorderLayout.SOUTH);
        busqueda.add(izquierda); busqueda.add(derecha);
        contenido.add(busqueda);
        contenido.add(Box.createVerticalStrut(16));
        zonaDetalle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 335));
        zonaDetalle.setPreferredSize(new Dimension(1044, 335));
        contenido.add(zonaDetalle);
        contenido.add(Box.createVerticalStrut(16));
        JPanel pago = tarjetaPanel("4  ·  Tarjeta de pago");
        pago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        JPanel formulario = new JPanel(new BorderLayout(20, 8));
        formulario.setOpaque(false);
        tarjeta.setName("tarjeta");
        tarjeta.setEchoChar('•');
        estilizarCampo(tarjeta);
        formulario.add(tarjeta, BorderLayout.CENTER);
        pagar.setName("pagar");
        pagar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pagar.setBackground(new Color(25, 83, 151));
        pagar.setForeground(Color.WHITE);
        pagar.setFocusPainted(false);
        pagar.setPreferredSize(new Dimension(230, 40));
        formulario.add(pagar, BorderLayout.EAST);
        formulario.add(etiqueta("Prueba aceptada: 4111 1111 1111 1111  ·  Rechazo: 4000 0000 0000 0002", 12, false), BorderLayout.SOUTH);
        pago.add(formulario, BorderLayout.CENTER);
        contenido.add(pago);
        add(contenido, BorderLayout.CENTER);
        mensaje.setName("mensajeEstado");
        mensaje.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        mensaje.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        add(mensaje, BorderLayout.SOUTH);

        numeroServicio.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent evento) { buscar(); }
            public void removeUpdate(DocumentEvent evento) { buscar(); }
            public void changedUpdate(DocumentEvent evento) { buscar(); }
            private void buscar() { accionBuscar.accept(numeroServicio.getText()); }
        });
        listaClientes.addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && !actualizandoLista) accionSeleccionar.accept(listaClientes.getSelectedValue());
        });
        pagar.addActionListener(evento -> {
            char[] entrada = tarjeta.getPassword();
            try { accionPagar.accept(new String(entrada)); }
            finally { java.util.Arrays.fill(entrada, '\0'); }
        });
        tarjeta.addActionListener(evento -> { if (pagar.isEnabled()) pagar.doClick(); });
    }
    private JPanel tarjetaPanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218, 226, 232)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        panel.add(etiqueta(titulo, 17, true), BorderLayout.NORTH);
        return panel;
    }
    private JLabel etiqueta(String texto, int tamano, boolean negrita) {
        JLabel label = new JLabel(texto);
        label.setForeground(new Color(26, 45, 65));
        label.setFont(new Font(Font.SANS_SERIF, negrita ? Font.BOLD : Font.PLAIN, tamano));
        return label;
    }
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 19));
        campo.setPreferredSize(new Dimension(330, 40));
        campo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(167, 185, 201)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }
    @Override public void establecerAcciones(Consumer<String> buscar, Consumer<Cliente> seleccionar, Consumer<String> pagar) {
        accionBuscar = buscar; accionSeleccionar = seleccionar; accionPagar = pagar;
    }
    @Override public void mostrarClientes(List<Cliente> clientes) {
        actualizandoLista = true;
        try { modeloClientes.clear(); clientes.forEach(modeloClientes::addElement); }
        finally { actualizandoLista = false; }
        coincidencias.setText(clientes.size() + (clientes.size() == 1 ? " coincidencia" : " coincidencias"));
    }
    @Override public void mostrarConsumo(Consumo consumo) { zonaDetalle.mostrarConsumo(consumo); }
    @Override public void mostrarRecibo(Recibo recibo) { zonaDetalle.mostrarRecibo(recibo); }
    @Override public void mostrarMensaje(String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.setForeground(error ? new Color(176, 42, 42) : new Color(46, 82, 107));
    }
    @Override public void habilitarPago(boolean habilitado) { pagar.setEnabled(habilitado); tarjeta.setEnabled(habilitado); }
    @Override public void limpiarTarjeta() { tarjeta.setText(""); }
}

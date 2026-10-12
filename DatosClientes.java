package mx.itson.luz.repositorio;

import java.math.BigDecimal;
import java.util.List;
import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;

/** Datos ficticios en memoria. No consulta servicios externos. */
public final class DatosClientes implements RepositorioClientes {
    private final List<Consumo> consumos = List.of(
        crear("123456789001", "Ana Lucía Torres", "Calle Náinari 145, Ciudad Obregón", 1200, 1350),
        crear("123456789002", "Carlos Méndez Ruiz", "Calle California 820, Ciudad Obregón", 2420, 2640),
        crear("123456780003", "Sofía Castillo Luna", "Av. Miguel Alemán 310, Ciudad Obregón", 600, 690),
        crear("987654321004", "Diego Ramírez López", "Calle Sinaloa 72, Ciudad Obregón", 3100, 3400)
    );
    private static Consumo crear(String numero, String nombre, String domicilio, int anterior, int actual) {
        Cliente cliente = new Cliente(numero, nombre, domicilio);
        return new Consumo("LUZ-2026-09-" + numero, cliente,
                "01 ago – 30 sep 2026", anterior, actual,
                new BigDecimal("1.25"), new BigDecimal("45.00"), new BigDecimal("0.16"));
    }
    @Override public List<Cliente> buscarPorNumero(String criterio) {
        String filtro = criterio == null ? "" : criterio.trim();
        if (filtro.isEmpty()) return List.of();
        if (!filtro.matches("\\d{1,12}")) return List.of();
        return consumos.stream().map(Consumo::cliente)
                .filter(c -> c.numeroServicio().startsWith(filtro)).toList();
    }
    @Override public Consumo consumoDe(Cliente cliente) {
        return consumos.stream().filter(c -> c.cliente().equals(cliente)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cliente sin consumo disponible."));
    }
}

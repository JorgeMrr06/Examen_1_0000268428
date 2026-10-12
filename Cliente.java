package mx.itson.luz.modelo;

/** Cliente asociado a un número de servicio de luz. */
public record Cliente(String numeroServicio, String nombre, String domicilio) {
    @Override public String toString() { return numeroServicio + "  ·  " + nombre; }
}

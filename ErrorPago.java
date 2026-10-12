package mx.itson.luz.modelo;

public final class ErrorPago extends RuntimeException {
    public enum Codigo { SIN_SELECCION, TARJETA_INVALIDA, RECHAZADO, DUPLICADO }
    private final Codigo codigo;
    public ErrorPago(Codigo codigo, String mensaje) { super(mensaje); this.codigo = codigo; }
    public Codigo codigo() { return codigo; }
}

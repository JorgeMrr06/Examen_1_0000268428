package mx.itson.luz.servicio;

import mx.itson.luz.modelo.ErrorPago;

/** Valida longitud y dígito verificador de Luhn. No garantiza fondos ni vigencia. */
public final class ValidadorTarjeta {
    public String validar(String entrada) {
        String numero = entrada == null ? "" : entrada.replace(" ", "").replace("-", "");
        if (!numero.matches("\\d{13,19}") || numero.chars().allMatch(c -> c == '0') || !cumpleLuhn(numero)) {
            throw new ErrorPago(ErrorPago.Codigo.TARJETA_INVALIDA,
                    "Escribe una tarjeta de prueba de 13 a 19 dígitos con verificador válido.");
        }
        return numero;
    }
    private boolean cumpleLuhn(String numero) {
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) { digito *= 2; if (digito > 9) digito -= 9; }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }
    public String enmascarar(String numero) { return "•••• •••• •••• " + numero.substring(numero.length() - 4); }
}

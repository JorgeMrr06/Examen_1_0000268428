package mx.itson.luz.repositorio;

import java.util.List;
import mx.itson.luz.modelo.Cliente;
import mx.itson.luz.modelo.Consumo;

public interface RepositorioClientes {
    List<Cliente> buscarPorNumero(String criterio);
    Consumo consumoDe(Cliente cliente);
}

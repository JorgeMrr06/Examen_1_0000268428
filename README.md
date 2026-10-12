# Publicacion del proyecto Java

Este repositorio presenta todos los archivos Java en la raiz para permitir su carga desde GitHub Web. Cada archivo mantiene su declaracion original de paquete y su contenido. Los scripts de esta raiz compilan todos los archivos con Java 17 y generan los directorios de paquetes mediante javac -d build/classes.

En Windows: build.cmd compila; probar.cmd ejecuta las pruebas; demo.cmd genera evidencia; ejecutar.cmd abre la aplicacion. En Linux/macOS usa los scripts equivalentes con sh. Los JAR compilados se crean en dist/.

**Proyecto_Java_CU4.zip** incluye la estructura convencional original src/ y tests/, los scripts originales y el JAR runnable. Puedes descargarlo y descomprimirlo para trabajar en un IDE. La documentacion que sigue describe ese proyecto convencional; las mismas clases y reglas funcionan en la version visible de este repositorio.

---
# CU 4 — Pagar servicio de Luz

**Jorge Alberto Murrieta Acedo · ITSON 0000268428**

Aplicación de escritorio en Java Swing que implementa el caso de uso 4. Al escribir el número de servicio se filtran los clientes en la lista de la derecha. La selección presenta el consumo debajo, permite capturar una tarjeta y muestra el comprobante en **la misma instancia de `PanelDetalle`** que mostraba el consumo.

## Requisitos y ejecución

Necesitas Java 17 o posterior para ejecutar el JAR. Para compilar necesitas un JDK 17 o posterior con `java`, `javac` y `jar` disponibles en PATH. En Windows también puedes definir `JAVA_HOME` apuntando al JDK.

- Windows: abre `ejecutar.cmd`, o ejecuta `java -jar dist/PagoLuz.jar`.
- Linux/macOS: ejecuta `sh ejecutar.sh`.
- Compilar: `build.cmd` en Windows, `sh build.sh` en Linux/macOS.
- Verificación sin ventana: `java -jar dist/PagoLuz.jar --verificar`.
- Pruebas: `probar.cmd` o `sh probar.sh`; incluyen exportación de capturas Swing a `evidencias/`.

El JAR incluido en `dist/PagoLuz.jar` ya está compilado para Java 17. La ejecución gráfica requiere un escritorio; `--verificar` y las pruebas funcionan también sin escritorio. No requiere Maven, Gradle, bibliotecas externas, servidor, navegador ni conexión a Internet.

## Demostración

1. Escribe `12345678`: aparecen tres clientes a la derecha. Escribe `123456789`: quedan dos.
2. Completa `123456789001` y selecciona a **Ana Lucía Torres**.
3. Comprueba las lecturas 1200 → 1350: 150 kWh. Tarifa de ejemplo $1.25/kWh, cargo fijo $45.00, subtotal $232.50, IVA de ejemplo $37.20 y total **$269.70 MXN**.
4. Introduce la tarjeta de prueba `4111 1111 1111 1111` y pulsa **Pagar servicio de luz**.
5. El panel de consumo cambia a comprobante `SIM-LUZ-000001`, con fecha de operación, tarjeta enmascarada y total. El botón queda deshabilitado.
6. Busca de nuevo el mismo servicio y selecciónalo: aparece el comprobante registrado y el pago sigue deshabilitado.

Otros servicios ficticios: `123456789002` (Carlos Méndez Ruiz), `123456780003` (Sofía Castillo Luna), `987654321004` (Diego Ramírez López).

## Flujos alternos

- `555`: ningún cliente encontrado.
- Servicio con letras o más de 12 dígitos: muestra aviso y no presenta coincidencias.
- La tarjeta `123` o una tarjeta con Luhn incorrecto: muestra error, limpia la tarjeta y permite corregir.
- La tarjeta `4000 0000 0000 0002`: simula un rechazo. No registra pago; deja el consumo disponible para reintento.
- Editar el número de servicio después de seleccionar un cliente invalida la selección y deshabilita el pago.
- Un segundo pago del mismo consumo se bloquea en la interfaz y en `ServicioPagos`, incluso ante intentos concurrentes.

## Arquitectura MVC y coherencia con UML

| Capa | Clases | Responsabilidad |
|---|---|---|
| Modelo | `Cliente`, `Consumo`, `Recibo`, `ErrorPago` | Datos, cálculo decimal de consumo y errores de dominio. |
| Repositorio | `RepositorioClientes`, `DatosClientes` | Búsqueda por prefijo y consulta de consumo local. |
| Servicios del modelo | `ServicioPagos`, `ValidadorTarjeta` | Validación Luhn, simulación, comprobantes y duplicados. |
| Vista | `VistaPagoLuz`, `PanelPagoLuz`, `PanelDetalle` | Componentes Swing, entradas y representación de estados. |
| Controlador | `ControladorPagoLuz` | Acciones `buscarClientes`, `seleccionarCliente` y `pagar`. |
| Inicio | `AplicacionLuz` | Construcción de dependencias y ventana. |

`DocumentListener` produce la búsqueda incremental. La lista dispara la selección y el botón dispara el pago mediante callbacks `Consumer` conectados por el controlador. El controlador depende de las interfaces de vista y repositorio. `PanelPagoLuz` delega la representación de consumo y comprobante a su único `PanelDetalle`. Todos los importes usan `BigDecimal` con dos decimales y redondeo `HALF_UP`.

## Alcance de la simulación

Datos, consumos y tarifas son ficticios. El porcentaje de IVA forma parte de los datos de ejemplo. **No se realizan cobros ni se consulta un banco o proveedor eléctrico.** Las tarjetas se validan solo en formato, longitud y Luhn; cualquier tarjeta con formato válido se acepta en este simulador, salvo la tarjeta de rechazo indicada. No uses tarjetas reales.

Los recibos y la prevención de duplicados viven en memoria durante la ejecución: al cerrar la aplicación se reinician. El comprobante almacena únicamente una cadena enmascarada y los últimos cuatro dígitos. El campo de tarjeta oculta lo escrito y se limpia tras cada intento. La aplicación no genera registros con tarjetas completas.

## Pruebas y evidencia reproducible

`PruebasLuz` verifica búsquedas, importes exactos, tarjetas inválidas, rechazo sin registro, pago, recibo enmascarado, duplicados, estado del controlador y dos pagos concurrentes del mismo consumo. Un fallo termina con código de salida distinto de cero.

`DemoSwingLuz` usa eventos reales de los componentes Swing: escribe criterios, selecciona la lista y pulsa el botón. Dibuja el panel con `printAll` sin capturar el escritorio, exporta cuatro estados principales y un rechazo adicional, y verifica que el mismo componente presenta consumo y recibo. La evidencia corresponde a la aplicación Java ejecutada.

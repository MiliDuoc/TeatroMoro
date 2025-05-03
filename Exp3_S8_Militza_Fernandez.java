package teatromoro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * ===============================================================
 * SISTEMA DE VENTA DE ENTRADAS - TEATRO MORO
 * ===============================================================
 * Alumna       : Militza Fernández
 * Fecha        : 28 abril 2025
 * Proyecto     : Gestión de datos con listas en Java - Semana 7
 * Lenguaje     : Java
 * Asignatura   : Fundamentos de Programación PRY2201
 *
 * REQUISITOS IDENTIFICADOS:
 * - Menú principal con 9 opciones
 * - Control de asientos (compra/reserva/eliminación)
 * - Boletas detalladas
 * - Aplicación de descuentos
 * - Validación por edad y zona
 *
 * FUNCIONALIDADES IMPLEMENTADAS:
 * ---------------------------------------------------------------
 * - Menú interactivo que permite:
 *     > Reservar entradas (con tiempo límite de 1 minuto)
 *     > Comprar entradas (con descuentos por edad)
 *     > Modificar ventas ya registradas
 *     > Eliminar entradas compradas
 *     > Confirmar reservas existentes
 *     > Imprimir boletas detalladas
 *     > Buscar entradas por número, zona o tipo de cliente
 *     > Ver promociones activas
 * - Plano del teatro con asientos numerados, reservados u ocupados
 * - Cálculo automático de precios y descuentos
 * - Boletas con fecha, zona, tipo de cliente y total
 *
 * OPTIMIZACIONES TÉCNICAS:
 * ---------------------------------------------------------------
 * ✔ Uso de estructuras de control modernas (`switch`, `for-each`)
 * ✔ Modularización del código (funciones reutilizables)
 * ✔ Variables bien organizadas (locales, estáticas e instancia)
 * ✔ Incorporación de 9 puntos de depuración
 * ✔ Separación visual de zonas en el plano de asientos
 * 
 * ===============================================================
 */


public class TeatroMoro {
// Scanner global para entrada de datos desde consola
    static Scanner scanner = new Scanner(System.in);
// Matriz de asientos (10 filas x 10 columnas) 100 asientos
    static String[][] asientos = new String[10][10];
// Lista que almacena todas las entradas compradas durante la sesión
    static List<Map<String, Object>> resumenCompras = new ArrayList<>();
    static List<Map<String, Object>> reservas = new ArrayList<>();
    static List<Map<String, Object>> comprasEnSesion = new ArrayList<>();

    static int[] ventasAsientos = new int[0];
    static int[] edadesClientes = new int[0];
    static String[] tipoClientes = new String[0];
    static List<Map<String, String>> promociones = new ArrayList<>();

    // Variables estáticas para estadísticas globales
    static int totalGeneral = 0;
    static int entradasVendidas = 0;
    static String nombreTeatro = "TEATRO MORO";

    public static void main(String[] args) {
        inicializarPlano(asientos); // Llenar el plano con zonas
        inicializarPromociones();
        
        int opcion;
            // Bienvenida e información de precios y descuentos
            System.out.println("|=================================================|");
            System.out.println("|       BIENVENIDO AL SISTEMA DE ENTRADAS         |");
            System.out.println("|                 " + nombreTeatro + "                     |");
            System.out.println("|=================================================|");
            System.out.println("| Tipos de entrada y precios (Publico General):   |");
            System.out.println("|  - VIP          : $30.000                       |");
            System.out.println("|  - Platea Baja  : $15.000                       |");
            System.out.println("|  - Platea Alta  : $18.000                       |");
            System.out.println("|  - Palcos       : $13.000                       |");
            System.out.println("|                                                 |");
            System.out.println("| Descuentos:                                     |");
            System.out.println("|  - Estudiantes (<25 anios) : 10%                |");
            System.out.println("|  - Tercera edad (>=60 anios): 15%               |");
            System.out.println("|=================================================|");

        // Ciclo principal del programa con menú principal
        for (;;) {
            System.out.println("\n|=================================================|");
            System.out.println("|                  MENU PRINCIPAL                 |");
            System.out.println("|=================================================|");
            System.out.println("| 1. Venta de entradas                            |");
            System.out.println("| 2. Visualizar resumen de ventas                 |");
            System.out.println("| 3. Generar boleta                               |");
            System.out.println("| 4. Calcular ingresos totales                    |");
            System.out.println("| 5. Salir                                        |");
            System.out.println("| 6. Menu Avanzado                                |");
            System.out.println("|=================================================|");
            opcion = pedirEnteroEnRango("Seleccione una opcion (1-6): ", 1, 6);

            switch (opcion) {
                case 1 -> comprarEntrada();
                case 2 -> mostrarResumenFinal();
                case 3 -> imprimirBoleta();
                case 4 -> mostrarTotalIngresos();
                case 5 -> {
                    System.out.println("Gracias por su compra. Disfrute la funcion.");
                    return;
                }
                case 6 -> mostrarMenuAvanzado(); 
            }
        }
    }


/**
 * Solicita al usuario que compre una o más entradas.
 * Muestra el plano, valida asientos, aplica descuentos y registra la compra.
 */
    static void comprarEntrada() {
        List<Map<String, Object>> entradasDeEstaCompra = new ArrayList<>();
        limpiarReservasExpiradas();
        mostrarPlano();

        int cantidad = pedirEnteroEnRango("Cuantas entradas desea comprar? (maximo 4): ", 1, 4); 

        for (int i = 0; i < cantidad; i++) {
            System.out.println("\nCompra #" + (i + 1));
            int numAsiento, fila, columna;

            while (true) {
                numAsiento = pedirEnteroEnRango("Ingrese numero de asiento (1-100): ", 1, 100);
                fila = (numAsiento - 1) / 10;
                columna = (numAsiento - 1) % 10;
                if (!asientoDisponible(fila, columna)) {
                    System.out.println("Asiento no disponible (ocupado o reservado).");
                } else {
                    break;
                }
            }

            int edad = pedirEnteroEnRango("Ingrese su edad: ", 10, 120);
            boolean estudiante = edad < 25;
            boolean terceraEdad = edad >= 60;

            String zona = obtenerTipoEntrada(fila);
            int precioBase = obtenerPrecioBase(zona);
            int descuento = estudiante ? (int)(precioBase * 0.10)
                             : terceraEdad ? (int)(precioBase * 0.15) : 0;
            int total = precioBase - descuento;

            asientos[fila][columna] = "[---]";

            Map<String, Object> entrada = crearEntrada(numAsiento, zona, total, estudiante, terceraEdad, null);
            resumenCompras.add(entrada);
            comprasEnSesion.add(entrada);
            entradasDeEstaCompra.add(entrada);

            // Actualizar arreglos requeridos por semana 8
            ventasAsientos = agregarInt(ventasAsientos, numAsiento);
            edadesClientes = agregarInt(edadesClientes, edad);
            String tipo = estudiante ? "Estudiante" : (terceraEdad ? "Tercera edad" : "General");
            tipoClientes = agregarString(tipoClientes, tipo);

            
            totalGeneral += total;
            entradasVendidas++;

            System.out.println("Compra realizada. Total pagado: $" + total);
            mostrarResumenCompra(zona, numAsiento, precioBase, descuento, total);
        }

        System.out.println("\nResumen de esta compra:");
        System.out.println("|===========================================================|");
        System.out.println("|              RESUMEN DE ENTRADAS COMPRADAS                |");
        System.out.println("|===========================================================|");
        System.out.printf("| %-22s | %-9s | %-20s |\n", "Tipo de entrada", "Asiento", "Total a pagar");
        System.out.println("|------------------------|-----------|----------------------|");

        int totalCompra = 0;
        for (Map<String, Object> e : entradasDeEstaCompra) {
            System.out.printf("| %-22s | %9d | $%-19d |\n",
                e.get("zona"),
                e.get("numero"),
                e.get("precioFinal")
            );
            totalCompra += (int) e.get("precioFinal");
        }

        int descuentoExtra = 0;
        if (entradasDeEstaCompra.size() >= 3) {
            descuentoExtra = (int)(totalCompra * 0.05);
            totalCompra -= descuentoExtra;
            System.out.println("|-----------------------------------------------------------|");
            System.out.printf("| %-34s : $%-19d |\n", "Descuento adicional (5%)", descuentoExtra);
        }

        System.out.println("|------------------------|-----------|----------------------|");
        System.out.printf("| %-34s : $%-19d |\n", "TOTAL DE ESTA COMPRA", totalCompra);
        System.out.println("|===========================================================|");
        
        mostrarVentasPorArreglos();

    }


/**
 * Muestra en pantalla las promociones vigentes,
 * incluyendo descuentos por edad y por cantidad de entradas.
 */
    static void verPromociones() {
        System.out.println("\n|=================================================|");
        System.out.println("|              PROMOCIONES DISPONIBLES            |");
        System.out.println("|=================================================|");
        for (Map<String, String> promo : promociones) {
            System.out.printf("| - %-40s : %-7s |\n",
                promo.get("descripcion"), promo.get("descuento"));
        }
        System.out.println("|=================================================|");
    }


/**
 * Permite al usuario buscar entradas compradas por número de asiento,
 * zona o tipo de cliente (estudiante/tercera edad).
 */
    static void buscarEntrada() {
        System.out.println("\nBuscar por:");
        System.out.println("1. Numero de asiento");
        System.out.println("2. Zona");
        System.out.println("3. Tipo (Estudiante/Tercera edad)");
        int tipo = pedirEnteroEnRango("Seleccione opcion: ",1,3);

        switch (tipo) {
            case 1 -> {
                int n = pedirEnteroEnRango("Numero de asiento: ",1,100);
                boolean encontrado = false;
                for (Map<String, Object> e : resumenCompras){
                    if ((int) e.get("numero") == n){
                        mostrarEntrada(e);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("\n No se encontro entrada para ese asiento.");
            }

            case 2 -> {
                while (true) {
                    System.out.println("\nZONAS DISPONIBLES:");
                    System.out.println("1. VIP");
                    System.out.println("2. PLATEA BAJA");
                    System.out.println("3. PLATEA ALTA");
                    System.out.println("4. PALCOS");
                    System.out.println("5. Volver al menu");

                    int opcionZona = pedirEnteroEnRango("Seleccione el numero de la zona: ", 1, 5);

                    if (opcionZona == 5) break;

                    String zonaBuscada = switch (opcionZona) {
                        case 1 -> "VIP";
                        case 2 -> "PLATEA BAJA";
                        case 3 -> "PLATEA ALTA";
                        case 4 -> "PALCOS";
                        default -> ""; // no debería pasar
                    };

                    boolean encontrado = false;
                    for (Map<String, Object> e : resumenCompras) {
                        if (((String) e.get("zona")).equalsIgnoreCase(zonaBuscada)) {
                            mostrarEntrada(e);
                            encontrado = true;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("No se encontraron entradas en esa zona.");
                    } else {
                        break; // salir si se encontró al menos una
                    }
                }
            }

            case 3 -> {
                while (true) {
                    System.out.println("\nTIPOS DE CLIENTE DISPONIBLES:");
                    System.out.println("1. Estudiante");
                    System.out.println("2. Tercera edad");
                    System.out.println("3. Volver al menu");

                    int opcionTipo = pedirEnteroEnRango("Seleccione el numero del tipo de cliente: ", 1, 3);

                    if (opcionTipo == 3) break;

                    boolean buscarEstudiante = opcionTipo == 1;
                    boolean buscarTercera = opcionTipo == 2;

                    boolean encontrado = false;

                   
                    for (Map<String, Object> e : resumenCompras) {
                        if ((buscarEstudiante && ((Boolean) e.get("estudiante"))) || (buscarTercera && ((Boolean) e.get("terceraEdad")))) {
                            mostrarEntrada(e);
                            encontrado = true;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("No se encontraron entradas de ese tipo.");
                    } else {
                        break; // salir si se encontró al menos una
                    }
                }
            }
        }
    }

/**
 * Permite eliminar una entrada previamente comprada, liberando el asiento
 * y actualizando el resumen de compras. Se puede eliminar más de una.
 */
static void eliminarEntrada() {
    mostrarPlano();

    while (true) {
        System.out.println("\n|------------------------------------------------|");
        System.out.println("|          ELIMINAR ENTRADA COMPRADA             |");
        System.out.println("|------------------------------------------------|");
        int num = pedirEnteroEnRango("Numero de asiento (1-100 o 0 para salir): ", 0, 100);

        if (num == 0) {
            System.out.println("Operacion cancelada. Volviendo al menu principal.");
            break;
        }

        Map<String, Object> encontrada = null;

        for (Map<String, Object> e : resumenCompras) {
            if ((int) e.get("numero") == num) {
                encontrada = e;
                break;
            }
        }

        if (encontrada != null) {
            int fila = (num - 1) / 10;
            int col = (num - 1) % 10;
            asientos[fila][col] = String.format("[%03d]", num);
            resumenCompras.remove(encontrada);
            totalGeneral -= (int) encontrada.get("precioFinal");
            entradasVendidas--;
            System.out.println("\n Entrada eliminada con exito.");

            scanner.nextLine(); // limpiar buffer
            while (true) {
                System.out.print("Desea eliminar otra entrada? (s/n): ");
                String opcion = scanner.nextLine().trim().toLowerCase();
                if (opcion.equals("s")) break;
                if (opcion.equals("n")) return;
                System.out.println("Opcion no valida. Ingrese 's' o 'n'.");
            }

        } else {
            System.out.println("\n Entrada no encontrada. Intente con otro numero.");
        }
    }
}


/**
 * Muestra el resumen completo de todas las entradas compradas durante la sesión.
 */    
    static void mostrarResumenFinal() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        System.out.println();
        System.out.println("|===========================================================|");
        System.out.println("|                 RESUMEN TOTAL DE COMPRAS                  |");
        System.out.println("|===========================================================|");
        System.out.printf("| %-22s | %-9s | %-20s |\n", "Tipo de entrada", "Asiento", "Total a pagar");
        System.out.println("|------------------------|-----------|----------------------|");

        for (Map<String, Object> compra : resumenCompras) {
            System.out.printf("| %-22s | %9d | $%-19d |\n",
                compra.get("zona"),
                compra.get("numero"),
                compra.get("precioFinal")
            );
        }

        System.out.println("|------------------------|-----------|----------------------|");
        System.out.printf("| %-34s : $%-19d |\n", "TOTAL GENERAL", totalGeneral);
        System.out.printf("| %-34s : %-20s |\n", "Fecha y hora", ahora.format(formato));
        System.out.printf("| %-57s |\n", "Gracias por su compra. Disfrute la funcion.");
        System.out.println("|===========================================================|");
    }


/**
 * Muestra en pantalla la información completa de una entrada específica,
 * incluyendo número de asiento, zona, total pagado y tipo de cliente.
 */
    static void mostrarEntrada(Map<String, Object> e) {
        System.out.println("\n|-----------------------------------------------|");
        System.out.println("|-----------   INFORMACION DE ENTRADA   --------|");
        System.out.println("|-----------------------------------------------|");
        System.out.printf("| %-20s: %-23s |\n", "Zona", e.get("zona"));
        System.out.printf("| %-20s: %-23d |\n", "No de asiento", (int) e.get("numero"));
        System.out.printf("| %-20s: $%-22d |\n", "Total pagado", (int) e.get("precioFinal"));

        String tipo = (Boolean) e.get("estudiante") ? "Estudiante" :
                      (Boolean) e.get("terceraEdad") ? "Tercera edad" : "General";
        System.out.printf("| %-20s: %-23s |\n", "Tipo de cliente", tipo);
        System.out.println("|-----------------------------------------------|");
    }


/**
 * Inicializa el plano del teatro y precarga algunos asientos como ocupados.
 */
    static void inicializarPlano(String[][] asientos) {
    for (int i = 0, num = 1; i < 10; i++) {
        for (int j = 0; j < 10; j++, num++) {
            asientos[i][j] = String.format("[%03d]", num);
        }
    }

    // Asientos ocupados al iniciar (pre-cargados con información)
    ocuparAsiento(0, 0, 22);  // estudiante
    ocuparAsiento(0, 1, 65);  // tercera edad
    ocuparAsiento(3, 0, 30);  // general
    ocuparAsiento(5, 0, 24);  // estudiante
    ocuparAsiento(7, 0, 68);  // tercera edad
    reservarAsiento(1, 1);    // asiento 12
    reservarAsiento(2, 2);    // asiento 23
}

/**
 * Marca un asiento como ocupado y lo registra en el resumen de compras.
 * Este método se usa para precargar asientos como ya vendidos.
 */
    static void ocuparAsiento(int fila, int columna, int edad) {
        int numero = fila * 10 + columna + 1;
        String zona = obtenerTipoEntrada(fila);
        int total = calcularPrecioFinal(zona, edad);
        boolean estudiante = edad < 25;
        boolean terceraEdad = edad >= 60;

        // Marcar como ocupado y registrar
        asientos[fila][columna] = "[---]";
        Map<String, Object> entrada = crearEntrada(numero, zona, total, estudiante, terceraEdad, null);
        resumenCompras.add(entrada);        
        totalGeneral += total;
        entradasVendidas++;
    }

/**
 * Muestra en consola el plano actual del teatro con la zona y estado de cada asiento.
 * Los asientos ocupados se marcan como [---].
 */    
    static void mostrarPlano() {
        System.out.println("\n>>> PLANO DEL TEATRO <<<");
        int num = 1;
        for (int i = 0; i < 10; i++) {
            System.out.printf("%-13s ", obtenerTipoEntrada(i));
            for (int j = 0; j < 10; j++) {
                System.out.print(asientos[i][j] + " ");
                num++;
            }
            System.out.println();
        }
        System.out.println("[---] Ocupado");
        System.out.println("[RES] Reservado");

    }

/**
 * Determina la zona según fila
 * ESTRUCTURA DE CONTROL
 */
    static String obtenerTipoEntrada(int fila) {
        return switch (fila) {
            case 0, 1, 2 -> "VIP";
            case 3, 4 -> "PLATEA BAJA";
            case 5, 6 -> "PLATEA ALTA";
            default -> "PALCOS";
        };
    }
    
/**
 * Muestra el detalle de una entrada comprada, incluyendo precio base,
 * descuento aplicado, total pagado y fecha/hora de la transacción.
 * ESTRUCTURA DE CONTROL
 */
    static void mostrarResumenCompra(String tipo, int asiento, int base, int descuento, int total) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println("[DEBUG] Resumen de compra - tipo: " + tipo + ", asiento: " + asiento + ", base: $" + base + ", descuento: $" + descuento + ", total: $" + total);

        System.out.println("\n|-----------------------------------------------|");
        System.out.println("|-------------   RESUMEN DE COMPRA   -----------|");
        System.out.println("|-----------------------------------------------|");
        System.out.printf("| %-20s: %-23s |\n", "Tipo de entrada", tipo);
        System.out.printf("| %-20s: %-23d |\n", "Nro de asiento", asiento);
        System.out.printf("| %-20s: $%-22d |\n", "Precio base", base);
        System.out.printf("| %-20s: $%-22d |\n", "Descuento", descuento);
        System.out.printf("| %-20s: $%-22d |\n", "Total a pagar", total);
        System.out.printf("| %-20s: %-23s |\n", "Fecha y hora", ahora.format(formato));
        System.out.println("| Gracias por su compra, disfrute la funcion.   |");
        System.out.println("|-----------------------------------------------|");
    }

/**
 * Solicita al usuario un número entero dentro de un rango específico,
 * validando que la entrada sea numérica y esté dentro del límite.
 */
    static int pedirEnteroEnRango(String mensaje, int min, int max) {
        int valor;
        while (true) {
            try {
                System.out.print(mensaje);
                valor = Integer.parseInt(scanner.next());
                if (valor >= min && valor <= max) break;
                else System.out.println("Debe ingresar un numero entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada no valida. Intente nuevamente.");
            }
        }
        return valor;
    }
    
/**
 * Calcula el precio final de una entrada aplicando descuentos según la edad.
 * ESTRUCTURA DE CONTROL
 */    
    static int calcularPrecioFinal(String zona, int edad) {
        int precioBase = obtenerPrecioBase(zona);
        int descuento = (edad < 25) ? (int)(precioBase * 0.10)
                       : (edad >= 60) ? (int)(precioBase * 0.15)
                       : 0;
        return precioBase - descuento;
    }

/**
 * Marca un asiento como reservado en el plano. No lo registra como venta.
 */
    static void reservarAsiento(int fila, int columna) {
       int numero = fila * 10 + columna + 1;
       asientos[fila][columna] = "[RES]";
       System.out.println("[DEBUG] Asiento " + numero + " reservado al iniciar.");
   }

/**
 * Permite generar la reserva.
 */    
 static void reservarEntrada() {
    limpiarReservasExpiradas();
    mostrarPlano();

    int asiento = pedirEnteroEnRango("Ingrese numero de asiento a reservar (1-100): ", 1, 100);
    int fila = (asiento - 1) / 10;
    int col = (asiento - 1) % 10;

    if (!asientoDisponible(fila, col)) {
        System.out.println("Ese asiento no esta disponible.");
        return;
    }

    asientos[fila][col] = "[RES]";
    String zona = obtenerTipoEntrada(fila);
    LocalDateTime hora = LocalDateTime.now();

    Map<String, Object> reserva = crearEntrada(asiento, zona, 0, false, false, hora);
    reservas.add(reserva);
    System.out.println("Asiento reservado correctamente.");
}

    
/**
 * Permite modificar una entrada, la edad mas no el número.
 */
static void modificarVenta() {
    int asiento = pedirEnteroEnRango("Ingrese numero de asiento que desea modificar: ", 1, 100);
    Map<String, Object> encontrada = null;

    for (Map<String, Object> e : resumenCompras) {
        if ((int) e.get("numero") == asiento) {
            encontrada = e;
            break;
        }
    }

    if (encontrada == null) {
        System.out.println("No se encontro la entrada.");
        return;
    }

    System.out.println("Entrada actual:");
    mostrarEntrada(encontrada);

    int nuevaEdad = pedirEnteroEnRango("Ingrese nueva edad para recalcular descuento: ", 10, 120);
    String zona = (String) encontrada.get("zona");

    int nuevoPrecioBase = obtenerPrecioBase(zona);
    int nuevoDescuento = nuevaEdad < 25 ? (int)(nuevoPrecioBase * 0.10)
                          : nuevaEdad >= 60 ? (int)(nuevoPrecioBase * 0.15) : 0;
    int nuevoTotal = nuevoPrecioBase - nuevoDescuento;

    totalGeneral -= (int) encontrada.get("precioFinal"); // quitar el valor anterior
    encontrada.put("precioFinal", nuevoTotal);
    encontrada.put("estudiante", nuevaEdad < 25);
    encontrada.put("terceraEdad", nuevaEdad >= 60);
    totalGeneral += nuevoTotal;

    System.out.println("Entrada modificada exitosamente.");
}


/**
 * Permite imprimir la boleta.
 */
    static void imprimirBoleta() {
    if (comprasEnSesion.isEmpty()) {
        System.out.println("No hay entradas compradas en esta sesion.");
        return;
    }

    System.out.println("\n|====================== BOLETA COMPLETA ======================|");
    System.out.println("[DEBUG] Entradas en la sesion actual: " + comprasEnSesion.size());

    for (Map<String, Object> e : comprasEnSesion){
        System.out.println(generarBoleta(e));
    }

    System.out.printf("| %-34s : $%-19d |\n", "TOTAL PAGADO", calcularTotal(comprasEnSesion));
    System.out.println("|=============================================================|");
}

/**
 * Permite hacer la boleta.
 */
static String generarBoleta(Map<String, Object> e){
    LocalDateTime ahora = LocalDateTime.now();
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    String zona = (String) e.get("zona");
    int numero = (int) e.get("numero");
    int precioFinal = (int) e.get("precioFinal");
    String tipoCliente = (Boolean) e.get("estudiante") ? "Estudiante" :
                         (Boolean) e.get("terceraEdad") ? "Tercera edad" : "General";

    return "\n|-----------------------------------------------|\n" +
           "|-------------   BOLETA DE ENTRADA   -----------|\n" +
           "|-----------------------------------------------|\n" +
           String.format("| %-20s: %-23s |\n", "Zona", zona) +
           String.format("| %-20s: %-23d |\n", "No de asiento", numero) +
           String.format("| %-20s: $%-22d |\n", "Total pagado", precioFinal) +
           String.format("| %-20s: %-23s |\n", "Tipo de cliente", tipoCliente) +
           String.format("| %-20s: %-23s |\n", "Fecha y hora", ahora.format(formato)) +
           "|-----------------------------------------------|\n";
}


/**
 * Permite convertir una reserva existente en una compra confirmada.
 */
static void confirmarReserva() {
    limpiarReservasExpiradas();
    if (reservas.isEmpty()) {
        System.out.println("No hay reservas registradas.");
        return;
    }

    mostrarPlano();
    int num = pedirEnteroEnRango("Ingrese numero de asiento reservado que desea confirmar: ", 1, 100);
    int fila = (num - 1) / 10;
    int col = (num - 1) % 10;

    if (!asientos[fila][col].equals("[RES]")) {
        System.out.println("Ese asiento no esta reservado.");
        return;
    }

    Map<String, Object> reserva = null;
    for (Map<String, Object> r : reservas) {
        if ((int) r.get("numero") == num) {
            reserva = r;
            break;
        }
    }

    if (reserva == null) {
        System.out.println("Reserva no encontrada.");
        return;
    }

    int edad = pedirEnteroEnRango("Ingrese su edad para confirmar la compra: ", 10, 120);
    boolean estudiante = edad < 25;
    boolean terceraEdad = edad >= 60;

    String zona = (String) reserva.get("zona");
    int precioBase = obtenerPrecioBase(zona);
    int descuento = estudiante ? (int)(precioBase * 0.10)
                     : terceraEdad ? (int)(precioBase * 0.15) : 0;
    int total = precioBase - descuento;

    asientos[fila][col] = "[---]";
    reservas.remove(reserva);

    Map<String, Object> entradaFinal = crearEntrada(num, zona, total, estudiante, terceraEdad, null);
    resumenCompras.add(entradaFinal);
    comprasEnSesion.add(entradaFinal);
    totalGeneral += total;
    entradasVendidas++;

    mostrarResumenCompra(zona, num, precioBase, descuento, total);
    System.out.println("Reserva confirmada exitosamente.");
}


/**
 * Limpia las reservas cuando cmplen el limite tiempo
 * ESTRUCTURA DE CONTROL
 */
    static void limpiarReservasExpiradas() {
        Iterator<Map<String, Object>> it = reservas.iterator();

        while (it.hasNext()) {
            Map<String, Object> r = it.next();
            LocalDateTime hora = (LocalDateTime) r.get("horaReserva");
            if (hora != null && hora.plusMinutes(1).isBefore(LocalDateTime.now())) {
                int num = (int) r.get("numero");
                int fila = (num - 1) / 10;
                int col = (num - 1) % 10;
                asientos[fila][col] = String.format("[%03d]", num);
                it.remove();
                System.out.println("[INFO] Reserva expirada liberada: asiento " + num);
            }
        }
    }

/**
 * Calcula el total
 */   
static int calcularTotal(List<Map<String, Object>> lista) {
    int total = 0;
    for (Map<String, Object> e : lista) {
        total += (int) e.get("precioFinal");
    }
    return total;
}
 /**
 * Revisa si un asiento está libre (ni comprado ni reservado).
 */
    static boolean asientoDisponible(int fila, int col) {
        return !(asientos[fila][col].equals("[---]") || asientos[fila][col].equals("[RES]"));
    }

/**
 * Devuelve el precio base de una entrada según la zona.
 */
    static int obtenerPrecioBase(String zona) {
        return switch (zona) {
            case "VIP" -> 30000;
            case "PLATEA BAJA" -> 15000;
            case "PLATEA ALTA" -> 18000;
            default -> 13000; // PALCOS u otra
        };
    }
   
 static void mostrarMenuAvanzado() {
    int opcionAvanzado;

    do {
        System.out.println("\n|=================================================|");
        System.out.println("|                 MENU AVANZADO                   |");
        System.out.println("|=================================================|");
        System.out.println("| 1. Reservar asiento                             |");
        System.out.println("| 2. Confirmar reserva                            |");
        System.out.println("| 3. Buscar entrada                               |");
        System.out.println("| 4. Modificar venta                              |");
        System.out.println("| 5. Eliminar entrada                             |");
        System.out.println("| 6. Ver promociones                              |");
        System.out.println("| 7. Mostrar resumen por arreglos                 |");
        System.out.println("| 8. Volver al menu principal                     |");  
        System.out.println("|=================================================|");
        opcionAvanzado = pedirEnteroEnRango("Seleccione una opcion (1-8): ", 1, 8);

        switch (opcionAvanzado) {
            case 1 -> reservarEntrada();
            case 2 -> confirmarReserva();
            case 3 -> buscarEntrada();
            case 4 -> modificarVenta();
            case 5 -> eliminarEntrada();
            case 6 -> verPromociones();
            case 7 -> mostrarVentasPorArreglos();
            case 8 -> System.out.println("Volviendo al menu principal...");
        }
    } while (opcionAvanzado != 7);
}

    
    static void mostrarTotalIngresos() {
        System.out.println("\n|=================================================|");
        System.out.printf("| %-34s : $%-19d |\n", "INGRESOS TOTALES", totalGeneral);
        System.out.println("|=================================================|");
    
   }
    
    static Map<String, Object> crearEntrada(int numero, String zona, int precioFinal, boolean estudiante, boolean terceraEdad, LocalDateTime horaReserva) {
        Map<String, Object> entrada = new HashMap<>();
        entrada.put("numero", numero);
        entrada.put("zona", zona);
        entrada.put("precioFinal", precioFinal);
        entrada.put("estudiante", estudiante);
        entrada.put("terceraEdad", terceraEdad);
        entrada.put("horaReserva", horaReserva); // puede ser null
        return entrada;
    }

    
    static int[] agregarInt(int[] original, int valor) {
        int[] nuevo = new int[original.length + 1];
        for (int i = 0; i < original.length; i++) nuevo[i] = original[i];
        nuevo[original.length] = valor;
        return nuevo;
    }

    static String[] agregarString(String[] original, String valor) {
        String[] nuevo = new String[original.length + 1];
        for (int i = 0; i < original.length; i++) nuevo[i] = original[i];
        nuevo[original.length] = valor;
        return nuevo;
    }

    static void mostrarVentasPorArreglos() {
        System.out.println("\n>>> RESUMEN CON ARREGLOS <<<");
        for (int i = 0; i < ventasAsientos.length; i++) {
            System.out.printf("Asiento #%03d | Edad: %d | Tipo: %s\n",
                ventasAsientos[i], edadesClientes[i], tipoClientes[i]);
        }
    }
    
    static void inicializarPromociones() {
        promociones.add(crearPromocion("Estudiantes < 25 años", "10%"));
        promociones.add(crearPromocion("Tercera edad >= 60 años", "15%"));
        promociones.add(crearPromocion("Compra de 3 o mas entradas", "5% adicional"));
    }

    static Map<String, String> crearPromocion(String descripcion, String descuento) {
        Map<String, String> promo = new HashMap<>();
        promo.put("descripcion", descripcion);
        promo.put("descuento", descuento);
        return promo;
    }

    
}

package teatromoro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * ===============================================================
 * SISTEMA DE VENTA DE ENTRADAS - TEATRO MORO
 * ===============================================================
 * Alumna       : Militza Fernández
 * Fecha        : 05 mayo 2025
 * Proyecto     : Optimizando la Gestión de datos en Java - Semana 8
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



 *Profe, en este proyecto traté de organizar el código de forma clara y funcional, 
 *respetando el requisito de no usar POO. Utilicé arreglos (String[][]) para el 
 *plano de asientos porque es una estructura fija y conocida, lo que lo hace 
 *eficiente para ese caso. Para manejar datos más dinámicos, como las reservas, 
 *opté por usar un ArrayList<String[]> ya que permite mayor flexibilidad y evita 
 *tener que controlar manualmente el tamaño o los índices. En el caso de las ventas, 
 *se usó un arreglo también, pero podría haberse implementado con ArrayList para 
 *lograr un código más limpio y fácil de mantener. La idea fue mantener un buen 
 *equilibrio entre eficiencia y claridad, sin salirse del enfoque estructurado.
 * 
 * ===============================================================
 */


public class TeatroMoro {
// Scanner global para entrada de datos desde consola
    static Scanner scanner = new Scanner(System.in);
// Matriz de asientos (10 filas x 10 columnas) 100 asientos
    static String[][] asientos = new String[10][10];
// Lista que almacena todas las entradas compradas durante la sesión


    static int idVentaActual = 1000;
    static int idClienteActual = 5000;
    static final int MAX_ENTRADAS = 100;
    static String[][] ventas = new String[MAX_ENTRADAS][6];
    static int indiceVenta = 0;

    static ArrayList<String[]> reservas = new ArrayList<>();
    static final String[] promociones = {
        "Estudiantes < 25 años: 10%",
        "Tercera edad >= 60 años: 15%",
        "Compra de 3 o más entradas: 5% adicional"
    };

    // Variables estáticas para estadísticas globales
    static int totalGeneral = 0;
    static int entradasVendidas = 0;
    static String nombreTeatro = "TEATRO MORO";

    public static void main(String[] args) {
        inicializarPlano(asientos); // Llenar el plano con zonas
        
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
                case 1 -> medirTiempo("Compra de entrada", () -> comprarEntrada());
                case 2 -> medirTiempo("Mostrar resumen", () -> mostrarResumenFinal());
                case 3 -> medirTiempo("Imprimir boleta", () -> imprimirBoleta());
                case 4 -> medirTiempo("Total de ingresos", () -> mostrarTotalIngresos());
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



            // Determinar tipo de cliente
            String tipo = estudiante ? "Estudiante" : (terceraEdad ? "Tercera edad" : "General");

            ventas[indiceVenta][0] = String.valueOf(idVentaActual++);
            ventas[indiceVenta][1] = String.valueOf(idClienteActual++);
            ventas[indiceVenta][2] = String.valueOf(numAsiento);
            ventas[indiceVenta][3] = String.valueOf(edad);
            ventas[indiceVenta][4] = tipo;
            ventas[indiceVenta][5] = zona;
            indiceVenta++;

            totalGeneral += total;
            entradasVendidas++;
            System.out.println("Compra realizada. Total pagado: $" + total);
            mostrarResumenCompra(zona, numAsiento, precioBase, descuento, total);
        }

        System.out.println("\n>>> Compra registrada exitosamente <<<");
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
        for (String promo : promociones) {
            System.out.printf("| - %-46s |\n", promo);
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
        int opcion = pedirEnteroEnRango("Seleccione opcion: ", 1, 3);

        switch (opcion) {
            case 1 -> {
                int n = pedirEnteroEnRango("Numero de asiento: ", 1, 100);
                boolean encontrado = false;
                for (int i = 0; i < indiceVenta; i++) {
                    if (Integer.parseInt(ventas[i][2]) == n) {
                        mostrarEntradaDesdeVentas(i);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("\nNo se encontró entrada para ese asiento.");
            }

            case 2 -> {
                System.out.println("\nZONAS DISPONIBLES:");
                System.out.println("1. VIP");
                System.out.println("2. PLATEA BAJA");
                System.out.println("3. PLATEA ALTA");
                System.out.println("4. PALCOS");
                int zonaOpcion = pedirEnteroEnRango("Seleccione una zona: ", 1, 4);
                String zonaBuscada = switch (zonaOpcion) {
                    case 1 -> "VIP";
                    case 2 -> "PLATEA BAJA";
                    case 3 -> "PLATEA ALTA";
                    case 4 -> "PALCOS";
                    default -> "";
                };

                boolean encontrado = false;
                for (int i = 0; i < indiceVenta; i++) {
                    if (ventas[i][5].equalsIgnoreCase(zonaBuscada)) {
                        mostrarEntradaDesdeVentas(i);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("No se encontraron entradas en esa zona.");
            }

            case 3 -> {
                System.out.println("\nTIPOS DE CLIENTE DISPONIBLES:");
                System.out.println("1. Estudiante");
                System.out.println("2. Tercera edad");
                int tipoOpcion = pedirEnteroEnRango("Seleccione un tipo: ", 1, 2);
                String tipoBuscado = tipoOpcion == 1 ? "Estudiante" : "Tercera edad";

                boolean encontrado = false;
                for (int i = 0; i < indiceVenta; i++) {
                    if (ventas[i][4].equalsIgnoreCase(tipoBuscado)) {
                        mostrarEntradaDesdeVentas(i);
                        encontrado = true;
                    }
                }
                if (!encontrado) System.out.println("No se encontraron entradas para ese tipo.");
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

            int indice = -1;
            for (int i = 0; i < indiceVenta; i++) {
                if (Integer.parseInt(ventas[i][2]) == num) {
                    indice = i;
                    break;
                }
            }

            if (indice != -1) {
                int fila = (num - 1) / 10;
                int col = (num - 1) % 10;
                asientos[fila][col] = String.format("[%03d]", num);

                int edad = Integer.parseInt(ventas[indice][3]);
                String zona = ventas[indice][5];
                totalGeneral -= calcularPrecioFinal(zona, edad);

                // Shift para eliminar
                for (int j = indice; j < indiceVenta - 1; j++) {
                    for (int k = 0; k < 6; k++) {
                        ventas[j][k] = ventas[j + 1][k];
                    }
                }

                indiceVenta--;

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

        for (int i = 0; i < indiceVenta; i++) {
            String zona = ventas[i][5];
            int asiento = Integer.parseInt(ventas[i][2]);
            int edad = Integer.parseInt(ventas[i][3]);

            int precioBase = obtenerPrecioBase(zona);
            int descuento = edad < 25 ? (int)(precioBase * 0.10)
                            : edad >= 60 ? (int)(precioBase * 0.15) : 0;
            int total = precioBase - descuento;

            System.out.printf("| %-22s | %9d | $%-19d |\n", zona, asiento, total);
        }

        System.out.println("|------------------------|-----------|----------------------|");
        System.out.printf("| %-34s : $%-19d |\n", "TOTAL GENERAL", totalGeneral);
        System.out.printf("| %-34s : %-20s |\n", "Fecha y hora", ahora.format(formato));
        System.out.printf("| %-57s |\n", "Gracias por su compra. Disfrute la funcion.");
        System.out.println("|===========================================================|");
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

        String tipo = estudiante ? "Estudiante" : (terceraEdad ? "Tercera edad" : "General");

        asientos[fila][columna] = "[---]";

        ventas[indiceVenta][0] = String.valueOf(idVentaActual++);
        ventas[indiceVenta][1] = String.valueOf(idClienteActual++);
        ventas[indiceVenta][2] = String.valueOf(numero);
        ventas[indiceVenta][3] = String.valueOf(edad);
        ventas[indiceVenta][4] = tipo;
        ventas[indiceVenta][5] = zona;
        indiceVenta++;

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

        int asiento = pedirEnteroEnRango("Ingrese número de asiento a reservar (1-100): ", 1, 100);
        int fila = (asiento - 1) / 10;
        int col = (asiento - 1) % 10;

        if (!asientoDisponible(fila, col)) {
            System.out.println("Ese asiento no está disponible.");
            return;
        }

        asientos[fila][col] = "[RES]";
        String zona = obtenerTipoEntrada(fila);
        String fechaHora = LocalDateTime.now().toString();

        reservas.add(new String[]{String.valueOf(asiento), zona, fechaHora});
        System.out.println("Asiento reservado correctamente.");
    }

    
/**
 * Permite modificar una entrada, la edad mas no el número.
 */
    static void modificarVenta() {
        int asiento = pedirEnteroEnRango("Ingrese numero de asiento que desea modificar: ", 1, 100);

        int indice = -1;
        for (int i = 0; i < indiceVenta; i++) {
            if (Integer.parseInt(ventas[i][2]) == asiento) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            System.out.println("No se encontró la entrada.");
            return;
        }

        System.out.println("Entrada actual:");
        mostrarEntradaDesdeVentas(indice);

        int nuevaEdad = pedirEnteroEnRango("Ingrese nueva edad para recalcular descuento: ", 10, 120);
        String zona = ventas[indice][5];

        int precioAnterior = calcularPrecioFinal(zona, Integer.parseInt(ventas[indice][3]));
        int precioNuevo = calcularPrecioFinal(zona, nuevaEdad);

        totalGeneral -= precioAnterior;
        totalGeneral += precioNuevo;

        ventas[indice][3] = String.valueOf(nuevaEdad);
        ventas[indice][4] = nuevaEdad < 25 ? "Estudiante" :
                            nuevaEdad >= 60 ? "Tercera edad" : "General";

        System.out.println("Entrada modificada exitosamente.");
    }
    
/**
 * Muestra en consola los detalles de una entrada específica desde el arreglo de ventas.
 * @param i Índice de la venta dentro del arreglo.
 */
    static void mostrarEntradaDesdeVentas(int i) {
        System.out.println("\n|-----------------------------------------------|");
        System.out.println("|-----------   INFORMACIÓN DE ENTRADA   --------|");
        System.out.println("|-----------------------------------------------|");

        int precio = calcularPrecioFinal(ventas[i][5], Integer.parseInt(ventas[i][3]));

        System.out.printf("| %-20s: %-23s |\n", "Zona", ventas[i][5]);
        System.out.printf("| %-20s: %-23s |\n", "No de asiento", ventas[i][2]);
        System.out.printf("| %-20s: $%-22d |\n", "Total pagado", precio);
        System.out.printf("| %-20s: %-23s |\n", "Tipo de cliente", ventas[i][4]);
        System.out.println("|-----------------------------------------------|");
    }

/**
 * Permite imprimir la boleta.
 */
    static void imprimirBoleta() {
        if (indiceVenta == 0) {
            System.out.println("No hay entradas registradas.");
            return;
        }

        System.out.println("\n|====================== BOLETA COMPLETA ======================|");

        int total = 0;
        for (int i = 0; i < indiceVenta; i++) {
            System.out.println(generarBoletaDesdeVentas(i));
            total += calcularPrecioFinal(ventas[i][5], Integer.parseInt(ventas[i][3]));
        }

        System.out.printf("| %-34s : $%-19d |\n", "TOTAL PAGADO", total);
        System.out.println("|=============================================================|");
    }
    
    
/**
 * Permite hacer la boleta.
 */
static String generarBoletaDesdeVentas(int i) {
    LocalDateTime ahora = LocalDateTime.now();
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    int precioFinal = calcularPrecioFinal(ventas[i][5], Integer.parseInt(ventas[i][3]));

    return """
           
           |-----------------------------------------------|
           |-------------   BOLETA DE ENTRADA   -----------|
           |-----------------------------------------------|
           """ +
           String.format("| %-20s: %-23s |\n", "Zona", ventas[i][5]) +
           String.format("| %-20s: %-23s |\n", "No de asiento", ventas[i][2]) +
           String.format("| %-20s: $%-22d |\n", "Total pagado", precioFinal) +
           String.format("| %-20s: %-23s |\n", "Tipo de cliente", ventas[i][4]) +
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
        int num = pedirEnteroEnRango("Ingrese número de asiento reservado que desea confirmar: ", 1, 100);
        int fila = (num - 1) / 10;
        int col = (num - 1) % 10;

        if (!asientos[fila][col].equals("[RES]")) {
            System.out.println("Ese asiento no está reservado.");
            return;
        }

        int indiceReserva = -1;
        for (int i = 0; i < reservas.size(); i++) {
            if (Integer.parseInt(reservas.get(i)[0]) == num) {
                indiceReserva = i;
                break;
            }
        }

        if (indiceReserva == -1) {
            System.out.println("Reserva no encontrada.");
            return;
        }

        int edad = pedirEnteroEnRango("Ingrese su edad para confirmar la compra: ", 10, 120);
        boolean estudiante = edad < 25;
        boolean terceraEdad = edad >= 60;

        String zona = reservas.get(indiceReserva)[1];
        int precioBase = obtenerPrecioBase(zona);
        int descuento = estudiante ? (int)(precioBase * 0.10)
                         : terceraEdad ? (int)(precioBase * 0.15) : 0;
        int total = precioBase - descuento;
        String tipo = estudiante ? "Estudiante" : terceraEdad ? "Tercera edad" : "General";

        ventas[indiceVenta][0] = String.valueOf(idVentaActual++);
        ventas[indiceVenta][1] = String.valueOf(idClienteActual++);
        ventas[indiceVenta][2] = String.valueOf(num);
        ventas[indiceVenta][3] = String.valueOf(edad);
        ventas[indiceVenta][4] = tipo;
        ventas[indiceVenta][5] = zona;
        indiceVenta++;

        asientos[fila][col] = "[---]";
        reservas.remove(indiceReserva);

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
        LocalDateTime ahora = LocalDateTime.now();
        reservas.removeIf(reserva -> {
            int num = Integer.parseInt(reserva[0]);
            LocalDateTime hora = LocalDateTime.parse(reserva[2]);
            if (hora.plusMinutes(1).isBefore(ahora)) {
                int fila = (num - 1) / 10;
                int col = (num - 1) % 10;
                asientos[fila][col] = String.format("[%03d]", num);
                System.out.println("[INFO] Reserva expirada liberada: asiento " + num);
                return true;
            }
            return false;
        });
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
    
 /**
 * Muestra un menú avanzado con funciones adicionales como modificar ventas,
 * confirmar reservas, eliminar entradas y ver promociones.
 */
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
        System.out.println("| 9. Verificar integridad del sistema             |");  
        System.out.println("|=================================================|");
        opcionAvanzado = pedirEnteroEnRango("Seleccione una opcion (1-9): ", 1, 9);

        switch (opcionAvanzado) {
            case 1 -> reservarEntrada();
            case 2 -> confirmarReserva();
            case 3 -> buscarEntrada();
            case 4 -> modificarVenta();
            case 5 -> eliminarEntrada();
            case 6 -> verPromociones();
            case 7 -> mostrarVentasPorArreglos();
            case 8 -> System.out.println("Volviendo al menu principal...");
            case 9 -> validarIntegridadSistema();
        }
    } while (opcionAvanzado != 8);
}

 /**
 * Imprime en consola el total de ingresos acumulados por todas las ventas registradas.
 */   
    static void mostrarTotalIngresos() {
        System.out.println("\n|=================================================|");
        System.out.printf("| %-34s : $%-19d |\n", "INGRESOS TOTALES", totalGeneral);
        System.out.println("|=================================================|");
    
   }
    
 /**
 * Muestra el resumen completo de todas las ventas almacenadas en el arreglo `ventas`
 * e informa si la cantidad total está dentro del límite permitido.
 */
    static void mostrarVentasPorArreglos() {
        System.out.println("\n>>> RESUMEN CON MATRIZ DE VENTAS <<<");
        for (int i = 0; i < indiceVenta; i++) {
            System.out.printf("Venta ID: %s | Cliente ID: %s | Asiento #%03d | Edad: %s | Tipo: %s | Zona: %s\n",
                ventas[i][0],
                ventas[i][1],
                Integer.parseInt(ventas[i][2]),
                ventas[i][3],
                ventas[i][4],
                ventas[i][5]
            );
        }

        System.out.println("\nIntegridad de datos: " + (verificarIntegridadDatos() ? "OK" : "ERROR"));
    }
    
/**
 * Verifica que la cantidad de ventas registradas no exceda el límite máximo permitido (MAX_ENTRADAS).
 * Retorna true si está dentro del límite; false si se ha superado.
 */
    static boolean verificarIntegridadDatos() {
        return indiceVenta <= MAX_ENTRADAS;
    }
 
 /**
 * Verifica que cada asiento registrado como vendido en la matriz de ventas
 * esté correctamente marcado como ocupado ("[---]") en el plano de asientos.
 * Retorna true si todo está consistente; false si hay inconsistencias.
 */
    static boolean validarVentasVsAsientos() {
        for (int i = 0; i < indiceVenta; i++) {
            int numAsiento = Integer.parseInt(ventas[i][2]);
            int fila = (numAsiento - 1) / 10;
            int col = (numAsiento - 1) % 10;
            if (!asientos[fila][col].equals("[---]")) {
                System.out.println("[ERROR] Venta registrada en asiento " + numAsiento + " pero no está marcado como ocupado.");
                return false;
            }
        }
        return true;
    }

/**
 * Revisa que cada reserva registrada en la lista de reservas
 * esté efectivamente marcada como "[RES]" en el plano de asientos.
 * Retorna true si las reservas están bien reflejadas; false si no.
 */
    static boolean validarReservasVsAsientos() {
        for (String[] reserva : reservas) {
            int numAsiento = Integer.parseInt(reserva[0]);
            int fila = (numAsiento - 1) / 10;
            int col = (numAsiento - 1) % 10;

            if (!asientos[fila][col].equals("[RES]")) {
                System.out.println("[ERROR] Reserva registrada en asiento " + numAsiento + " pero no está marcado como reservado.");
                return false;
            }
        }
        return true;
    }
 
/**
 * Calcula el total de ingresos sumando todas las ventas registradas
 * y lo compara con el valor acumulado en totalGeneral.
 * Informa si hay inconsistencias y retorna true o false según corresponda.
 */
    static boolean validarTotalGeneral() {
        int totalCalculado = 0;
        for (int i = 0; i < indiceVenta; i++) {
            int edad = Integer.parseInt(ventas[i][3]);
            String zona = ventas[i][5];
            totalCalculado += calcularPrecioFinal(zona, edad);
        }
        if (totalCalculado != totalGeneral) {
            System.out.println("[ERROR] Total general inconsistente. Esperado: " + totalCalculado + " / Actual: " + totalGeneral);
            return false;
        }
        return true;
    }
    
/**
 * Ejecuta una verificación completa del sistema revisando tres aspectos:
 * - Consistencia entre ventas y asientos ocupados
 * - Consistencia entre reservas y asientos reservados
 * - Coherencia del total de ingresos calculado
 * Muestra mensajes de error si detecta problemas y un resumen final del estado.
 */
    static void validarIntegridadSistema() {
        System.out.println("\n>>> VERIFICACIÓN DE INTEGRIDAD <<<");

        boolean ventasOK = validarVentasVsAsientos();
        boolean reservasOK = validarReservasVsAsientos();
        boolean totalOK = validarTotalGeneral();

        if (ventasOK && reservasOK && totalOK) {
            System.out.println(">>> Sistema coherente y sin errores.");
        } else {
            System.out.println(">>> Se detectaron inconsistencias en el sistema.");
        }
    }
 
 /**
 * Mide el tiempo que tarda en ejecutarse una operación (método) específica.
 * Recibe un nombre para la operación y una función que se ejecuta como tarea.
 * Al finalizar, imprime el tiempo total de ejecución en milisegundos.
 */
    static void medirTiempo(String nombreOperacion, Runnable operacion) {
        long inicio = System.currentTimeMillis();
        operacion.run();
        long fin = System.currentTimeMillis();
        System.out.println(">> Tiempo para " + nombreOperacion + ": " + (fin - inicio) + " ms\n");
    }
}

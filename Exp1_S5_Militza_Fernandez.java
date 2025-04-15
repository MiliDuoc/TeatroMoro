package teatromoro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TeatroMoro {

    static Scanner scanner = new Scanner(System.in);
    // Matriz de asientos (10 filas x 10 columnas) 100 asientos
    static String[][] asientos = new String[10][10];
    static List<Entrada> resumenCompras = new ArrayList<>(); // Lista de todas las compras

    // Variables estÃ¡ticas para estadÃ­sticas globales
    static int totalGeneral = 0;
    static int entradasVendidas = 0;
    static String nombreTeatro = "TEATRO MORO";

    public static void main(String[] args) {
        inicializarPlano(asientos); // Llenar el plano con zonas

        int opcion;
            // Bienvenida e informaciÃ³n de precios y descuentos
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

        // Ciclo principal del programa con menÃº principal
        for (;;) {

            // MenÃº principal con validaciÃ³n de opciÃ³n
            while (true) {
                System.out.println("\n|=================================================|");
                System.out.println("|                  MENU PRINCIPAL                 |");
                System.out.println("|=================================================|");
                System.out.println("| 1. Comprar entrada                              |");
                System.out.println("| 2. Ver promociones                              |");
                System.out.println("| 3. Buscar entrada                               |");
                System.out.println("| 4. Eliminar entrada                             |");
                System.out.println("| 5. Salir                                        |");
                System.out.println("|=================================================|");
                System.out.print("Seleccione una opcion: ");
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    if (opcion >= 1 && opcion <= 5) break;
                    System.out.println("\n Opcion fuera de rango. Intente nuevamente.");
                } else {
                    System.out.println("\n Entrada no valida. Debe ingresar un numero.");
                    scanner.next(); // Limpiar entrada invÃ¡lida
                }
            }

            switch (opcion) {
                case 1 -> comprarEntrada();
                case 2 -> verPromociones();
                case 3 -> buscarEntrada();
                case 4 -> eliminarEntrada();
                case 5 -> {
                    if (!resumenCompras.isEmpty()) mostrarResumenFinal();
                    System.out.println("Gracias por usar el sistema. Hasta luego.");
                    return;
                }
            }
        }
    }

    // Clase para representar cada entrada vendida
    static class Entrada {
        int numero;
        String zona;
        int precioFinal;
        boolean estudiante;
        boolean terceraEdad;

        public Entrada(int numero, String zona, int precioFinal, boolean estudiante, boolean terceraEdad) {
            this.numero = numero;
            this.zona = zona;
            this.precioFinal = precioFinal;
            this.estudiante = estudiante;
            this.terceraEdad = terceraEdad;
        }
    }

    // Comprar una o mÃ¡s entradas
    static void comprarEntrada() {
    List<Entrada> entradasDeEstaCompra = new ArrayList<>();
    mostrarPlano(); // Mostrar asientos disponibles

    int cantidad;
    while (true) {
        cantidad = pedirEntero("Cuantas entradas desea comprar? (maximo 4): ");
        if (cantidad >= 1 && cantidad <= 4) break;
        System.out.println("Debe ingresar entre 1 y 4 entradas.");
    }

    for (int i = 0; i < cantidad; i++) {
        System.out.println("\nCompra #" + (i + 1));
        int numAsiento, fila, columna;

        // Solicitud y validaciÃ³n de nÃºmero de asiento
        while (true) {
            numAsiento = pedirEntero("Ingrese numero de asiento (1-100): ");
            fila = (numAsiento - 1) / 10;
            columna = (numAsiento - 1) % 10;

            if (numAsiento < 1 || numAsiento > 100) {
                System.out.println("Numero fuera de rango.");
            } else if (asientos[fila][columna].equals("[---]")) {
                System.out.println("Asiento ocupado.");
            } else {
                break;
            }
        }

        int edad = pedirEntero("Ingrese su edad: ");
        boolean estudiante = edad < 25;
        boolean terceraEdad = edad >= 60;

        String zona = obtenerTipoEntrada(fila);
        int precio = switch (zona) {
            case "VIP" -> 30000;
            case "PLATEA BAJA" -> 15000;
            case "PLATEA ALTA" -> 18000;
            default -> 13000;
        };

        int descuento = estudiante ? (int)(precio * 0.10) :
                         terceraEdad ? (int)(precio * 0.15) : 0;
        int total = precio - descuento;

        asientos[fila][columna] = "[---]";
        resumenCompras.add(new Entrada(numAsiento, zona, total, estudiante, terceraEdad));
        totalGeneral += total;
        entradasVendidas++;

        System.out.println("Compra realizada. Total pagado: $" + total);
        mostrarResumenCompra(zona, numAsiento, precio, descuento, total);
        entradasDeEstaCompra.add(new Entrada(numAsiento, zona, total, estudiante, terceraEdad));

    }
    System.out.println("\nResumen de esta compra:");
    System.out.println("|===========================================================|");
    System.out.println("|              RESUMEN DE ENTRADAS COMPRADAS                |");
    System.out.println("|===========================================================|");
    System.out.printf("| %-22s | %-9s | %-20s |\n", "Tipo de entrada", "Asiento", "Total a pagar");
    System.out.println("|------------------------|-----------|----------------------|");

    int totalCompra = 0;
    for (Entrada e : entradasDeEstaCompra) {
        System.out.printf("| %-22s | %9d | $%-19d |\n", e.zona, e.numero, e.precioFinal);
        totalCompra += e.precioFinal;
    }

    // Aplicar 5% de descuento adicional si se compraron 3 o mÃ¡s entradas
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

}

    // Mostrar promociones disponibles
    static void verPromociones() {
        System.out.println("\n|=================================================|");
        System.out.println("|              PROMOCIONES DISPONIBLES            |");
        System.out.println("|=================================================|");
        System.out.println("| - 10% de descuento para estudiantes (<25 anios) |");
        System.out.println("| - 15% de descuento para tercera edad (â‰¥60 anios)|");
        System.out.println("| - 5% adicional si compra 3 o mas entradas       |");
        System.out.println("|=================================================|");
    }


    // Buscar entradas por distintos criterios
    static void buscarEntrada() {
        System.out.println("\nBuscar por:");
        System.out.println("1. Numero de asiento");
        System.out.println("2. Zona");
        System.out.println("3. Tipo (Estudiante/Tercera edad)");
        int tipo = pedirEntero("Seleccione opcion: ");

        switch (tipo) {
            case 1 -> {
                int n = pedirEntero("Numero de asiento: ");
                boolean encontrado = false;
                for (Entrada e : resumenCompras) {
                    if (e.numero == n) {
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

        int opcionZona = pedirEntero("Seleccione el numero de la zona: ");
        String zonaBuscada = "";

        if (opcionZona == 5) break;

        if (opcionZona >= 1 && opcionZona <= 4) {
            zonaBuscada = switch (opcionZona) {
                case 1 -> "VIP";
                case 2 -> "PLATEA BAJA";
                case 3 -> "PLATEA ALTA";
                case 4 -> "PALCOS";
                default -> ""; // no deberÃ­a pasar
            };
        } else {
            System.out.println("Opcion invalida. Intente nuevamente.");
            continue; // ahora sÃ­ se puede usar, porque estamos en el while
        }

        boolean encontrado = false;
        for (Entrada e : resumenCompras) {
            if (e.zona.equalsIgnoreCase(zonaBuscada)) {
                mostrarEntrada(e);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontraron entradas en esa zona.");
        } else {
            break; // salir si se encontrÃ³ al menos una
        }
    }
}





case 3 -> {
    while (true) {
        System.out.println("\nTIPOS DE CLIENTE DISPONIBLES:");
        System.out.println("1. Estudiante");
        System.out.println("2. Tercera edad");
        System.out.println("3. Volver al menu");

        int opcionTipo = pedirEntero("Seleccione el numero del tipo de cliente: ");

        if (opcionTipo == 3) break;

        boolean buscarEstudiante = false;
        boolean buscarTercera = false;

        if (opcionTipo == 1) {
            buscarEstudiante = true;
        } else if (opcionTipo == 2) {
            buscarTercera = true;
        } else {
            System.out.println("OpciÃ³n invÃ¡lida. Intente nuevamente.");
            continue;
        }

        boolean encontrado = false;
        for (Entrada e : resumenCompras) {
            if ((buscarEstudiante && e.estudiante) || (buscarTercera && e.terceraEdad)) {
                mostrarEntrada(e);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontraron entradas de ese tipo.");
        } else {
            break; // Salir si se encontrÃ³ al menos una
        }
    }
}


            default -> System.out.println("OpciÃ³n invÃ¡lida.");
        }
    }

    // Eliminar una entrada especÃ­fica por nÃºmero
static void eliminarEntrada() {
    while (true) {
        System.out.println("\n|------------------------------------------------|");
        System.out.println("|          ELIMINAR ENTRADA COMPRADA             |");
        System.out.println("|------------------------------------------------|");
        System.out.println("| Ingrese el numero de asiento a eliminar        |");
        System.out.println("| o escriba 0 para volver al menu principal.     |");
        System.out.println("|------------------------------------------------|");

        int num = pedirEntero("Numero de asiento (1-100 o 0 para salir): ");

        if (num == 0) {
            System.out.println("Operacion cancelada. Volviendo al menu principal.");
            break;
        }

        Entrada encontrada = null;

        for (Entrada e : resumenCompras) {
            if (e.numero == num) {
                encontrada = e;
                break;
            }
        }

        if (encontrada != null) {
            int fila = (num - 1) / 10;
            int col = (num - 1) % 10;
            asientos[fila][col] = String.format("[%03d]", num);
            resumenCompras.remove(encontrada);
            totalGeneral -= encontrada.precioFinal;
            entradasVendidas--;
            System.out.println("\n Entrada eliminada con exito.");

            // Preguntar si desea eliminar otra
            scanner.nextLine(); // limpiar buffer
            while (true) {
                System.out.print("Desea eliminar otra entrada? (s/n): ");
                String opcion = scanner.nextLine().trim().toLowerCase();

                if (opcion.equals("s")) {
                    break; // vuelve al inicio del while principal
                } else if (opcion.equals("n")) {
                    System.out.println("Volviendo al menu principal...");
                    return; // salir del metodo eliminarEntrada
                } else {
                    System.out.println("Opcion no valida. Ingrese 's' o 'n'.");
                }
            }

        } else {
            System.out.println("\n Entrada no encontrada. Intente con otro numero.");
        }
    }
}


    // Mostrar resumen final con total e ingresos
    static void mostrarResumenFinal() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        System.out.println();
        System.out.println("|===========================================================|");
        System.out.println("|                 RESUMEN TOTAL DE COMPRAS                  |");
        System.out.println("|===========================================================|");
        System.out.printf("| %-22s | %-9s | %-20s |\n", "Tipo de entrada", "Asiento", "Total a pagar");
        System.out.println("|------------------------|-----------|----------------------|");

        for (Entrada compra : resumenCompras) {
            System.out.printf("| %-22s | %9d | $%-19d |\n", compra.zona, compra.numero, compra.precioFinal);
        }

        System.out.println("|------------------------|-----------|----------------------|");
        System.out.printf("| %-34s : $%-19d |\n", "TOTAL GENERAL", totalGeneral);
        System.out.printf("| %-34s : %-20s |\n", "Fecha y hora", ahora.format(formato));
        System.out.printf("| %-57s |\n", "Gracias por su compra. Disfrute la funcion.");
        System.out.println("|===========================================================|");
    }

    // Mostrar detalle individual de una entrada
    static void mostrarEntrada(Entrada e) {
        System.out.println("\n|-----------------------------------------------|");
        System.out.println("|-----------   INFORMACION DE ENTRADA   --------|");
        System.out.println("|-----------------------------------------------|");
        System.out.printf("| %-20s: %-23s |\n", "Zona", e.zona);
        System.out.printf("| %-20s: %-23d |\n", "No de asiento", e.numero);
        System.out.printf("| %-20s: $%-22d |\n", "Total pagado", e.precioFinal);

        String tipo = e.estudiante ? "Estudiante" : e.terceraEdad ? "Tercera edad" : "General";
        System.out.printf("| %-20s: %-23s |\n", "Tipo de cliente", tipo);
        System.out.println("|-----------------------------------------------|");
    }


    // Inicializa los asientos con sus nÃºmeros
static void inicializarPlano(String[][] asientos) {
    for (int i = 0, num = 1; i < 10; i++) {
        for (int j = 0; j < 10; j++, num++) {
            asientos[i][j] = String.format("[%03d]", num);
        }
    }

    // Asientos ocupados al iniciar (pre-cargados con informaciÃ³n)
    ocuparAsiento(0, 0, 22);  // estudiante
    ocuparAsiento(0, 1, 65);  // tercera edad
    ocuparAsiento(3, 0, 30);  // general
    ocuparAsiento(5, 0, 24);  // estudiante
    ocuparAsiento(7, 0, 68);  // tercera edad
}

// Esta funciÃ³n simula la compra y guarda la entrada
static void ocuparAsiento(int fila, int columna, int edad) {
    int numero = fila * 10 + columna + 1;
    String zona = obtenerTipoEntrada(fila);
    int precio = switch (zona) {
        case "VIP" -> 30000;
        case "PLATEA BAJA" -> 15000;
        case "PLATEA ALTA" -> 18000;
        default -> 13000;
    };

    boolean estudiante = edad < 25;
    boolean terceraEdad = edad >= 60;
    int descuento = estudiante ? (int)(precio * 0.10) :
                     terceraEdad ? (int)(precio * 0.15) : 0;
    int total = precio - descuento;

    // Marcar como ocupado y registrar
    asientos[fila][columna] = "[---]";
    resumenCompras.add(new Entrada(numero, zona, total, estudiante, terceraEdad));
    totalGeneral += total;
    entradasVendidas++;
}



    // Muestra visualmente el plano del teatro
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
    }

    // Determina la zona segÃºn la fila
    static String obtenerTipoEntrada(int fila) {
        return switch (fila) {
            case 0, 1, 2 -> "VIP";
            case 3, 4 -> "PLATEA BAJA";
            case 5, 6 -> "PLATEA ALTA";
            default -> "PALCOS";
        };
    }
    static void mostrarResumenCompra(String tipo, int asiento, int base, int descuento, int total) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

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

    // Pide un nÃºmero entero con validaciÃ³n
    
    
    
    static int pedirEntero(String mensaje) {
        int valor;
        while (true) {
            try {
                System.out.print(mensaje);
                valor = Integer.parseInt(scanner.next());
                if (valor >= 0) break;
                else System.out.println("Debe ser un numero positivo.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada no valida.");
            }
        }
        return valor;
    }
}






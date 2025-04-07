package teatro.moro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TeatroMoro {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Matriz de asientos (10 filas x 10 columnas) 100 asientos
        String[][] asientos = new String[10][10];
        inicializarPlano(asientos); // Llenar el plano con zonas

        List<String[]> resumenCompras = new ArrayList<>(); // Lista de todas las compras
        int totalGeneral = 0; // Acumulador total
        int opcion;
        boolean huboCompras = false;

        // Ciclo principal del programa con menú principal
        for (;;) {
            // Bienvenida e información de precios y descuentos
            System.out.println("|=================================================|");
            System.out.println("|       BIENVENIDO AL SISTEMA DE ENTRADAS         |");
            System.out.println("|                 TEATRO MORO                     |");
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

            // Menú principal con validación de opción
            while (true) {
                System.out.println("\n--- MENU PRINCIPAL ---");
                System.out.println("1. Comprar entrada");
                System.out.println("2. Salir");
                System.out.print("Seleccione una opcion: ");

                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    if (opcion == 1 || opcion == 2) break;
                    System.out.println("Opcion fuera de rango. Intente nuevamente.");
                } else {
                    System.out.println("Entrada no valida. Debe ingresar un numero.");
                    scanner.next(); // Limpiar entrada inválida
                }
            }

            // Si elige salir y hubo compras, mostrar resumen
            if (opcion == 2) {
                if (huboCompras) mostrarResumenFinal(resumenCompras, totalGeneral);
                System.out.println("Gracias por usar el sistema. Hasta luego.");
                break;
            }

            // Variables de asiento
            int asientoNum = -1, fila = -1, columna = -1;

            // Solicitud y validación de número de asiento
            while (true) {
                mostrarPlano(asientos);

                while (true) {
                    System.out.print("Ingrese numero de asiento (1-100): ");
                    if (scanner.hasNextInt()) {
                        asientoNum = scanner.nextInt();
                        if (asientoNum >= 1 && asientoNum <= 100) {
                            fila = (asientoNum - 1) / 10;
                            columna = (asientoNum - 1) % 10;

                            // Verificar si el asiento ya está ocupado
                            if (asientos[fila][columna].equals("[---]")) {
                                System.out.println("Ese asiento ya esta ocupado. Intente con otro.\n");
                            } else {
                                break;
                            }
                        } else {
                            System.out.println("Numero fuera de rango. Intente nuevamente.\n");
                        }
                    } else {
                        System.out.println("Entrada no valida. Debe ingresar un numero.");
                        scanner.next();
                    }
                }

                break;
            }

            // Determinar tipo de entrada según la fila
            String tipoEntrada = obtenerTipoEntrada(fila);

            // Asignar precio base según tipo
            int precioBase = switch (tipoEntrada) {
                case "VIP" -> 30000;
                case "PLATEA BAJA" -> 15000;
                case "PLATEA ALTA" -> 18000;
                case "PALCOS" -> 13000;
                default -> 0;
            };

            // Solicitar edad para calcular descuento
            System.out.print("Ingrese su edad: ");
            int edad = scanner.nextInt();
            if (edad <= 0 || edad > 120) {
                System.out.println("Edad invalida. Intente nuevamente.");
                continue;
            }

            // Aplicar descuentos por edad
            int descuento = 0;
            if (edad < 25) {
                descuento = (int)(precioBase * 0.10);
            } else if (edad >= 60) {
                descuento = (int)(precioBase * 0.15);
            }

            int totalFinal = precioBase - descuento;
            asientos[fila][columna] = "[---]"; // Marcar asiento como ocupado
            huboCompras = true;

            // Guardar compra en resumen
            resumenCompras.add(new String[] {
                tipoEntrada, String.valueOf(asientoNum), String.valueOf(totalFinal)
            });
            totalGeneral += totalFinal;

            // Mostrar resumen individual de compra
            mostrarResumenCompra(tipoEntrada, asientoNum, precioBase, descuento, totalFinal);

            // Preguntar si desea realizar otra compra
            String continuar;
            do {
                System.out.print("\n ¿Desea realizar otra compra? (s/n): ");
                continuar = scanner.next().toLowerCase();
                if (!continuar.equals("s") && !continuar.equals("n")) {
                    System.out.println("Entrada no valida. Por favor ingrese 's' o 'n'.");
                }
            } while (!continuar.equals("s") && !continuar.equals("n"));

            if (continuar.equals("n")) {
                if (huboCompras) mostrarResumenFinal(resumenCompras, totalGeneral);
                System.out.println("Gracias por su compra. Hasta pronto.");
                break;
            }
        }

        scanner.close();
    }

    // Llena la matriz con asientos disponibles
    public static void inicializarPlano(String[][] asientos) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                asientos[i][j] = "[ ]";
            }
        }

        // Ejemplos de asientos ya ocupados al iniciar
        asientos[0][0] = "[---]";
        asientos[0][1] = "[---]";
        asientos[3][0] = "[---]";
        asientos[5][0] = "[---]";
        asientos[7][0] = "[---]";
    }

    // Muestra el plano del teatro con zonas y asientos
    public static void mostrarPlano(String[][] asientos) {
        System.out.println("\n|=================================================|");
        System.out.println("|                PLANO DEL TEATRO                 |");
        System.out.println("|=================================================|");
        System.out.println("| ZONAS:                                          |");
        System.out.println("|  - VIP (Filas 1-3)                              |");
        System.out.println("|  - Platea Baja (Filas 4-5)                      |");
        System.out.println("|  - Platea Alta (Filas 6-7)                      |");
        System.out.println("|  - Palcos (Filas 8-10)                          |");
        System.out.println("|=================================================|");
        System.out.println("\n                                   >>> ESCENARIO <<<\n");

        int numero = 1;
        for (int i = 0; i < 10; i++) {
            String tipo = obtenerTipoEntrada(i);
            System.out.printf("%-13s ", tipo);

            for (int j = 0; j < 10; j++) {
                String estado = asientos[i][j].equals("[---]") ? "[---]" : String.format("[%03d]", numero);
                System.out.print(" " + estado);
                numero++;
            }

            System.out.println();

            if (i == 2 || i == 4 || i == 6) {
                System.out.println();
            }
        }

        System.out.println("\n[---] Ocupado");
    }

    // Determina la zona del asiento segun la fila
    public static String obtenerTipoEntrada(int fila) {
        return switch (fila) {
            case 0, 1, 2 -> "VIP";
            case 3, 4 -> "PLATEA BAJA";
            case 5, 6 -> "PLATEA ALTA";
            default -> "PALCOS";
        };
    }

    // Muestra el resumen de una compra individual
    public static void mostrarResumenCompra(String tipo, int asiento, int base, int descuento, int total) {
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

    // Muestra el resumen general de todas las compras realizadas
    public static void mostrarResumenFinal(List<String[]> compras, int total) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        System.out.println();
        System.out.println("|===========================================================|");
        System.out.println("|                 RESUMEN TOTAL DE COMPRAS                  |");
        System.out.println("|===========================================================|");
        System.out.printf("| %-22s | %-9s | %-20s |\n", "Tipo de entrada", "Asiento", "Total a pagar");
        System.out.println("|------------------------|-----------|----------------------|");

        for (String[] compra : compras) {
            System.out.printf("| %-22s | %9s | $%-19s |\n", compra[0], compra[1], compra[2]);
        }

        System.out.println("|------------------------|-----------|----------------------|");
        System.out.printf("| %-34s : $%-19d |\n", "TOTAL GENERAL", total);
        System.out.printf("| %-34s : %-20s |\n", "Fecha y hora", ahora.format(formato));
        System.out.printf("| %-57s |\n", "Gracias por su compra. Disfrute la funcion.");
        System.out.println("|===========================================================|");
    }
}

package com.mycompany.crudhibernate;

import java.util.ArrayList;
import java.util.Scanner;
import models.Carta;
import models.Pedido;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 *
 * @author marco
 */
public class Main {

    private static SessionFactory sf = new Configuration().configure().buildSessionFactory();
    private static Session s = sf.openSession();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int seleccionMenu = 0;
        boolean menuActivo = true;
        Scanner input = new Scanner(System.in);

        while (menuActivo) {
            System.out.println("1- Listar carta disponible");
            System.out.println("2- Listar todos los pedidos");
            System.out.println("3- Listar los pedidos de hoy");
            System.out.println("4- Crear un pedido");
            System.out.println("5- Borrar un pedido");
            System.out.println("6- Marcar pedido como recogido");
            System.out.println("7- Salir");

            seleccionMenu = input.nextInt();
            switch (seleccionMenu) {
                case 1:
                    listarCarta();
                    break;
                case 2:
                    listarPedidos();
                    break;
                case 3:
                    listarPedidosPendientes();
                    break;
                case 4:
                    crearPedido();
                    break;
                case 5:
                    borrarPedido();
                    break;
                case 6:
                    marcarRecogido();
                    break;
                case 7:
                    menuActivo = false;
                    break;
                default:
                    System.out.println("Elija una opción válida");
            }
        }

    }

    public static void listarCarta() {

        Query q = s.createQuery("FROM Carta", Carta.class);
        ArrayList<Carta> resultado = (ArrayList<Carta>) q.list();

        resultado.forEach((c) -> System.out.println(c));

    }

    public static void listarPedidos() {

        Query q = s.createQuery("FROM Pedido", Pedido.class);
        ArrayList<Pedido> resultado = (ArrayList<Pedido>) q.list();

        resultado.forEach((c) -> System.out.println(c));

    }

    public static void listarPedidosPendientes() {

        java.util.Date ahora = new java.util.Date();
        java.sql.Date fechaActual = new java.sql.Date(ahora.getTime());

        Query q = s.createQuery("FROM Pedido p WHERE p.estado = 'SIN ENTREGAR' AND p.fecha = :fecha", Pedido.class);
        q.setParameter("fecha", fechaActual);
        ArrayList<Pedido> resultado = (ArrayList<Pedido>) q.list();

        resultado.forEach((p) -> System.out.println(p));

    }

    public static void crearPedido() {

        Pedido pedido = new Pedido();
        Scanner input = new Scanner(System.in);
        Scanner id_input = new Scanner(System.in);

        listarCarta();
        System.out.println("Inserte el ID del pedido que desea: ");
        Long id = id_input.nextLong();
        System.out.println("Inserte un nombre para el pedido: ");
        String nombre = input.nextLine();

        Carta producto = s.load(Carta.class, id);

        java.util.Date ahora = new java.util.Date();
        java.sql.Date fecha = new java.sql.Date(ahora.getTime());

        pedido.setId(0L);
        pedido.setEstado("Sin entregar");
        pedido.setFecha(fecha);
        pedido.setNombre(nombre);
        pedido.setProducto_id(id);

        Transaction t = s.beginTransaction();
        s.save(pedido);
        t.commit();

    }

    public static void borrarPedido() {

        Scanner id_input = new Scanner(System.in);

        listarPedidos();
        System.out.println("Selecciona el id del pedido para eliminar: ");
        Long id = id_input.nextLong();

        Transaction t = s.beginTransaction();
        Pedido pedido = s.load(Pedido.class, id);
        s.remove(pedido);
        t.commit();

    }

    private static void marcarRecogido() {

        Scanner id_input = new Scanner(System.in);

        listarPedidos();
        System.out.println("Selecciona el id para marcar como recogido: ");
        Long id = id_input.nextLong();

        Transaction t = s.beginTransaction();
        Pedido pedido = s.load(Pedido.class, id);
        pedido.setEstado("Recogido");
        s.update(pedido);
        t.commit();

    }

}

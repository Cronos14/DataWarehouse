package com.masnegocio.oracle;


import java.util.Scanner;

import javax.xml.xpath.XPath;

import org.w3c.dom.Document;



public class Main {

	public static void main(String[] args) {

		Document doc;
		XPath xpath;
		Scanner scan = new Scanner(System.in);
		System.out.println("introduce la cantidad total a recuperar: ");
		int maxTotal =scan.nextInt();
		System.out.println("introduce la cantidad de hilos a ejecutar");
		int totalHilos = scan.nextInt();
		System.out.println("introduce la cantidad de registros a recuperar por hilo: ");
		int maxFacturasPorHilo = scan.nextInt();
		System.out.println("introduce la cantidad para la separacion por carpeta: ");
		int splitFolder = scan.nextInt();
		System.out.println("introduce la ruta donde se almacenaran los datos: ");
		String ruta = scan.nextLine();
		ruta = scan.nextLine();
		System.out.println("-------datos de la base de datos------");
		System.out.println("introduce el host: ");
		ConexionBD.host = scan.nextLine();
		System.out.println("introduce el puerto: ");
		ConexionBD.puerto = scan.nextLine();
		System.out.println("introduce el esquema: ");
		ConexionBD.esquema = scan.nextLine();
		System.out.println("introduce el usuario: ");
		ConexionBD.usuario = scan.nextLine();
		System.out.println("introduce el password: ");
		ConexionBD.password = scan.nextLine();
		FacturaTest factura = new FacturaTest(maxTotal,maxFacturasPorHilo,splitFolder,ruta);
		factura.selectConHilos(totalHilos);
		
		
		
		
	}
	
	

}

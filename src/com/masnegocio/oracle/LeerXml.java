package com.masnegocio.oracle;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LeerXml {

	public static void main(String[] args) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		DocumentBuilder builder;
		try {

			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);

			XPath xPath = XPathFactory.newInstance().newXPath();
			builder = factory.newDocumentBuilder();
			Document document = builder
					.parse(new File("C:\\Users\\raul.gonzalez\\Documents\\Facturas\\1\\factura_1.xml"));
			
			String dato = xPath.compile("/Results/Row/NOMBRE/@att").evaluate(document);
			System.out.println("dato: "+dato);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

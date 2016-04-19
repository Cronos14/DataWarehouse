package com.masnegocio.oracle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FacturaTest {

	private long maxFacturasTotal = 1000000;
	private long splitFolders = 10000;
	private int maxFacturasPorHilo = 50000;
	private String ruta;
	
	public FacturaTest() {
	}
	
	public FacturaTest(int maxFacturasTotal, int maxFacturasPorHilo,int splitFolders,String ruta) {
		this.maxFacturasTotal = maxFacturasTotal;
		this.splitFolders = splitFolders;
		this.maxFacturasPorHilo = maxFacturasPorHilo;
		this.ruta = ruta;
	}

	public ArrayList<HiloInsertar> insertarConHilos(int cantidad) {

		ArrayList<HiloInsertar> hilos = new ArrayList<HiloInsertar>();

		for (int i = 1; i <= cantidad; i++) {

			hilos.add(insertar(i));
		}

		return hilos;

	}

	private HiloInsertar insertar(int index) {

		HiloInsertar myRunnable = new HiloInsertar(index);
		Thread hilo = new Thread(myRunnable);

		hilo.start();

		return myRunnable;
	}

	public ArrayList<HiloSelect> selectConHilos(int cantidad) {

		ArrayList<HiloSelect> hilos = new ArrayList<HiloSelect>();

		for (int i = 1; i <= cantidad; i++) {

			hilos.add(select(i));
		}

		return hilos;

	}

	private HiloSelect select(int index) {

		HiloSelect myRunnable = new HiloSelect(index);
		Thread hilo = new Thread(myRunnable);

		hilo.start();

		return myRunnable;
	}

	class HiloInsertar implements Runnable {

		private long tiempoInicial;
		private long tiempoFinal;
		private Connection conn;
		private Statement st;
		private int index;
		private int indexInicial;

		public HiloInsertar(int index) {
			this.index = index;
			this.conn = ConexionBD.getConnection();
			;
			try {
				this.st = conn.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {

				tiempoInicial = System.currentTimeMillis();

				indexInicial = ((index * maxFacturasPorHilo) - (maxFacturasPorHilo - 1));
				System.out.println("iniciando inserts facturas: " + indexInicial);
				for (int i = indexInicial; i <= (index * maxFacturasPorHilo); i++) {

					st.executeUpdate(
							"insert into masnegocio.factura (id,nombre)" + "values (" + i + ",'factura" + i + "')");

				}

				st.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				tiempoFinal = System.currentTimeMillis();
				System.out.println("El hilo " + index + " del monto inicial: " + indexInicial
						+ " termino con el tiempo: " + (getRunningTime() / 1000) + "(s)");
			}
		}

		public int getIndex() {
			return index;
		}

		public long getRunningTime() {
			return (tiempoFinal - tiempoInicial);
		}

		public void setTiempoInicial(long tiempoInicial) {
			this.tiempoInicial = tiempoInicial;
		}

		public void setTiempoFinal(long tiempoFinal) {
			this.tiempoFinal = tiempoFinal;
		}

		public void setIndexInicial(int indexInicial) {
			this.indexInicial = indexInicial;
		}

		public int getIndexInicial() {
			return this.indexInicial;
		}

		public Statement getStatement() {
			return st;
		}

		public Connection getConnection() {
			return conn;
		}

	}

	class HiloSelect extends HiloInsertar {

		
		public HiloSelect(int index) {
			super(index);
			
		}

		@Override
		public void run() {
			try {
				setTiempoInicial(System.currentTimeMillis());

				setIndexInicial(((getIndex() * maxFacturasPorHilo) - (maxFacturasPorHilo - 1)));
				System.out.println("iniciando selects facturas: " + getIndexInicial());

				ResultSet rs = getStatement().executeQuery("select * from masnegocio.factura where id >= "
						+ getIndexInicial() + " and " + "id <= " + (getIndex() * maxFacturasPorHilo));

				
				while (rs.next()) {
					try {
						parseToXml(rs,rs.getInt("id"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}

				getStatement().close();
				getConnection().close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				setTiempoFinal(System.currentTimeMillis());
				System.out.println("El hilo " + getIndex() + " del monto inicial: " + getIndexInicial()
						+ " termino con el tiempo: " + (getRunningTime() / 1000) + "(s)");

			}
		}

		

	}

	public void parseToXml(ResultSet rs, int numeroFactura) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element results = doc.createElement("Results");
		doc.appendChild(results);

		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();

		//while (rs.next()) {
			Element row = doc.createElement("Row");
			results.appendChild(row);
			for (int i = 1; i <= colCount; i++) {
				String columnName = rsmd.getColumnName(i);
				Object value = rs.getObject(i);
				Element node = doc.createElement(columnName);
				node.appendChild(doc.createTextNode(value.toString()));
				row.appendChild(node);
			}
		//}
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);

		//System.out.println(sw.toString());
		//C:\\Users\\raul.gonzalez\\Documents\\Facturas
		crearXML(ruta,"factura"+"_"+numeroFactura+".xml",numeroFactura,sw.toString());
		// rs.close();
	}

	private void crearXML(String ruta, String nombreArchivo,int numeroFactura,String texto) throws IOException{


	        for(int i = 1;i<=maxFacturasTotal;i+=splitFolders){
	        	if(numeroFactura>=i && numeroFactura < i+splitFolders){
	        		ruta+=File.separator+i;
	        		break;
	        	}
	        }
	        

	        File rutaArchivo = new File(ruta);
	        rutaArchivo.mkdirs();
	        
	        File archivo = new File(ruta+File.separator+nombreArchivo);
	        BufferedWriter bw;
	        if(archivo.exists()) {
	            bw = new BufferedWriter(new FileWriter(archivo));
	            bw.write(texto);
	        } else {
	            bw = new BufferedWriter(new FileWriter(archivo));
	            bw.write(texto);
	        }
	        bw.close();
	}

}

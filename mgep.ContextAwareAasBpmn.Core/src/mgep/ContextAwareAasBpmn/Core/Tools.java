package mgep.ContextAwareAasBpmn.Core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Tools {
	public static String GRAPHDB_SERVER = "http://localhost:7200/";
	public static String REPOSITORY_ID = "DeviceServiceOnt_Exp01";
	public static String DEVICE_SERVICE_ONT_IRI = "https://www.mondragon.edu/ontologies/2022/9/DeviceServiceOnt#";
	public static String RDF_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	/**
	 * Get a random number given min and max values
	 * @param min
	 * @param max
	 * @return a random int
	 */
	public static int GetRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
	
	/**
	 * Loads configuration variables about graphdb repository from a config file
	 * @param filePath
	 */
	public static void LoadEnvironmentFromPropertiesFile(String filePath) {
		var prop = new Properties();
	    InputStream input = null;

	    try {
	        input = new FileInputStream(filePath);

	        // load a properties file
	        prop.load(input);

	        // get the property value and set it out
	        GRAPHDB_SERVER = prop.getProperty("GRAPHDB_SERVER");
	        REPOSITORY_ID = prop.getProperty("REPOSITORY_ID");
	        DEVICE_SERVICE_ONT_IRI = prop.getProperty("DEVICE_SERVICE_ONT_IRI");
	        RDF_IRI = prop.getProperty("RDF_IRI");

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
}

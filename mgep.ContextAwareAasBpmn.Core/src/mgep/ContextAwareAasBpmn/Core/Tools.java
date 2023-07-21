package mgep.ContextAwareAasBpmn.Core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mgep.ContextAwareAasBpmn.Entities.QualityParameterDTO;
import mgep.ContextAwareAasBpmn.Enums.*;

public final class Tools {
	public static String GRAPHDB_SERVER = "http://localhost:7200/";
	public static String REPOSITORY_ID = "DeviceServiceOnt_Exp01";
	public static String DEVICE_SERVICE_ONT_IRI = "https://www.mondragon.edu/ontologies/2022/9/DeviceServiceOnt#";
	public static String RDF_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String AAS_SERVER = "http://localhost:8081/";
	public static int INTERVAL_GATHER_QOS_VALS = 10000;
	
	public static String ROS_SERVER_IP = "34.95.194.5";
	public static String ROS_SERVER_HTTP_PORT = "5000";
	public static String ROS_SERVER_PROTOCOL = "http";
	
	public static String NODEREDWM_SERVER_IP = "localhost";
	public static String NODEREDWM_HTTP_PORT = "1880";
	public static String NODEREDWM_PROTOCOL = "http";
	
	public static String CA_SERVER_IP  = "localhost";
	public static String CA_PROTOCOL = "http";
	public static String CA_HTTP_PORT = "8080";
	
	/**
	 * Get a random number given min and max values
	 * @param min
	 * @param max
	 * @return a random int
	 */
	public static int GetRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min + 1)) + min);
	}
	
	/**
	 * Loads configuration variables about graphdb repository, AAS server and so, from a config file
	 * @param filePath
	 */
	public static void LoadEnvironmentFromPropertiesFile(String filePath) {
	    InputStream input = null;

	    try {
	    	filePath = URLDecoder.decode(filePath, "UTF-8");
	        // If you're on Windows, you might need to remove the leading slash
	        if (filePath.startsWith("/")) {
	            filePath = filePath.substring(1);
	        }
	        
	        input = new FileInputStream(filePath);

	        LoadEnvironmentFromPropertiesFileStream(input);
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
	
	public static void LoadEnvironmentFromPropertiesFileStream(InputStream fileStream) {
		var prop = new Properties();

	    try {
	        // load a properties file
	        prop.load(fileStream);

	        // get the property value and set it out
	        GRAPHDB_SERVER = prop.getProperty("GRAPHDB_SERVER");
	        REPOSITORY_ID = prop.getProperty("REPOSITORY_ID");
	        DEVICE_SERVICE_ONT_IRI = prop.getProperty("DEVICE_SERVICE_ONT_IRI");
	        RDF_IRI = prop.getProperty("RDF_IRI");
	        AAS_SERVER = prop.getProperty("AAS_SERVER");
	        INTERVAL_GATHER_QOS_VALS = Integer.valueOf(prop.getProperty("INTERVAL_GATHER_QOS_VALS"));
	        ROS_SERVER_IP = prop.getProperty("ROS_SERVER_IP");
	        ROS_SERVER_HTTP_PORT = prop.getProperty("ROS_SERVER_HTTP_PORT");
	        ROS_SERVER_PROTOCOL = prop.getProperty("ROS_SERVER_PROTOCOL");	        	
	        CA_SERVER_IP  = prop.getProperty("CA_SERVER_IP");
	        CA_PROTOCOL = prop.getProperty("CA_PROTOCOL");
	        CA_HTTP_PORT = prop.getProperty("CA_HTTP_PORT");
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	public static List<QualityParameterDTO> GenerateDefaultQoS(EnumQualityGenStrategy valueGenerationStrategy){
		var qos = new ArrayList<QualityParameterDTO>();
		
		switch (valueGenerationStrategy) {
			case BEST_VALUES:
				qos.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(100), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
				qos.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(0), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(0), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(0), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(0), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("Throughput", "Integer", String.valueOf(10), EnumQualityType.DEVICE.name(), "Throughput > 5"));
				qos.add(new QualityParameterDTO("EnergyConsumption", "Integer", String.valueOf(1), EnumQualityType.DEVICE.name(), "EnergyConsumption < 3"));
				qos.add(new QualityParameterDTO("PayloadCapacity", "Integer", String.valueOf(10), EnumQualityType.DEVICE.name(), "PayloadCapacity > 10"));
				qos.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(0), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
				qos.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(15), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
				qos.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(0), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
				qos.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(0), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
				qos.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(100), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
				qos.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(0), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));
				break;
			case RANDOM_VALUES:
				qos.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
				qos.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("Throughput", "Integer", String.valueOf(Tools.GetRandomNumber(1, 10)), EnumQualityType.DEVICE.name(), "Throughput > 5"));
				qos.add(new QualityParameterDTO("EnergyConsumption", "Integer", String.valueOf(Tools.GetRandomNumber(1, 5)), EnumQualityType.DEVICE.name(), "EnergyConsumption < 3"));
				qos.add(new QualityParameterDTO("PayloadCapacity", "Integer", String.valueOf(Tools.GetRandomNumber(1, 10)), EnumQualityType.DEVICE.name(), "PayloadCapacity > 10"));
				qos.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
				qos.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
				qos.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
				qos.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
				qos.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
				qos.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 1000)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));
				break;
			case WORST_VALUES:
				qos.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(0), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
				qos.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(100000), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(100000), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
				qos.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(1000), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(1000), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
				qos.add(new QualityParameterDTO("Throughput", "Integer", String.valueOf(0), EnumQualityType.DEVICE.name(), "Throughput > 5"));
				qos.add(new QualityParameterDTO("EnergyConsumption", "Integer", String.valueOf(5), EnumQualityType.DEVICE.name(), "EnergyConsumption < 3"));
				qos.add(new QualityParameterDTO("PayloadCapacity", "Integer", String.valueOf(1), EnumQualityType.DEVICE.name(), "PayloadCapacity > 10"));
				qos.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(100), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
				qos.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(50), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
				qos.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(100), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
				qos.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(100), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
				qos.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(0), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
				qos.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(100), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));

				break;
			default:
				break;
		}
		return qos;
	}
}

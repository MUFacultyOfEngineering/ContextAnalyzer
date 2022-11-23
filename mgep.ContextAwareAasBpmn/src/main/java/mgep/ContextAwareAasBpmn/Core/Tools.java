package mgep.ContextAwareAasBpmn.Core;

public final class Tools {
	public static final String GRAPHDB_SERVER = "http://localhost:7200/";
	public static final String REPOSITORY_ID = "DeviceServiceOnt_Exp01";
	public static final String DEVICE_SERVICE_ONT_IRI = "https://www.mondragon.edu/ontologies/2022/9/DeviceServiceOnt#";
	public static final String RDF_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	/**
	 * Get a random number given min and max values
	 * @param min
	 * @param max
	 * @return a random int
	 */
	public static int GetRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
}

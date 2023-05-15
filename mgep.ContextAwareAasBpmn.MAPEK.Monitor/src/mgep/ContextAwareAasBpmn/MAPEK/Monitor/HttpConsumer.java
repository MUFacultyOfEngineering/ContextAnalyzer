package mgep.ContextAwareAasBpmn.MAPEK.Monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConsumer {
	private String protocol;
	private String server;
	private int port;
	private String endpoint;
	private String methodType;
	private URL url;
	private HttpURLConnection client;
	
	public HttpConsumer(String protocol, String server, int port, String endpoint, String methodType) {
		this.protocol = protocol;
		this.server = server;
		this.port = port;
		this.endpoint = endpoint;
		this.methodType = methodType;
	}
	
	public String GetValue() {
		try {
			url = new URL(this.protocol + "://" + this.server + ":" + this.port + "/" + this.endpoint);
			client = (HttpURLConnection) url.openConnection();
			
			
			client.setRequestMethod(methodType);
			var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine;
			var response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			//client.disconnect();
		}
	}
}

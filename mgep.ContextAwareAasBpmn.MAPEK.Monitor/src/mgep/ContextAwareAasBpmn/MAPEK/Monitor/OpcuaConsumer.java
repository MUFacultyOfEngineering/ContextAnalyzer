package mgep.ContextAwareAasBpmn.MAPEK.Monitor;


import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

public class OpcuaConsumer {
	private String protocol;
	private String server;
	private int port;
	private String opcuaServerEndpoint;
	private OpcUaClient client;
	
	public OpcuaConsumer(String protocol, String server, int port) {
		this.protocol = protocol;
		this.server = server;
		this.port = port;
	}
	
	private void openConnection() {
		try {
			opcuaServerEndpoint = protocol + "://" + server + ":" + port;
			
			client = OpcUaClient.create(
					opcuaServerEndpoint,
					endpoints ->
					endpoints.stream()
					.filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
					.findFirst(),
					configBuilder ->
					configBuilder.build()
					);
			client.connect().get();
		} catch (UaException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			closeConnection();
		}
	}
	
	private void closeConnection() {
		try {
			client.disconnect().get();
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
	}
	
	public String ReadOpcuaValue(int nameSpaceIndex, String identifier) {
		DataValue dataValue = null;
		
		try {
			// Connect to the OPC UA server
			openConnection();
			
			// Read data from an OPC UA node
			dataValue = client.readValue(0, TimestampsToReturn.Both, NodeId.parse(String.format("ns=%s;s=%s", nameSpaceIndex, identifier))).get();
		} catch (UaRuntimeException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}finally {
			closeConnection();
		}
		
		if(dataValue.getValue() == null) return "";
		if(dataValue.getValue().getValue() == null) return "";
		
		return dataValue.getValue().getValue().toString();
	}
}

package mgep.ContextAwareAasBpmn.MAPEK.Monitor;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.rail.jrosbridge.*;
import edu.wpi.rail.jrosbridge.callback.*;
import edu.wpi.rail.jrosbridge.messages.Message;

public class RosConsumer {
	private static String server;
	private static int port;
	private static Ros client;
	private static Map<String,String> mapLatestValuesTopics;
	
	private static void openConnection() {		
		if(RosConsumer.client == null) RosConsumer.client = new Ros(RosConsumer.server, RosConsumer.port);
		if(!RosConsumer.client.isConnected()) RosConsumer.client.connect();
	}
	
	private static void closeConnection() {
		if(RosConsumer.client == null) return;
		if(RosConsumer.client.isConnected()) RosConsumer.client.disconnect();
	}
	
	private static void subscribeRosTopic(String topicName, String messageType) {
		try {
			// Connect to the ROS server
			RosConsumer.openConnection();
			
			// Read data from a ROS topic
			var topicObj = new Topic(RosConsumer.client, topicName, messageType, 1000);
			topicObj.subscribe(new TopicCallback() {
				
				@Override
				public void handleMessage(Message message) {					
					var objectMapper = new ObjectMapper();
					JsonNode jsonNodeObj = null;
					try {
						jsonNodeObj = objectMapper.readTree(message.toString());
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					var value = jsonNodeObj.get("data").toString();
					RosConsumer.mapLatestValuesTopics.put(topicName, value);
				}
			});
		} catch(Exception ex) {
			System.out.println(ex.toString());
		} finally {
			//if(this.client != null && this.client.isConnected()) closeConnection();
		}
	}
	
	/**
	 * Makes a subscription to the ROS topic (one time) and gets the latest value
	 * @param topicName
	 * @return
	 */
	public static String GetLatestValue(String server, int port, String topicName, String messageType) {
		RosConsumer.server = server;
		RosConsumer.port = port;
		
		//eval if theres an active subscription to the provided topic
		if (RosConsumer.mapLatestValuesTopics == null || !RosConsumer.mapLatestValuesTopics.containsKey(topicName)) {
			//subscribe
			RosConsumer.mapLatestValuesTopics = new HashMap<String, String>();
			
			if(messageType == null || messageType.isEmpty()) messageType = "std_msgs/String";
			
			RosConsumer.subscribeRosTopic(topicName, messageType);
		}
		
		//wait some seconds
		var i = 0;
		//while((RosConsumer.mapLatestValuesTopics == null || !RosConsumer.mapLatestValuesTopics.containsKey(topicName)) && i < 5) {
		while(i < 5) {
			try {
				//System.out.println(String.format("waiting for topic message %s", topicName));
				i++;
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		var value = RosConsumer.mapLatestValuesTopics.get(topicName);		
		System.out.println(String.format("%s %s", topicName, value));
		return value;
	}
}

package mgep.ContextAwareAasBpmn.Test.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.Enums.*;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.*;
import mgep.ContextAwareAasBpmn.Core.*;

public class TestIsaacSim {
	private static final String ROS_SERVER_IP = "34.116.215.235";
	private static final String ROS_SERVER_HTTP_PORT = "5000";
	private static final String ROS_SERVER_PROTOCOL = "http";
	
	private static final String NODEREDWM_SERVER_IP = "localhost";
	private static final String NODEREDWM_HTTP_PORT = "1880";
	private static final String NODEREDWM_PROTOCOL = "http";
	
	private static final String CA_SERVER_IP  = "localhost";
	private static final String CA_PROTOCOL = "http";
	private static final String CA_HTTP_PORT = "8080";
	private static final int MS_SLEEP_GATHER_QOS_VALS = 20000;
	
	private static final double[][] INIT_ROBOTS_POS = {
		    {7.0, 6.0},
		    {7.0, 3.0},
		    {7.0, 0.0},
		    {7.0, -3.0},
		    {7.0, -6.0},
		    {5.0, -10.0},
		    {2.0, -10.0},
		    {-1.0, -10.0},
		    {-4.0, -10.0},
		    {-7.0, -10.0}
		};
	
	private static double[][] CURRENT_ROBOTS_POS = {
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0},
		    {0.0, 0.0}
		};

	private static int[] CURRENT_ROBOTS_BATTERIES = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private static double[] CURRENT_ROBOTS_PROXIMITY_PICKUP = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private static double[] CURRENT_ROBOTS_POSITIONAL_UCR = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private static double[] CURRENT_ROBOTS_PAYLOAD_CAPACITY = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	
	private static String CA_SELECTED_ASS_ID_SHORT = "";
	private static String RANDOM_SELECTED_ASS_ID_SHORT = "";

	
	//prior executing this script, Node-RED WM, Context Analizer (CA), and the simulation in Nvidia Isaac Sim should be running

	public static void main(String[] args) throws InterruptedException {
		//load config file
		//String filePath = SynchronizeThisDeviceData.class.getClassLoader().getResource("config.properties").getPath();
		//Tools.LoadEnvironmentFromPropertiesFile(filePath);
		
		int epoch = 40;
		int maxEpochs = 50;
		int qtyShells = 5;
		
		TestIsaacSim simulation = new TestIsaacSim();
		
		List<String> lParamsToMeasure = new ArrayList<String>();
		lParamsToMeasure.add("PROXIMITY_PICKUP");
		lParamsToMeasure.add("BATTERY");
		lParamsToMeasure.add("POSITIONAL_UNCERTAINTY");
		lParamsToMeasure.add("PAYLOAD_CAPACITY");
		//lParamsToMeasure.add("AvgNetworkLatency");
		
		//the order of these conditions will determine priority
		List<String> lQoSConditions = new ArrayList<String>();
		lQoSConditions.add("PROXIMITY_PICKUP <= 20.0");
		lQoSConditions.add("BATTERY >= 25");
		lQoSConditions.add("POSITIONAL_UNCERTAINTY <= 0.90");
		lQoSConditions.add("PAYLOAD_CAPACITY >= 0.6");
		//lQoSConditions.add("AvgNetworkLatency <= 100");
		
		System.out.println(String.format("Shells: %s Quality conditions: %s", qtyShells, String.join(" && ", lQoSConditions)));

		while(epoch <= maxEpochs) {
			System.out.println(String.format("Epoch: %s. Initiating simulation...", epoch));
			
			//Move all robots to initial position
			simulation.MoveAllInitialPosition();
			simulation.MakeSureAllRobotsHaveAchievedMovingToGoals("INITIAL_POS", null);
			
			//Move all robots to random position
			simulation.MoveAllRandomPosition();
			simulation.MakeSureAllRobotsHaveAchievedMovingToGoals("RANDOM_POS", null);				
			
			//Reset Batteries and payload capacity
			simulation.ResetBatteriesAllRobots();
			simulation.RandomizeMaxPayloadCapacityAllRobots();
			
			//sleep so that Context Monitor can gather new quality values
			Thread.sleep(MS_SLEEP_GATHER_QOS_VALS);
			
			//Get and save current position of all robots and as well as quality properties
			simulation.GetAndSaveCurrentRobotsPositionsAndQoS();
			
			//Start task. CA device selection
			simulation.StartTaskExecution("CA", qtyShells, lParamsToMeasure, lQoSConditions);
			
			//Move the robots again to previous positions and set back their previous quality properties
			simulation.SetAndMoveRobotsPositionsAndQoS();
			
			//sleep so that Context Monitor can gather new quality values
			Thread.sleep(MS_SLEEP_GATHER_QOS_VALS);
			
			//restore batteries again, just in case
			simulation.SetBatteriesAllRobots(CURRENT_ROBOTS_BATTERIES);
			
			//Start task. RANDOM device selection, except the one CA have just selected
			simulation.StartTaskExecution("RANDOM", qtyShells, lParamsToMeasure, null);

				
			epoch++;
		}
	}
	
	private void GetAndSaveCurrentRobotsPositionsAndQoS() {
		JsonNode jsonCurrentRobotsPositions = GetAllRobotsCurrentPosition();
		JsonNode jsonCurrentRobotsBatteries = GetAllRobotsCurrentBatteries();
		JsonNode jsonCurrentRobotsPayloadCapacities = GetAllRobotsCurrentPayloadCapacity();
		JsonNode jsonCurrentRobotsPurs = GetAllRobotsCurrentPurs();
		JsonNode jsonCurrentRobotsPpul = GetAllRobotsCurrentPpul();
		
		for (int i = 0; i < jsonCurrentRobotsPositions.size(); i++) {
			CURRENT_ROBOTS_POS[i][0] = jsonCurrentRobotsPositions.get(i).get("x").asDouble();
			CURRENT_ROBOTS_POS[i][1] = jsonCurrentRobotsPositions.get(i).get("y").asDouble();
		}
		
		for (int i = 0; i < jsonCurrentRobotsBatteries.size(); i++) {
			CURRENT_ROBOTS_BATTERIES[i] = jsonCurrentRobotsBatteries.get(i).asInt();
		}
		
		for (int i = 0; i < jsonCurrentRobotsPayloadCapacities.size(); i++) {
			CURRENT_ROBOTS_PAYLOAD_CAPACITY[i] = jsonCurrentRobotsPayloadCapacities.get(i).asDouble();
		}
		
		for (int i = 0; i < jsonCurrentRobotsPurs.size(); i++) {
			CURRENT_ROBOTS_POSITIONAL_UCR[i] = jsonCurrentRobotsPurs.get(i).asDouble();
		}
		
		for (int i = 0; i < jsonCurrentRobotsPpul.size(); i++) {
			CURRENT_ROBOTS_PROXIMITY_PICKUP[i] = jsonCurrentRobotsPpul.get(i).asDouble();
		}
		
		//printout all
		System.out.println(String.format("CURRENT_ROBOTS_POS: %s", Arrays.deepToString(CURRENT_ROBOTS_POS)));
		System.out.println(String.format("CURRENT_ROBOTS_BATTERIES: %s", Arrays.toString(CURRENT_ROBOTS_BATTERIES)));
		System.out.println(String.format("CURRENT_ROBOTS_PAYLOAD_CAPACITY: %s", Arrays.toString(CURRENT_ROBOTS_PAYLOAD_CAPACITY)));
		System.out.println(String.format("CURRENT_ROBOTS_PROXIMITY_PICKUP: %s", Arrays.toString(CURRENT_ROBOTS_PROXIMITY_PICKUP)));
		System.out.println(String.format("CURRENT_ROBOTS_POSITIONAL_UCR: %s", Arrays.toString(CURRENT_ROBOTS_POSITIONAL_UCR)));
	}
	
	private void SetAndMoveRobotsPositionsAndQoS() {
		MoveAllToPosition(CURRENT_ROBOTS_POS);
		MakeSureAllRobotsHaveAchievedMovingToGoals("SPECIFIC_POS", CURRENT_ROBOTS_POS);		
		SetAllRobotsPayloadCapacity(CURRENT_ROBOTS_PAYLOAD_CAPACITY);
		SetAllRobotsPurs(CURRENT_ROBOTS_POSITIONAL_UCR);
		SetAllRobotsPpul(CURRENT_ROBOTS_PROXIMITY_PICKUP);
		SetBatteriesAllRobots(CURRENT_ROBOTS_BATTERIES);
		System.out.println("Previous quality properties have been restored to all robots");
	}
	
	private void StartTaskExecution(String robotSelectionMode, int qtyShells, List<String> lParamsToMeasure, List<String> lQoSConditions) {
		TestIsaacSim simulation = new TestIsaacSim();
		
		//Call CA for best service selection
		String serviceName = "PickAndDeliverPackage";
		JsonNode resultServiceSelection = null;
		
		if(robotSelectionMode == "CA") {
			JsonNode jsonNodeSuggestedService = simulation.SelectBestService("AASCarter1", serviceName, lQoSConditions, lParamsToMeasure);
			System.out.println(String.format("CA API Message: %s", jsonNodeSuggestedService.path("message")));
			resultServiceSelection = jsonNodeSuggestedService.path("suggestedService");
			CA_SELECTED_ASS_ID_SHORT = resultServiceSelection.path("aasIdShort").asText();
			
		}else if(robotSelectionMode == "RANDOM") {
			resultServiceSelection = simulation.SelectRandomService(qtyShells, serviceName);
			RANDOM_SELECTED_ASS_ID_SHORT = resultServiceSelection.path("aasIdShort").asText();
		}
		
		//Log the selected service and its quality properties
		String printMsg = simulation.stringifyJsonNodeService(resultServiceSelection, lParamsToMeasure);
		System.out.println(String.format("%s. %s", robotSelectionMode, printMsg));
		
		//Invoke AAS service
		long startTime = System.nanoTime();
		simulation.InvokeAasService(resultServiceSelection);
		
		//Log the time taken to finish the task
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		double elapsedTimeMs = elapsedTime / 1_000_000.0;
		System.out.println(String.format("Task execution %s mode. Elapsed Time: %s ms", robotSelectionMode, elapsedTimeMs));
		
		//printout the current quality properties after performing the tasks
		//Call CA to get service quality props
		String aasIdentifier = resultServiceSelection.path("aasIdentifier").asText();
		JsonNode resultServiceWithUpdatedQos = simulation.SelectService(aasIdentifier, serviceName);

		//printout
		printMsg = simulation.stringifyJsonNodeService(resultServiceWithUpdatedQos, lParamsToMeasure);
		System.out.println(String.format("%s latest QoS. %s", robotSelectionMode, printMsg));
	}
		
	private JsonNode MoveAllInitialPosition() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveAllInitialPosition", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode MoveInitialPosition(int robotId) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveInitialPosition/%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private JsonNode MoveToPosition(int robotId, double x, double y) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/move_async/%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestProperty("Content-Type", "application/json");
			httpClient.setDoOutput(true);			
			httpClient.setRequestMethod("POST");
			
			OutputStream wr = httpClient.getOutputStream();
		
			String requestBody= "{"
					+ "    \"x\": \"" + x + "\",\r\n"
					+ "    \"y\": \"" + y + "\"\r\n"
					+ "}";
			wr.write(requestBody.getBytes());
			wr.flush();			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private List<JsonNode> MoveAllToPosition(double[][] robots) {
		List<JsonNode> results = new ArrayList<JsonNode>();
		
		for (int i = 0; i < robots.length; i++) {
			results.add(MoveToPosition(i + 1, robots[i][0], robots[i][1]));			
		}
		
		return results;
	}
	
	private JsonNode MoveAllRandomPosition() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveAllRandomPosition", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode MoveRandomPosition(int robotId) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveRandomPosition/%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode MoveToPositionRecoveryStrategy(int robotId, String moveGoal, double specificGoal[]) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveToPositionRecoveryStrategy?robot_id=%s&destination=%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId, moveGoal));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestMethod("POST");
			
			if(moveGoal == "SPECIFIC_POS" && specificGoal != null) {
				httpClient.setRequestProperty("Content-Type", "application/json");
				httpClient.setDoOutput(true);
				
				OutputStream wr = httpClient.getOutputStream();
			
				String requestBody= "{"
						+ "    \"x\": \"" + specificGoal[0] + "\",\r\n"
						+ "    \"y\": \"" + specificGoal[1] + "\"\r\n"
						+ "}";
				wr.write(requestBody.getBytes());
				wr.flush();		
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetStateAll() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_state_all_robots", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetState(int robotId) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_state/%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetAllRobotsCurrentPosition() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_all_robots_current_position", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetAllRobotsCurrentBatteries() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_all_robots_current_batteries", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode ResetBatteriesAllRobots() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/ResetBatteriesAllRobots", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}

	
	private JsonNode SetBatteriesAllRobots(int batteries[]) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/set_all_robots_batteries", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestProperty("Content-Type", "application/json");
			httpClient.setDoOutput(true);
			httpClient.setRequestMethod("POST");
			
			OutputStream wr = httpClient.getOutputStream();		
			String requestBody = "\"" + String.join(", ", Arrays.toString(batteries)) + "\"";

			wr.write(requestBody.getBytes());
			wr.flush();			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetAllRobotsCurrentPayloadCapacity() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_all_robots_current_payload_capacity", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private JsonNode SetAllRobotsPayloadCapacity(double payloadCapacities[]) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/set_all_robots_payload_capacities", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestProperty("Content-Type", "application/json");
			httpClient.setDoOutput(true);			
			httpClient.setRequestMethod("POST");
			
			OutputStream wr = httpClient.getOutputStream();		
			String requestBody = "\"" + String.join(", ", Arrays.toString(payloadCapacities)) + "\"";

			wr.write(requestBody.getBytes());
			wr.flush();			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetAllRobotsCurrentPurs() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_all_robots_current_purs", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private JsonNode SetAllRobotsPurs(double purs[]) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/set_all_robots_purs", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestProperty("Content-Type", "application/json");
			httpClient.setDoOutput(true);			
			httpClient.setRequestMethod("POST");
			
			OutputStream wr = httpClient.getOutputStream();		
			String requestBody = "\"" + String.join(", ", Arrays.toString(purs)) + "\"";

			wr.write(requestBody.getBytes());
			wr.flush();			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode GetAllRobotsCurrentPpul() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/get_all_robots_current_ppul", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private JsonNode SetAllRobotsPpul(double ppul[]) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/set_all_robots_ppul", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();
			httpClient.setRequestProperty("Content-Type", "application/json");
			httpClient.setDoOutput(true);			
			httpClient.setRequestMethod("POST");
			
			OutputStream wr = httpClient.getOutputStream();		
			String requestBody = "\"" + String.join(", ", Arrays.toString(ppul)) + "\"";

			wr.write(requestBody.getBytes());
			wr.flush();			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	private JsonNode RandomizeMaxPayloadCapacityAllRobots() {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/RandomizeMaxPayloadCapacityAllRobots", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
		JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private boolean MakeSureAllRobotsHaveAchievedMovingToGoals(String moveGoal, double specificGoals[][]) {
		boolean allRobotsSucceeded = false;
		long startTime = System.nanoTime();

		while (!allRobotsSucceeded) {
			//1: get state all robots (after moving) all codes should be 3: SUCCEEDED
			JsonNode stateAllRobots = GetStateAll();
			List<Integer> lStatus = new ArrayList<Integer>();
			
			for (JsonNode robotStatusNode : stateAllRobots) {
				int code = robotStatusNode.get("code").asInt();
				lStatus.add(code);
			}
		
			//if all robots have succeeded, just exit the loop and return true
			allRobotsSucceeded = lStatus.stream().allMatch(x -> x == 3);
			if (allRobotsSucceeded) break;
			
			//2: loop through all those which have not succeeded
			List<Thread> lThreads = new ArrayList<Thread>();
			for (int i = 0; i < lStatus.size(); i++) {
				int code = lStatus.get(i);
				final int robotId = i + 1;
				
				if (code != 3) { 
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								double[] specificGoal = {0, 0};
								if(moveGoal == "SPECIFIC_POS")
									specificGoal = specificGoals[robotId - 1];
								else
									specificGoal = null;
								
								MakeSureRobotHasAchievedMovingToGoal(robotId, moveGoal, specificGoal);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					t.start();
					lThreads.add(t);
				}				
			}
			
			//3: wait for all threads to finish
			for (Thread t : lThreads) {
				try {
	                t.join();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
			}
		}
		
		//Log the time taken to finish the task
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		double elapsedTimeMs = elapsedTime / 1_000_000.0;		
		System.out.println(String.format("All robots have achieved their goal %s. Elapsed Time: %s ms", moveGoal, elapsedTimeMs));
		
		return allRobotsSucceeded;
	}
	
	private boolean MakeSureRobotHasAchievedMovingToGoal(int robotId, String moveGoal, double[] specificGoal) throws InterruptedException {
		boolean robotSucceeded = false;
		
		while (!robotSucceeded) {
			//1: get state code should be 3: SUCCEEDED
			JsonNode jsonStateRobot = GetState(robotId);
			int code = jsonStateRobot.get("code").asInt();
		
			//if robot has succeeded, just exit the loop and return true
			if (code == 3) {
				System.out.println(String.format("Robot %s has reached the goal %s", robotId, moveGoal));
				return true;
			}
			
			//2: analyze state
			switch (code) {
				case 0:
				case 1:
				case 2:
					//0: PENDING The goal has yet to be processed by the action server
					//1: ACTIVE The goal is currently being processed by the action server
					//wait some seconds for it to finish
					//System.out.println(String.format("Robot %s is moving. Status: %s Goal: %s", robotId, code, moveGoal));
					Thread.sleep(1000);
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					//4: ABORTED The goal was aborted during execution by the action server due to some failure (Terminal State)
					//retry move goal
					List<String> arGoals = new ArrayList<String>();
					arGoals.add("INITIAL_POS");
					arGoals.add("PICKUP_POS");
					arGoals.add("DELIVERY_POS");
					arGoals.add("SPECIFIC_POS");
					
					System.out.println(String.format("Robot %s has aborted moving. Status: %s Goal: %s. Applying recovery strategy...", robotId, code, moveGoal));
					if (arGoals.contains(moveGoal)) {
						MoveToPositionRecoveryStrategy(robotId, moveGoal, specificGoal);
					} else if (moveGoal == "RANDOM_POS")
						MoveRandomPosition(robotId);
					
					Thread.sleep(10000);
				default:
					robotSucceeded = true;
					break;
			}
		}
		
		return robotSucceeded;
	}

	private JsonNode NodeRedWMStartProcess(String processName, int delayTasExecution, boolean shouldValidateContext) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/start_process?processname=%s&delayTaskExecution=%s&validateContext=%s", NODEREDWM_PROTOCOL, NODEREDWM_SERVER_IP, NODEREDWM_HTTP_PORT, processName, delayTasExecution, shouldValidateContext));
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod("POST");
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
	
	
	private JsonNode SelectRandomService(int qtyShells, String serviceName) {
		//Random Selection of a service
		int caSelectedShellInt = Integer.valueOf(CA_SELECTED_ASS_ID_SHORT.replace("AASCarter", ""));
		int randomShellInt = caSelectedShellInt;		
		
		while (randomShellInt == caSelectedShellInt) {
			randomShellInt = Tools.GetRandomNumber(1, qtyShells);
		}
		
		String randomAssetIdentifier = "";
		if(randomShellInt == 10)
			randomAssetIdentifier = "https://mondragon.com/ids/asset/4274_9012_3032_4650";
		else
			randomAssetIdentifier = String.format("https://mondragon.com/ids/asset/4274_9012_3032_464%s", randomShellInt);
		
		//invoke API to get this random service
		StringBuilder responseRandomService = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/ContextAwareAasBpmn/api/ContextAnalyzer/GetServiceByName?aasIdentifier=%s&serviceName=%s", CA_PROTOCOL, CA_SERVER_IP, CA_HTTP_PORT, randomAssetIdentifier, serviceName));
			HttpURLConnection client = (HttpURLConnection) url.openConnection();					
			
			client.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				responseRandomService.append(inputLine);
			}
			in.close();
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode responseNodeRandomService = null;
		try {
			responseNodeRandomService = new ObjectMapper().readTree(responseRandomService.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return responseNodeRandomService;
	}
	
	private JsonNode SelectBestService(String assetIdShort, String serviceName, List<String> lQoSConditions, List<String> lParamsToMeasure) {
		//use context analyzer for best service selection
		StringBuilder responseContextBestService = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/ContextAwareAasBpmn/api/ContextAnalyzer/ValidateContextSelectBestService", CA_PROTOCOL, CA_SERVER_IP, CA_HTTP_PORT));
			HttpURLConnection client = (HttpURLConnection) url.openConnection();
			client.setRequestProperty("Content-Type", "application/json");
			client.setDoOutput(true);
			
			client.setRequestMethod("POST");
			OutputStream wr = client.getOutputStream();
			
			String qpee = "";
			for (int i = 0; i < lQoSConditions.size(); i++) {
				qpee += "{\r\n"
						+ "\"qualityParameterEvaluationExpression\": \"" + lQoSConditions.get(i) + "\"\r\n"
						+ "},";
			}
			qpee = qpee.substring(0, qpee.length()-1);
			
			String requestBody= "{"
					+ "    \"aasIdShort\": \""+ assetIdShort +"\",\r\n"
					+ "    \"serviceName\": \""+ serviceName +"\",\r\n"
					+ "    \"qualityParameters\": "
					+ "[\r\n"
					+ qpee
					+ "] \r\n"
					+ "}";
			wr.write(requestBody.getBytes());
			wr.flush();					
			
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				responseContextBestService.append(inputLine);
			}
			in.close();
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Parse the JSON response
        JsonNode responseNodeContextBestService = null;
		try {
			responseNodeContextBestService = new ObjectMapper().readTree(responseContextBestService.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return responseNodeContextBestService;
	}
	
	
	private String stringifyJsonNodeService(JsonNode jsonServiceObj, List<String> lParamsToMeasure) {
		//Extract the values of aasIdentifier from jsonServiceObj
        String aasIdShort = jsonServiceObj.path("aasIdShort").asText();

        //Extract the values from parameterValue
        JsonNode jsonNodeQualityParametersService = jsonServiceObj.path("serviceQualityParameters");
        StringBuilder sbParameterValuesService = new StringBuilder();
        for (JsonNode jsonNodeParameter : jsonNodeQualityParametersService) {
            String parameterName = jsonNodeParameter.path("parameterName").asText();
            if (lParamsToMeasure.contains(parameterName)) {
                String parameterValue = jsonNodeParameter.path("parameterValue").asText();
                sbParameterValuesService.append(String.format("%s: %s", parameterName, parameterValue)).append(", ");
            }
        }
		
		return String.format("Device: %s QoS Properties: %s", aasIdShort, sbParameterValuesService.toString());
	}

	private JsonNode SelectService(String aasIdentifier, String serviceName) {
		//use context analyzer for best service selection
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/ContextAwareAasBpmn/api/ContextAnalyzer/GetServiceByName?aasIdentifier=%s&serviceName=%s", CA_PROTOCOL, CA_SERVER_IP, CA_HTTP_PORT, aasIdentifier, serviceName));
			HttpURLConnection client = (HttpURLConnection) url.openConnection();
			
			client.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Parse the JSON response
        JsonNode responseJsonNode = null;
		try {
			responseJsonNode = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return responseJsonNode;
	}
	
	
	private JsonNode InvokeAasService(JsonNode jsonNodeServiceObj) {
		StringBuilder response = new StringBuilder();
		try {
			String serviceUrl = jsonNodeServiceObj.path("serviceUrl").asText();
			String serviceMethod = jsonNodeServiceObj.path("serviceMethod").asText();
			
			URL url = new URL(serviceUrl);
			HttpURLConnection httpClient = (HttpURLConnection) url.openConnection();					
			
			httpClient.setRequestMethod(serviceMethod);
			BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			httpClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		// Parse the JSON response
        JsonNode jsonResponse = null;
		try {
			jsonResponse = new ObjectMapper().readTree(response.toString());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonResponse;
	}
}
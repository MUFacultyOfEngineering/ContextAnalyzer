package mgep.ContextAwareAasBpmn.Test.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
	
	//prior executing this script, Node-RED WM, Context Analizer (CA), and the simulation in Nvidia Isaac Sim should be running

	public static void main(String[] args) {
		//load config file
		//String filePath = SynchronizeThisDeviceData.class.getClassLoader().getResource("config.properties").getPath();
		//Tools.LoadEnvironmentFromPropertiesFile(filePath);
		
		int epoch = 8;
		int maxEpochs = 100;
		int qtyShells = 10;
		TestIsaacSim simulation = new TestIsaacSim();
		
		List<String> lParamsToMeasure = new ArrayList<String>();
		lParamsToMeasure.add("PROXIMITY_PICKUP");
		lParamsToMeasure.add("BATTERY");
		lParamsToMeasure.add("POSITIONAL_UNCERTAINTY");
		lParamsToMeasure.add("PAYLOAD_CAPACITY");
		//lParamsToMeasure.add("AvgNetworkLatency");
		
		List<String> lQoSConditions = new ArrayList<String>();
		lQoSConditions.add("PROXIMITY_PICKUP <= 20.0");
		lQoSConditions.add("BATTERY >= 25");
		lQoSConditions.add("POSITIONAL_UNCERTAINTY <= 0.90");
		lQoSConditions.add("PAYLOAD_CAPACITY >= 0.6");
		//lQoSConditions.add("AvgNetworkLatency <= 100");

		while(epoch <= maxEpochs) {
				System.out.println("Epoch: " + epoch + " # Quality conditions: " + lParamsToMeasure.size() + " Initiating simulation...");
				
				//Move all robots to initial position
				simulation.MoveAllInitialPosition();
				simulation.MakeSureAllRobotsHaveAchievedMovingToGoals("INITIAL_POS");
				
				//Move all robots to random position
				simulation.MoveAllRandomPosition();
				simulation.MakeSureAllRobotsHaveAchievedMovingToGoals("RANDOM_POS");
				
				//Reset Batteries and payload capacity
				simulation.ResetBatteriesAllRobots();
				simulation.RandomizeMaxPayloadCapacityAllRobots();
				
				//sleep so that Context Monitor can gather new quality values
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//Call CA for best service selection
				String serviceName = "PickAndDeliverPackage";
				JsonNode resultBestServiceSelection = simulation.SelectBestService("AASCarter1", serviceName, lQoSConditions, lParamsToMeasure);
				
				//Log the reselected service and its quality properties
				simulation.printoutJsonNodeBestService(resultBestServiceSelection, lParamsToMeasure);
				
				//Invoke AAS service
				long startTime = System.nanoTime();
				simulation.InvokeAasService(resultBestServiceSelection);
				
				//Log the time taken to finish the task
				long endTime = System.nanoTime();
				long elapsedTime = endTime - startTime;
				double elapsedTimeMs = elapsedTime / 1_000_000.0;
				System.out.println("Elapsed Time: " + elapsedTimeMs + " ms");
				
				//printout the current quality properties after performing the tasks
				//Call CA to get service quality props
				String aasIdentifierContextBestService = resultBestServiceSelection.path("suggestedService").path("aasIdentifier").asText();
				JsonNode resultService = simulation.SelectService(aasIdentifierContextBestService, serviceName);

				//printout
				String printMsg = simulation.stringifyJsonNodeService(resultService, lParamsToMeasure);
				System.out.println(String.format("CA Best latest QoS. %s", printMsg));
				
			epoch++;
		}
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
	
	private JsonNode MoveToPositionRecoveryStrategy(int robotId, String moveGoal) {
		StringBuilder response = new StringBuilder();
		try {
			URL url = new URL(String.format("%s://%s:%s/MoveToPositionRecoveryStrategy?robot_id=%s&destination=%s", ROS_SERVER_PROTOCOL, ROS_SERVER_IP, ROS_SERVER_HTTP_PORT, robotId, moveGoal));
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
	
	private boolean MakeSureAllRobotsHaveAchievedMovingToGoals(String moveGoal) {
		boolean allRobotsSucceeded = false;
		
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
			if (allRobotsSucceeded) {
				System.out.println(String.format("All robots have achieved their goal %s", moveGoal));
				return true;
			}
			
			//2: loop through all those which have not succeeded
			List<Thread> lThreads = new ArrayList<Thread>();
			for (int i = 0; i < lStatus.size(); i++) {
				int code = lStatus.get(i);
				final int robotId = i + 1;
				
				if (code != 3) { 
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								MakeSureRobotHasAchievedMovingToGoal(robotId, moveGoal);
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
		
		return allRobotsSucceeded;
	}
	
	private boolean MakeSureRobotHasAchievedMovingToGoal(int robotId, String moveGoal) throws InterruptedException {
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
					
					System.out.println(String.format("Robot %s has aborted moving. Status: %s Goal: %s. Applying recovery strategy...", robotId, code, moveGoal));
					if (arGoals.contains(moveGoal)) {
						MoveToPositionRecoveryStrategy(robotId, moveGoal);
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
		int randomShellInt = Tools.GetRandomNumber(1, qtyShells);
		String randomAssetIdentifier = String.format("AssetAdministrationShell---%s", randomShellInt);
		String randomAssetIdShort = String.format("AAS%s", randomShellInt);
		
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
	
	private void printoutJsonNodeRandomService(JsonNode jsonServiceObj, List<String> lParamsToMeasure) {
		String aasIdentifier = jsonServiceObj.path("aasIdentifier").asText();
		
        //Extract the values of parameterValue
        JsonNode qualityParametersNode = jsonServiceObj.path("serviceQualityParameters");
        StringBuilder parameterValuesRandomService = new StringBuilder();
        for (JsonNode parameterNodeRandomService : qualityParametersNode) {
            String parameterName = parameterNodeRandomService.path("parameterName").asText();
            if (lParamsToMeasure.contains(parameterName)) {
                String parameterValue = parameterNodeRandomService.path("parameterValue").asText();
                parameterValuesRandomService.append(String.format("%s: %s", parameterName, parameterValue)).append(", ");
            }
        }
		
		System.out.println(String.format("Random Service: %s QoS Properties: %s", aasIdentifier, parameterValuesRandomService));
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
	
	private void printoutJsonNodeBestService(JsonNode jsonBestServiceObj, List<String> lParamsToMeasure) {
		//Extract the values of aasIdentifier from jsonServiceObj
		JsonNode jsonServiceObj = jsonBestServiceObj.path("suggestedService");

        //Extract the values from parameterValue and stringify
		String printMsg = stringifyJsonNodeService(jsonServiceObj, lParamsToMeasure);
		System.out.println(String.format("CA Best %s", printMsg));
	}
	
	private String stringifyJsonNodeService(JsonNode jsonServiceObj, List<String> lParamsToMeasure) {
		//Extract the values of aasIdentifier from jsonServiceObj
        String aasIdentifier = jsonServiceObj.path("aasIdentifier").asText();
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
			JsonNode jsonNodeSuggestedService = jsonNodeServiceObj.path("suggestedService");
			String serviceUrl = jsonNodeSuggestedService.path("serviceUrl").asText();
			String serviceMethod = jsonNodeSuggestedService.path("serviceMethod").asText();
			
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
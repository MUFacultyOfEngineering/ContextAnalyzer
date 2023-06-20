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

public class DataGenerationLegoColourSorter {

	public static void main(String[] args) {
		//load config file
		//String filePath = SynchronizeThisDeviceData.class.getClassLoader().getResource("config.properties").getPath();
		//Tools.LoadEnvironmentFromPropertiesFile(filePath);
		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		RDFDAL rdfDal = new RDFDAL();
		
		int blockSize = 200;
		int epoch = 1;
		int maxEpochs = 1000;
		int qtyShells = 200;
		
		List<String> lParamsToMeasure = new ArrayList<String>();
		lParamsToMeasure.add("BATTERY");
		lParamsToMeasure.add("PROXIMITY");
		lParamsToMeasure.add("EnergyConsumption");
		lParamsToMeasure.add("PayloadCapacity");
		lParamsToMeasure.add("AvgNetworkLatency");
		
		List<String> lQoSConditions = new ArrayList<String>();
		lQoSConditions.add("BATTERY >= 20");
		lQoSConditions.add("PROXIMITY <= 200");
		lQoSConditions.add("EnergyConsumption < 3");
		lParamsToMeasure.add("PayloadCapacity >= 2");
		lParamsToMeasure.add("AvgNetworkLatency <= 100");

		while(qtyShells <= 200) {
			while(epoch <= maxEpochs) {
				//delete all
				System.out.println("Deleting");
				String prepareDelete = "delete where {?s ?o ?p};";
				repManager.executeQuery(Tools.REPOSITORY_ID, prepareDelete);
				
				//initial ip
				int firstSubnet = 192;
				int secondSubnet = 168;
				int thirdSubnet = 1;
				int fourthSubnet = 1;
				String queryInsert = "";			
				
				try {			
					for (int i = 1; i <= qtyShells; i++) {				
						if(fourthSubnet > 255) {
							fourthSubnet = 1;
							thirdSubnet += 1;
						}
						
						if(thirdSubnet > 255) {
							thirdSubnet = 1;
							secondSubnet += 1;
						}
						
						if(secondSubnet > 255) {
							secondSubnet = 1;
							firstSubnet += 1;
						}
						
						String ip = String.format("%s.%s.%s.%s", firstSubnet, secondSubnet, thirdSubnet, fourthSubnet);
						queryInsert += insertColorSorters(rdfDal, i, ip);
						
						//if <= blockSize insert all at once
						//if more than blockSize insert in blocks of blockSize
						
						if(qtyShells <= blockSize && i == qtyShells) {
							boolean resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, queryInsert);
							System.out.println(String.format("Insert result: %s shells: %s of %s", resultInsertAllAtOnce, i, qtyShells));
							queryInsert = "";
						} else if(qtyShells > blockSize && i > 1 && (i % blockSize == 0 || i == qtyShells)) {
							boolean resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, queryInsert);
							//show progress
							Double progress = i * 100.0 / qtyShells;
							System.out.println(String.format("Insert result: %s shells: %s of %s Progress: %s%%", resultInsertAllAtOnce, i, qtyShells, Math.round(progress)));
							queryInsert = "";
						}
						
						fourthSubnet++;
					}
					
					System.out.println("Epoch " + epoch + ". Initiating context validation...");
					
					//Random Selection of a service
					int randomShellInt = Tools.GetRandomNumber(1, qtyShells);
					String randomAssetIdentifier = String.format("AssetAdministrationShell---%s", randomShellInt);
					String randomAssetIdShort = String.format("AASLegoColorSorter0%s", randomShellInt);
					String serviceName = "ThrowCurrentPiece";
					
					//invoke API to get this random service
					StringBuilder responseRandomService = new StringBuilder();
					try {
						URL url = new URL(String.format("http://localhost:8080/ContextAwareAasBpmn/api/ContextAnalyzer/GetServiceByName?aasIdentifier=%s&serviceName=%s", randomAssetIdentifier, serviceName));
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
			        JsonNode responseNodeRandomService = new ObjectMapper().readTree(responseRandomService.toString());
	
			        // Extract the values of parameterValue where parameterName is BATTERY and PROXIMITY
			        JsonNode qualityParametersNode = responseNodeRandomService.path("serviceQualityParameters");
			        StringBuilder parameterValuesRandomService = new StringBuilder();
			        for (JsonNode parameterNodeRandomService : qualityParametersNode) {
			            String parameterName = parameterNodeRandomService.path("parameterName").asText();
			            if (lParamsToMeasure.contains(parameterName)) {
			                String parameterValue = parameterNodeRandomService.path("parameterValue").asText();
			                parameterValuesRandomService.append(String.format("%s: %s", parameterName, parameterValue)).append(", ");
			            }
			        }
					
					System.out.println(String.format("Random Dispatch Service: %s QoS Parameters: %s", randomAssetIdentifier, parameterValuesRandomService));
					
					//now use context analyzer for best service selection
					StringBuilder responseContextBestService = new StringBuilder();
					try {
						URL url = new URL("http://localhost:8080/ContextAwareAasBpmn/api/ContextAnalyzer/ValidateContextSelectBestService");
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
								+ "    \"aasIdShort\": \""+ randomAssetIdShort +"\",\r\n"
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
			        JsonNode responseNodeContextBestService = new ObjectMapper().readTree(responseContextBestService.toString());
	
			        // Extract the values of aasIdentifier from suggestedService
			        String aasIdentifierContextBestService = responseNodeContextBestService.path("suggestedService").path("aasIdentifier").asText();
	
			        // Extract the values of parameterValue where parameterName is BATTERY and PROXIMITY
			        JsonNode qualityParametersNodeContextBestService = responseNodeContextBestService.path("suggestedService").path("serviceQualityParameters");
			        StringBuilder parameterValuesContextBestService = new StringBuilder();
			        for (JsonNode parameterNode : qualityParametersNodeContextBestService) {
			            String parameterName = parameterNode.path("parameterName").asText();
			            if (lParamsToMeasure.contains(parameterName)) {
			                String parameterValue = parameterNode.path("parameterValue").asText();
			                parameterValuesContextBestService.append(String.format("%s: %s", parameterName, parameterValue)).append(", ");
			            }
			        }
					
					System.out.println(String.format("Context Dispatch Best Service: %s QoS Parameters: %s", aasIdentifierContextBestService, parameterValuesContextBestService));
					
					
					//System.out.println("Waiting for next run");
					//Thread.sleep(20000);
				//} catch (InterruptedException e) {
				//	e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				epoch++;
			}
			qtyShells += 25;
		}
	}
	
	private static String insertColorSorters(RDFDAL rdfDal, int deviceId, String deviceIpAddress) {
		//prepare insert device data
		DeviceDTO deviceObj = new DeviceDTO();
		deviceObj.setAasIdentifier("AssetAdministrationShell---" + deviceId);
		deviceObj.setAasIdShort("AASLegoColorSorter0" + deviceId);
		deviceObj.setAasName("AASLegoColorSorter0" + deviceId);
		deviceObj.setDeviceApiDocumentation("http://" + deviceIpAddress + ":80/swagger");
		deviceObj.setDeviceDescription("A robot lego color sorter for the classification pieces by color.");
		deviceObj.setDeviceIdentifier(UUID.randomUUID().toString());
		deviceObj.setDeviceIPAddress(deviceIpAddress);
		deviceObj.setDeviceIsOnline(true);
		deviceObj.setDeviceName("Device_ColorSorter0" + deviceId);
		deviceObj.setDeviceNetworkLatency(String.valueOf(Tools.GetRandomNumber(10, 500)));
		
		//sensors
		List<SensorDTO> lSensors = new ArrayList<SensorDTO>();
		deviceObj.setSensors(lSensors);
		
		//battery sensor
		SensorDTO sensorBatteryObj = new SensorDTO();
		sensorBatteryObj.setSensorDescription("A battery sensor. values represent the remaining battery level from 0 to 100");
		sensorBatteryObj.setSensorIdentifier(UUID.randomUUID().toString());
		sensorBatteryObj.setSensorName("Sensor_Battery");
		sensorBatteryObj.setSensorType(EnumSensorType.BATTERY);
		sensorBatteryObj.setSensorValueDataType("Integer");
		sensorBatteryObj.setSensorValueDataValue(String.valueOf(Tools.GetRandomNumber(1, 100)));
		sensorBatteryObj.setSensorValueDataUnit("Percent");
		lSensors.add(sensorBatteryObj);
		
		//services
		List<ServiceDTO> lServices = new ArrayList<ServiceDTO>();
		deviceObj.setServices(lServices);
		
		//GetPieceColor
		ServiceDTO serviceGetPieceColor = new ServiceDTO();
		serviceGetPieceColor.setServiceIdentifier(UUID.randomUUID().toString());
		serviceGetPieceColor.setServiceUrl("http://" + deviceIpAddress + ":80/brickpi/sensor/color/value");
		serviceGetPieceColor.setServiceMethod(EnumServiceMethod.GET.name());
		serviceGetPieceColor.setServiceIsAsync(false);
		serviceGetPieceColor.setServiceName("GetPieceColor");
		serviceGetPieceColor.setServiceDescription("Gets current piece color");
		serviceGetPieceColor.setAasIdentifier(deviceObj.getAasIdentifier());
		
		//outputs
		List<ParameterDTO> opsServiceGetPieceColor = new ArrayList<ParameterDTO>();
		opsServiceGetPieceColor.add(new ParameterDTO("color", "text", "Yellow|Red|Blue|Green"));
		serviceGetPieceColor.setServiceOutputParameters(opsServiceGetPieceColor);

		//quality		
		serviceGetPieceColor.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceGetPieceColor);
		
		//motorStatus
		ServiceDTO serviceMotorStatus = new ServiceDTO();
		serviceMotorStatus.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMotorStatus.setServiceUrl("http://" + deviceIpAddress + ":80/brickpi/motor/{motor_id}/status");
		serviceMotorStatus.setServiceMethod(EnumServiceMethod.GET.name());
		serviceMotorStatus.setServiceIsAsync(true);
		serviceMotorStatus.setServiceName("GetMotorStatus");
		serviceMotorStatus.setServiceDescription("Gets motor status");
		serviceMotorStatus.setAasIdentifier(deviceObj.getAasIdentifier());
		
		//inputs
		List<ParameterDTO> ipsServiceMotorStatus = new ArrayList<ParameterDTO>();
		ipsServiceMotorStatus.add(new ParameterDTO("motor_id", "string", "move|piece"));
		serviceMotorStatus.setServiceInputParameters(ipsServiceMotorStatus);
		
		//outputs
		List<ParameterDTO> opsServiceMotorStatus = new ArrayList<ParameterDTO>();
		opsServiceMotorStatus.add(new ParameterDTO("motor_status", "json", "STOPPED|MOVING|THROWING"));
		serviceMotorStatus.setServiceOutputParameters(opsServiceMotorStatus);

		//quality		
		serviceMotorStatus.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceMotorStatus);
		
		//MoveFeedTrayToFarLeft
		ServiceDTO serviceMoveFeedTrayToFarLeft = new ServiceDTO();
		serviceMoveFeedTrayToFarLeft.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMoveFeedTrayToFarLeft.setServiceUrl("http://" + deviceIpAddress + ":80/robot/move_left");
		serviceMoveFeedTrayToFarLeft.setServiceMethod(EnumServiceMethod.POST.name());
		serviceMoveFeedTrayToFarLeft.setServiceIsAsync(true);
		serviceMoveFeedTrayToFarLeft.setServiceName("MoveFeedTrayToFarLeft");
		serviceMoveFeedTrayToFarLeft.setServiceDescription("Moves feed tray to far left");
		serviceMoveFeedTrayToFarLeft.setAasIdentifier(deviceObj.getAasIdentifier());
		
		//outputs
		List<ParameterDTO> outputParametersServiceMoveFeedTrayToFarLeft = new ArrayList<ParameterDTO>();
		outputParametersServiceMoveFeedTrayToFarLeft.add(new ParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving left\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveFeedTrayToFarLeft.setServiceOutputParameters(outputParametersServiceMoveFeedTrayToFarLeft);
		
		//quality	
		serviceMoveFeedTrayToFarLeft.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceMoveFeedTrayToFarLeft);
		
		//MoveFeedTrayToFarRight
		ServiceDTO serviceMoveFeedTrayToFarRight = new ServiceDTO();
		serviceMoveFeedTrayToFarRight.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMoveFeedTrayToFarRight.setServiceUrl("http://" + deviceIpAddress + ":80/robot/move_right");
		serviceMoveFeedTrayToFarRight.setServiceMethod(EnumServiceMethod.POST.name());
		serviceMoveFeedTrayToFarRight.setServiceIsAsync(true);
		serviceMoveFeedTrayToFarRight.setServiceName("MoveFeedTrayToFarRight");
		serviceMoveFeedTrayToFarRight.setServiceDescription("Moves feed tray to far right");
		serviceMoveFeedTrayToFarRight.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> serviceOutputParametersServiceMoveFeedTrayToFarRight = new ArrayList<ParameterDTO>();
		serviceOutputParametersServiceMoveFeedTrayToFarRight.add(new ParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving right\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveFeedTrayToFarRight.setServiceOutputParameters(serviceOutputParametersServiceMoveFeedTrayToFarRight);
		
		//quality	
		serviceMoveFeedTrayToFarRight.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceMoveFeedTrayToFarRight);
		
		//ThrowCurrentPiece
		ServiceDTO serviceThrowCurrentPiece = new ServiceDTO();
		serviceThrowCurrentPiece.setServiceIdentifier(UUID.randomUUID().toString());
		serviceThrowCurrentPiece.setServiceUrl("http://" + deviceIpAddress + ":80/robot/throw_piece");
		serviceThrowCurrentPiece.setServiceMethod(EnumServiceMethod.POST.name());
		serviceThrowCurrentPiece.setServiceIsAsync(true);
		serviceThrowCurrentPiece.setServiceName("ThrowCurrentPiece");
		serviceThrowCurrentPiece.setServiceDescription("Throws current piece out of the feed tray");
		serviceThrowCurrentPiece.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> opsServiceThrowCurrentPiece = new ArrayList<ParameterDTO>();
		opsServiceThrowCurrentPiece.add(new ParameterDTO("message_piece_thrown", "json", "{"
				+ "  \'message\': \'piece thrown\',"
				+ "  \'next_color\': \'Yellow\',"
				+ "  \'thrown_color\': \'Blue\'"
				+ "}"));
		serviceThrowCurrentPiece.setServiceOutputParameters(opsServiceThrowCurrentPiece);
		
		//quality	
		serviceThrowCurrentPiece.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceThrowCurrentPiece);
		
		
		
		//IsFeedTrayToFarRight
		ServiceDTO serviceIsFeedTrayToFarRight = new ServiceDTO();
		serviceIsFeedTrayToFarRight.setServiceIdentifier(UUID.randomUUID().toString());
		serviceIsFeedTrayToFarRight.setServiceUrl("http://" + deviceIpAddress + ":80/brickpi/sensor/touch_right/value");
		serviceIsFeedTrayToFarRight.setServiceMethod(EnumServiceMethod.GET.name());
		serviceIsFeedTrayToFarRight.setServiceIsAsync(false);
		serviceIsFeedTrayToFarRight.setServiceName("IsFeedTrayToFarRight");
		serviceIsFeedTrayToFarRight.setServiceDescription("Evaluates whether the feed tray is positioned at far right of the conveyor");
		serviceIsFeedTrayToFarRight.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> opsServiceIsFeedTrayToFarRight = new ArrayList<ParameterDTO>();
		opsServiceIsFeedTrayToFarRight.add(new ParameterDTO("IsFeedTrayToFarRight", "bool", "true|false"));
		serviceIsFeedTrayToFarRight.setServiceOutputParameters(opsServiceIsFeedTrayToFarRight);
		
		//quality	
		serviceIsFeedTrayToFarRight.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceIsFeedTrayToFarRight);
				
		//IsFeedTrayToFarLeft
		ServiceDTO serviceIsFeedTrayToFarLeft = new ServiceDTO();
		serviceIsFeedTrayToFarLeft.setServiceIdentifier(UUID.randomUUID().toString());
		serviceIsFeedTrayToFarLeft.setServiceUrl("http://" + deviceIpAddress + ":80/brickpi/sensor/touch_left/value");
		serviceIsFeedTrayToFarLeft.setServiceMethod(EnumServiceMethod.GET.name());
		serviceIsFeedTrayToFarLeft.setServiceIsAsync(false);
		serviceIsFeedTrayToFarLeft.setServiceName("IsFeedTrayToFarLeft");
		serviceIsFeedTrayToFarLeft.setServiceDescription("Evaluates whether the feed tray is positioned at far left of the conveyor");
		serviceIsFeedTrayToFarLeft.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> opsServiceIsFeedTrayToFarLeft = new ArrayList<ParameterDTO>();
		opsServiceIsFeedTrayToFarLeft.add(new ParameterDTO("IsFeedTrayToFarLeft", "bool", "true|false"));
		serviceIsFeedTrayToFarLeft.setServiceOutputParameters(opsServiceIsFeedTrayToFarLeft);
		
		//quality	
		serviceIsFeedTrayToFarLeft.setServiceQualityParameters(Tools.GenerateDefaultQoS(EnumQualityGenStrategy.RANDOM_VALUES));
		lServices.add(serviceIsFeedTrayToFarLeft);
		
		//build query insert
		String queryInsert = rdfDal.prepareInsertDeviceQuery(deviceObj);
		return queryInsert;
	}
}
package mgep.ContextAwareAasBpmn.MAPEK.Monitor;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import mgep.ContextAwareAasBpmn.Core.Tools;
import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.Enums.EnumQualityGenStrategy;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.RDFDAL;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.RDFRepositoryManager;

public class ShellAgentUpdater {
	
	public static void main(String[] args) throws Exception {
		//read AAS and map values
		var agentUpdater = new ShellAgentUpdater();
		var rdfDal = new RDFDAL();
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//delete all data
		System.out.println("Deleting...");
		String prepareDelete = "delete where {?s ?o ?p};";
		repManager.executeQuery(Tools.REPOSITORY_ID, prepareDelete);
		
		//get shells
		var lShells = agentUpdater.GetShells();
		var queryInsert = "";
		System.out.println(String.format("%s shells found in the AAS Server %s", lShells.size(), Tools.AAS_SERVER));
		System.out.println("Preparing insert...");
		
		//build and concatenate inserts
		for (var shellObj : lShells) {
			queryInsert += rdfDal.prepareInsertDeviceQuery(shellObj);
		}
		
		//execute inserts at once
		var resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, queryInsert);
		System.out.println(String.format("Insert result: %s shells: %s", resultInsertAllAtOnce, lShells.size()));
		if (!resultInsertAllAtOnce) return;
		
		//update QoS values every x milliseconds
		while(true) {			
			for (var shellObj : lShells) {
				//eval shell has services
				if (shellObj.getServices() == null || shellObj.getServices().size() == 0) {
					System.out.println(String.format("Shell %s does not have any services", shellObj.getAasIdShort()));
					continue;
				}
				
				//loop through services and update QoS
				for (var serviceObj : shellObj.getServices()) {
					var lNewQualityParams = Tools.GenerateDefaultQoS(EnumQualityGenStrategy.BEST_VALUES);
					
					//Get quality values from InterfaceConnectionSheet aas submodel
					//var lNewQualityParams = agentUpdater.GetQualityValsFromInterfacteConnectionSubmodel(shellObj.getDeviceIPAddress(), serviceObj.getServiceQualityParameters());
					var updateQualityParamsResult = rdfDal.UpdateQualityParamtersOfAService(serviceObj.getServiceIdentifier(), lNewQualityParams);
					System.out.println(String.format("Update quality params result: %s Shell: %s Service: %s #QoS: %s", updateQualityParamsResult, shellObj.getAasIdShort(), serviceObj.getServiceName(), lNewQualityParams.size()));
				}
			}
			System.out.println(String.format("Waiting %s milliseconds for next update...", Tools.INTERVAL_GATHER_QOS_VALS));
			Thread.sleep(Tools.INTERVAL_GATHER_QOS_VALS);
		}
	}
	
	public List<DeviceDTO> GetShells() throws Exception{
		var url = new URL(Tools.AAS_SERVER + "aasServer/shells");
		var con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		var response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//convert to json object
		var objectMapper = new ObjectMapper();
		var arJsonNodes = objectMapper.readTree(response.toString());

		//iterate over the shells
		var lShells = new ArrayList<DeviceDTO>();
		for (var jsonNode : arJsonNodes) {
			var deviceObj = new DeviceDTO();
			deviceObj.setAasIdShort(jsonNode.get("idShort").asText());
			deviceObj.setAasIdentifier(jsonNode.get("identification").get("id").asText());
			deviceObj.setAasName(jsonNode.get("idShort").asText());
			deviceObj.setDeviceIdentifier(jsonNode.get("identification").get("id").asText());
			deviceObj.setDeviceName("Device_" + jsonNode.get("idShort").asText());
			deviceObj.setDeviceIsOnline(true);

			if (jsonNode.get("description") != null) {
				for (var descriptionObj : jsonNode.get("description")) {
					if (descriptionObj.get("language").asText().equals("en")) {
						deviceObj.setDeviceDescription(descriptionObj.get("text").asText());
						break;
					}
				}				
			}

			//map submodels
			var lSubmodels = GetSubmodels(deviceObj.getAasIdentifier());
			JsonNode interfaceConnectionSheetSubmodel = null;
			JsonNode restServicesSubmodel = null;
			for (var submodelObj : lSubmodels) {
				if (submodelObj.get("idShort").asText().equals("RestServices")) restServicesSubmodel = submodelObj;
				if (submodelObj.get("idShort").asText().equals("InterfaceConnectionSheet")) interfaceConnectionSheetSubmodel = submodelObj;
			}

			//ip address
			//SENSOR quality parameters
			var lSensorQualityParameters = new ArrayList<QualityParameterDTO>();
			if (interfaceConnectionSheetSubmodel != null) {
				for (var icsElement : interfaceConnectionSheetSubmodel.get("submodelElements")) {
					if (icsElement.get("idShort").asText().equals("DeviceIP")) {
						deviceObj.setDeviceIPAddress(icsElement.get("value").asText());
						continue;
					}
					
					if(icsElement.get("value") != null) {
						var icsRecord = new QualityParameterDTO();
						for (var icsRecordElement : icsElement.get("value")) {
							switch (icsRecordElement.get("idShort").asText()) {
								case "CommunicationProtocol":
									icsRecord.setIcsCommunicationProtocol(icsRecordElement.get("value").asText());
									break;
								case "CommunicationPort":
									icsRecord.setIcsCommunicationPort(icsRecordElement.get("value").asText());							
									break;
								case "EndpointNodeId":
									icsRecord.setIcsEndpointNodeId(icsRecordElement.get("value").asText());
									break;
								case "NameSpaceIndex":
									icsRecord.setIcsNameSpaceIndex(icsRecordElement.get("value").asText());
									break;
								case "ShortName":
									icsRecord.setIcsShortName(icsRecordElement.get("value").asText());
									break;
								case "Description":
									icsRecord.setIcsDescription(icsRecordElement.get("value").asText());
									break;
								case "DataType":
									icsRecord.setIcsDataType(icsRecordElement.get("value").asText());
									break;
								default:
									break;
							}							
						}
						lSensorQualityParameters.add(icsRecord);
					}
				}				
			}

			if (deviceObj.getDeviceIPAddress() != null) deviceObj.setDeviceApiDocumentation("http://" + deviceObj.getDeviceIPAddress() + ":80/swagger");

			//services
			var lServices = new ArrayList<ServiceDTO>();
			deviceObj.setServices(lServices);
			for (var rsElement : restServicesSubmodel.get("submodelElements")) {
				var serviceObj = new ServiceDTO();
				serviceObj.setServiceIdentifier(UUID.randomUUID().toString());
				serviceObj.setAasIdentifier(deviceObj.getAasIdentifier());
				
				if (jsonNode.get("description") != null) {
					for (var descriptionObj : rsElement.get("description")) {
						if (descriptionObj.get("language").asText().equals("en")) {
							serviceObj.setServiceDescription(descriptionObj.get("text").asText());
							break;
						}
					}					
				}
				
				//iterate over restServices properties and map the values
				for (var serviceJson : rsElement.get("value")) {					
					switch (serviceJson.get("idShort").asText()) {
						case "URL":
							serviceObj.setServiceUrl(serviceJson.get("value").asText());
							break;
						case "Name":
							serviceObj.setServiceName(serviceJson.get("value").asText());
							break;
						case "Method":
							serviceObj.setServiceMethod(serviceJson.get("value").asText());
							break;
						case "IsAsync":
							serviceObj.setServiceIsAsync(serviceJson.get("value").asBoolean());
							break;
						default:
							break;
					}
				}
				
				//outputs
				//TODO: need to map this from AAS
				var opsServiceGetPieceColor = new ArrayList<ParameterDTO>();
				opsServiceGetPieceColor.add(new ParameterDTO("color", "text", "Yellow|Red|Blue|Green"));
				serviceObj.setServiceOutputParameters(opsServiceGetPieceColor);
				
				//set quality params from aas, they dont have values during first run
				serviceObj.setServiceQualityParameters(lSensorQualityParameters);
				
				//generate random quality values for the first time 
				//serviceObj.setServiceQualityParameters(generateDefaultQoS());
				
				//add service to the list
				lServices.add(serviceObj);				
			}


			lShells.add(deviceObj);
		}

		return lShells;
	}

	private JsonNode GetSubmodels(String shellIdentifier) throws Exception {
		//http://localhost:8081/aasServer/shells/https%3A%2F%2Fexample.com%2Fids%2Faas%2F4382_9062_0122_8497/aas/submodels
		var encodedShellIdentifier = URLEncoder.encode(shellIdentifier, StandardCharsets.UTF_8);
		var url = new URL(Tools.AAS_SERVER + "aasServer/shells/" + encodedShellIdentifier + "/aas/submodels");
		var con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		var response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//convert to json object
		var objectMapper = new ObjectMapper();
		return objectMapper.readTree(response.toString());
	}
	
	public List<QualityParameterDTO> GetQualityValsFromInterfacteConnectionSubmodel(String deviceIP, List<QualityParameterDTO> prevQualityParams){
		for (var qosParam : prevQualityParams) {
			qosParam.setParameterValue(GetQualityValFromInterfacteConnectionSubmodel(deviceIP, qosParam));
		}
		
		return prevQualityParams;
	}
	
	public String GetQualityValFromInterfacteConnectionSubmodel(String deviceIP, InterfaceConnectionSheetDTO icsRecord){
		if (icsRecord == null) return null;
		
		String dataValue = "";
		
		switch (icsRecord.getIcsCommunicationProtocol()) {
		case "opc.tcp":
			var client = new OpcuaConsumer("opc.tcp://" + deviceIP + ":" + icsRecord.getIcsCommunicationPort());
			dataValue = client.ReadOpcuaValue(Integer.valueOf(icsRecord.getIcsNameSpaceIndex()), icsRecord.getIcsEndpointNodeId());
			break;
		case "http": case "https":
			try {
				var url = new URL(icsRecord.getIcsCommunicationProtocol() + "://" + deviceIP + ":" + icsRecord.getIcsCommunicationPort() + "/" + icsRecord.getIcsEndpointNodeId());
				var con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");

				var in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				var response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				dataValue = response.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		default:
			break;
		}
		
		return dataValue;
	}
}

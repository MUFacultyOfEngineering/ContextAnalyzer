package mgep.ContextAwareAasBpmn.DeviceServiceOntClient.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.Enums.*;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.*;
import mgep.ContextAwareAasBpmn.Core.*;

public class SynchronizeThisDeviceData {

	public static void main(String[] args) {
		//load config file
		String filePath = SynchronizeThisDeviceData.class.getClassLoader().getResource("config.properties").getPath();
		Tools.LoadEnvironmentFromPropertiesFile(filePath);
		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		while(true) {
			//delete all
			System.out.println("Deleting");
			String prepareDelete = "delete where {?s ?o ?p};";
			repManager.executeQuery(Tools.REPOSITORY_ID, prepareDelete);
			
			for (int i = 1; i <= 1536; i++) {
				insertColorSorters(repManager, i, "192.168.56.10" + i);
			}
			try {
				System.out.println("Waiting for next run");
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertColorSorters(RDFRepositoryManager repManager, int deviceId, String deviceIpAddress) {
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
		serviceGetPieceColor.setServiceQualityParameters(generateQoS());
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
		serviceMotorStatus.setServiceQualityParameters(generateQoS());
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
		serviceMoveFeedTrayToFarLeft.setServiceQualityParameters(generateQoS());
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
		serviceMoveFeedTrayToFarRight.setServiceQualityParameters(generateQoS());
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
		serviceThrowCurrentPiece.setServiceQualityParameters(generateQoS());
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
		serviceIsFeedTrayToFarRight.setServiceQualityParameters(generateQoS());
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
		serviceIsFeedTrayToFarLeft.setServiceQualityParameters(generateQoS());
		lServices.add(serviceIsFeedTrayToFarLeft);
		
		//build query insert
		String queryInsert = prepareInsertDeviceQuery(deviceObj);
		boolean resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, queryInsert);
		System.out.println(String.format("Insert result: %s DeviceID: %s", resultInsertAllAtOnce, deviceId));
	}
	
	private static String prepareInsertDeviceQuery(DeviceDTO deviceObj) {
		String insertQueryDevice = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + deviceObj.getDeviceName() + " rdf:type dsOnt:Device ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasIdentifier \"" + deviceObj.getAasIdentifier() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasIdShort \"" + deviceObj.getAasIdShort() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasName \"" + deviceObj.getAasName() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceApiDocumentation \"" + deviceObj.getDeviceApiDocumentation() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceDescription \"" + deviceObj.getDeviceDescription() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIdentifier \"" + deviceObj.getDeviceIdentifier() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIPAddress \"" + deviceObj.getDeviceIPAddress() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIsOnline \"" + deviceObj.getDeviceIsOnline() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceName \"" + deviceObj.getDeviceName() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceNetworkLatency \"" + deviceObj.getDeviceNetworkLatency() + "\" ."
				+ "};";
		
		String insertSensorQuery = "";		
		if(deviceObj.getSensors() != null || deviceObj.getSensors().size() > 0) {
			for (SensorDTO item : deviceObj.getSensors()) {
				insertSensorQuery += prepareInsertSensorQuery(deviceObj.getDeviceName(), item);
			}			
		}
		
		String insertQueryServices = "";
		for (ServiceDTO item : deviceObj.getServices()) {
			insertQueryServices += prepareInsertServiceQuery(deviceObj.getDeviceName(), item); 
		}
		
		return insertQueryDevice + insertSensorQuery + insertQueryServices;
	}
	
	private static String prepareInsertSensorQuery(String deviceName, SensorDTO sensorObj) {		
		String fullSensorName = String.format("%s_%s", deviceName, sensorObj.getSensorName());
		
		String insertQuery = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullSensorName + " rdf:type dsOnt:Sensor ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorDescription \"" + sensorObj.getSensorDescription() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorIdentifier \"" + sensorObj.getSensorIdentifier() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorName \"" + sensorObj.getSensorName() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorType dsOnt:" + sensorObj.getSensorType().name() + " ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueDataType \"" + sensorObj.getSensorValueDataType() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueDataValue \"" + sensorObj.getSensorValueDataValue() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueUnit \"" + sensorObj.getSensorValueDataUnit() + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:hasSensor dsOnt:" + fullSensorName
				+ "};";
		return insertQuery;
	}
	
	private static String prepareInsertServiceQuery(String deviceName, ServiceDTO service) {		
		String fullserviceName = String.format("%s_%s", deviceName, service.getServiceName());
		
		String insertQueryInputParams = "";
		if (service.getServiceInputParameters() != null && service.getServiceInputParameters().size() > 0) {
			for (ParameterDTO item : service.getServiceInputParameters()) {
				String paramName =  String.format("%s_Input_%s", fullserviceName, item.getParameterName());
				insertQueryInputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Input ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasInput dsOnt:" + paramName
						+ " .";
			}			
		}
				
		String insertQueryOutputParams = "";
		if (service.getServiceOutputParameters() != null && service.getServiceOutputParameters().size() > 0) {
			for (ParameterDTO item : service.getServiceOutputParameters()) {
				String paramName =  String.format("%s_Output_%s", fullserviceName, item.getParameterName());
				insertQueryOutputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Output ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasOutput dsOnt:" + paramName
						+ " .";
			}
		}
		
		String insertQueryQualityParams = "";
		if (service.getServiceQualityParameters() != null && service.getServiceQualityParameters().size() > 0) {
			for (QualityParameterDTO item : service.getServiceQualityParameters()) {
				String paramName =  String.format("%s_Quality_%s", fullserviceName, item.getParameterName());
				insertQueryQualityParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Quality ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterCorrespondsTo \"" + item.getQualityParameterCorrespondsTo() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterEvaluationExpression \"" + item.getQualityParameterEvaluationExpression() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasQuality dsOnt:" + paramName
						+ " .";
			}			
		}
		
		String insertServiceQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullserviceName + " rdf:type dsOnt:Service ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceDescription \"" + service.getServiceDescription() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceIdentifier \"" + service.getServiceIdentifier() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceIsAsync \"" + service.isServiceIsAsync() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceMethod \"" + service.getServiceMethod() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceName \"" + service.getServiceName() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceURL \"" + service.getServiceUrl() + "\" ."
				+ insertQueryInputParams
				+ insertQueryOutputParams
				+ insertQueryQualityParams
				+ "    dsOnt:" + deviceName + " dsOnt:hasService dsOnt:" + fullserviceName
				+ "};";
		return insertServiceQuery;
	}
	
	private static List<QualityParameterDTO> generateQoS(){
		List<QualityParameterDTO> qos = new ArrayList<QualityParameterDTO>();
		qos.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
		qos.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
		qos.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
		qos.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
		qos.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
		qos.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
		qos.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
		qos.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
		qos.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
		qos.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
		qos.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));
		return qos;
	}
}
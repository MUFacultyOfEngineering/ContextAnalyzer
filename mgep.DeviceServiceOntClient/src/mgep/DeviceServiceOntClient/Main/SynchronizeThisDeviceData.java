package mgep.DeviceServiceOntClient.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mgep.ContextAwareAasBpmn.Enums.*;
import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.DataAccess.*;
import mgep.ContextAwareAasBpmn.Core.*;

public class SynchronizeThisDeviceData {

	public static void main(String[] args) {
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		while(true) {
			//delete all
			System.out.println("Deleting");
			String prepareDelete = "delete where {?s ?o ?p};";
			repManager.executeQuery(Tools.REPOSITORY_ID, prepareDelete);
			
			for (int i = 1; i <= 3; i++) {
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
		
		//motorStatus
		ServiceDTO serviceMotorStatus = new ServiceDTO();
		serviceMotorStatus.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMotorStatus.setServiceUrl("http://" + deviceIpAddress + ":80/brickpi/motor/{motor_id}/status");
		serviceMotorStatus.setServiceMethod(EnumServiceMethod.GET.name());
		serviceMotorStatus.setServiceIsAsync(true);
		serviceMotorStatus.setServiceName("Service_GetMotorStatus");
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
		List<QualityParameterDTO> qosServiceMotorStatus = new ArrayList<QualityParameterDTO>();
		qosServiceMotorStatus.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
		qosServiceMotorStatus.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
		qosServiceMotorStatus.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
		qosServiceMotorStatus.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
		qosServiceMotorStatus.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
		qosServiceMotorStatus.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
		qosServiceMotorStatus.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));		
		serviceMotorStatus.setServiceQualityParameters(qosServiceMotorStatus);
		lServices.add(serviceMotorStatus);
		
		//moveLeft
		ServiceDTO serviceMoveLeft = new ServiceDTO();
		serviceMoveLeft.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMoveLeft.setServiceUrl("http://" + deviceIpAddress + ":80/robot/move_left");
		serviceMoveLeft.setServiceMethod(EnumServiceMethod.POST.name());
		serviceMoveLeft.setServiceIsAsync(true);
		serviceMoveLeft.setServiceName("Service_MoveLeft");
		serviceMoveLeft.setServiceDescription("Moves feed tray to far left");
		serviceMoveLeft.setAasIdentifier(deviceObj.getAasIdentifier());
		
		//outputs
		List<ParameterDTO> outputParametersServiceMoveLeft = new ArrayList<ParameterDTO>();
		outputParametersServiceMoveLeft.add(new ParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving left\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveLeft.setServiceOutputParameters(outputParametersServiceMoveLeft);
		
		//quality
		List<QualityParameterDTO> qosServiceMoveLeft = new ArrayList<QualityParameterDTO>();
		qosServiceMoveLeft.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
		qosServiceMoveLeft.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
		qosServiceMoveLeft.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
		qosServiceMoveLeft.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
		qosServiceMoveLeft.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
		qosServiceMoveLeft.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
		qosServiceMoveLeft.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));	
		serviceMoveLeft.setServiceQualityParameters(qosServiceMoveLeft);
		lServices.add(serviceMoveLeft);
		
		//moveRight
		ServiceDTO serviceMoveRight = new ServiceDTO();
		serviceMoveRight.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMoveRight.setServiceUrl("http://" + deviceIpAddress + ":80/robot/move_right");
		serviceMoveRight.setServiceMethod(EnumServiceMethod.POST.name());
		serviceMoveRight.setServiceIsAsync(true);
		serviceMoveRight.setServiceName("Service_MoveRight");
		serviceMoveRight.setServiceDescription("Moves feed tray to far right");
		serviceMoveRight.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> serviceOutputParametersServiceMoveRight = new ArrayList<ParameterDTO>();
		serviceOutputParametersServiceMoveRight.add(new ParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving right\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveRight.setServiceOutputParameters(serviceOutputParametersServiceMoveRight);
		
		//quality
		List<QualityParameterDTO> qosServiceMoveRight = new ArrayList<QualityParameterDTO>();
		qosServiceMoveRight.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
		qosServiceMoveRight.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
		qosServiceMoveRight.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
		qosServiceMoveRight.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
		qosServiceMoveRight.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
		qosServiceMoveRight.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
		qosServiceMoveRight.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));	
		serviceMoveRight.setServiceQualityParameters(qosServiceMoveRight);
		lServices.add(serviceMoveRight);
		
		//throwPiece
		ServiceDTO serviceThrowPiece = new ServiceDTO();
		serviceThrowPiece.setServiceIdentifier(UUID.randomUUID().toString());
		serviceThrowPiece.setServiceUrl("http://" + deviceIpAddress + ":80/robot/throw_piece");
		serviceThrowPiece.setServiceMethod(EnumServiceMethod.POST.name());
		serviceThrowPiece.setServiceIsAsync(true);
		serviceThrowPiece.setServiceName("Service_ThrowPiece");
		serviceThrowPiece.setServiceDescription("Throws current piece out of the feed tray");
		serviceThrowPiece.setAasIdentifier(deviceObj.getAasIdentifier());

		//outputs
		List<ParameterDTO> opsServiceThrowPiece = new ArrayList<ParameterDTO>();
		opsServiceThrowPiece.add(new ParameterDTO("message_piece_thrown", "json", "{"
				+ "  \'message\': \'piece thrown\',"
				+ "  \'next_color\': \'Yellow\',"
				+ "  \'thrown_color\': \'Blue\'"
				+ "}"));
		serviceThrowPiece.setServiceOutputParameters(opsServiceThrowPiece);
		
		//quality
		List<QualityParameterDTO> qosServiceThrowPiece = new ArrayList<QualityParameterDTO>();
		qosServiceThrowPiece.add(new QualityParameterDTO("SuccessRate", "Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), EnumQualityType.SERVICE.name(), "SuccessRate >= 80"));
		qosServiceThrowPiece.add(new QualityParameterDTO("AvgResponseTime", "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "AvgResponseTime <= 1000"));
		qosServiceThrowPiece.add(new QualityParameterDTO("LastResponseTime", "Decimal", String.valueOf(Tools.GetRandomNumber(100, 10000)), EnumQualityType.SERVICE.name(), "LastResponseTime <= 1000"));
		qosServiceThrowPiece.add(new QualityParameterDTO("AvgNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "AvgNetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("LastNetworkLatency", "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), EnumQualityType.DEVICE.name(), "LastNetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("HUMIDITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "HUMIDITY <= 50"));
		qosServiceThrowPiece.add(new QualityParameterDTO("TEMPERATURE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "TEMPERATURE <= 30"));
		qosServiceThrowPiece.add(new QualityParameterDTO("WEIGHT", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "WEIGHT <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("SIZE", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), EnumQualityType.SENSOR.name(), "SIZE <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("BATTERY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "BATTERY >= 65"));
		qosServiceThrowPiece.add(new QualityParameterDTO("PROXIMITY", "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), EnumQualityType.SENSOR.name(), "PROXIMITY <= 70"));	
		serviceThrowPiece.setServiceQualityParameters(qosServiceThrowPiece);
		lServices.add(serviceThrowPiece);
		
		//build query insert
		String queryInsert = prepareInsertDeviceQuery(deviceObj);
		boolean resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, queryInsert);
		System.out.println(String.format("Insert all properties at once: %s", resultInsertAllAtOnce));
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
}
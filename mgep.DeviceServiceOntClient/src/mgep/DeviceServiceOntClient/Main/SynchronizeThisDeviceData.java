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
		deviceObj.setName("Device_ColorSorter0" + deviceId);
		deviceObj.setDescription("A robot lego color sorter for the classification pieces by color.");
		deviceObj.setNetworkLatency(String.valueOf(Tools.GetRandomNumber(10, 500)));
		deviceObj.setApiDocumentation("http://" + deviceIpAddress + ":80/swagger");
		deviceObj.setAasIdentifier("AssetAdministrationShell---" + deviceId);
		deviceObj.setIpAddress(deviceIpAddress);
		String insertDeviceQuery = prepareInsertDeviceQuery(deviceObj);
		
		//prepare insert battery sensor
		List<SensorDTO> sensors = new ArrayList<SensorDTO>();
		SensorDTO sensorObj = new SensorDTO();
		sensorObj.setName("Sensor_Battery");
		sensorObj.setIdentifier(UUID.randomUUID().toString());
		sensorObj.setDescription("A battery sensor. values represent the remaining battery level from 0 to 100");
		sensorObj.setType(EnumSensorType.BATTERY);
		sensorObj.setDataUnit("Percent");
		sensorObj.setDataType("Integer");
		sensorObj.setDataValue(String.valueOf(Tools.GetRandomNumber(1, 100)));
		sensors.add(sensorObj);
		deviceObj.setSensors(sensors);
		String insertSensorQuery = prepareInsertSensorQuery(deviceObj);
		
		//prepare insert services
		//motorStatus
		ServiceDTO serviceMotorStatus = new ServiceDTO();
		serviceMotorStatus.setServiceIdentifier(UUID.randomUUID().toString());
		serviceMotorStatus.setUrl("http://" + deviceIpAddress + ":80/brickpi/motor/{motor_id}/status");
		serviceMotorStatus.setMethod(EnumServiceMethod.GET.name());
		serviceMotorStatus.setAsync(true);
		serviceMotorStatus.setName("Service_GetMotorStatus");
		serviceMotorStatus.setDescription("Gets motor status");
		serviceMotorStatus.setAasIdentifier(deviceObj.getAasIdentifier());
		
		List<ServiceParameterDTO> ipsServiceMotorStatus = new ArrayList<ServiceParameterDTO>();
		ipsServiceMotorStatus.add(new ServiceParameterDTO("motor_id", "string", "move|piece"));
		serviceMotorStatus.setInputParameters(ipsServiceMotorStatus);
		
		List<ServiceParameterDTO> opsServiceMotorStatus = new ArrayList<ServiceParameterDTO>();
		opsServiceMotorStatus.add(new ServiceParameterDTO("motor_status", "json", "STOPPED|MOVING|THROWING"));
		serviceMotorStatus.setOutputParameters(opsServiceMotorStatus);
		
		//quality
		List<QualityParameterDTO> qosServiceMotorStatus = new ArrayList<QualityParameterDTO>();
		qosServiceMotorStatus.add(new QualityParameterDTO("SuccessRate", EnumQualityType.SERVICE.name() ,"Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), "SuccessRate >= 80"));
		qosServiceMotorStatus.add(new QualityParameterDTO("AvgResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "AvgResponseTime <= 1000"));
		qosServiceMotorStatus.add(new QualityParameterDTO("LastResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "LastResponseTime <= 1000"));
		qosServiceMotorStatus.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "AvgNetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("HUMIDITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "HUMIDITY <= 50"));
		qosServiceMotorStatus.add(new QualityParameterDTO("TEMPERATURE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "TEMPERATURE <= 30"));
		qosServiceMotorStatus.add(new QualityParameterDTO("WEIGHT", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "WEIGHT <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("SIZE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "SIZE <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("BATTERY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BATTERY >= 65"));
		qosServiceMotorStatus.add(new QualityParameterDTO("PROXIMITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "PROXIMITY <= 70"));
		serviceMotorStatus.setQualityParameters(qosServiceMotorStatus);
		
		String insertQueryServiceMotorStatus = prepareInsertServiceQuery(deviceObj.getName(), serviceMotorStatus);
		
		//prepare insert services
		//moveLeft
		ServiceDTO serviceMoveLeft = new ServiceDTO(deviceObj.getAasIdentifier(), UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/move_left", EnumServiceMethod.POST.name(), true, "Service_MoveLeft", "Moves feed tray to far left");
		
		List<ServiceParameterDTO> outputParametersServiceMoveLeft = new ArrayList<ServiceParameterDTO>();
		outputParametersServiceMoveLeft.add(new ServiceParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving left\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveLeft.setInputParameters(outputParametersServiceMoveLeft);
		
		//quality
		List<QualityParameterDTO> qosServiceMoveLeft = new ArrayList<QualityParameterDTO>();
		qosServiceMoveLeft.add(new QualityParameterDTO("SuccessRate", EnumQualityType.SERVICE.name() ,"Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), "SuccessRate >= 80"));
		qosServiceMoveLeft.add(new QualityParameterDTO("AvgResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "AvgResponseTime <= 1000"));
		qosServiceMoveLeft.add(new QualityParameterDTO("LastResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "LastResponseTime <= 1000"));
		qosServiceMoveLeft.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "AvgNetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("HUMIDITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "HUMIDITY <= 50"));
		qosServiceMoveLeft.add(new QualityParameterDTO("TEMPERATURE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "TEMPERATURE <= 30"));
		qosServiceMoveLeft.add(new QualityParameterDTO("WEIGHT", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "WEIGHT <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("SIZE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "SIZE <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("BATTERY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BATTERY >= 65"));
		qosServiceMoveLeft.add(new QualityParameterDTO("PROXIMITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "PROXIMITY <= 70"));
		serviceMoveLeft.setQualityParameters(qosServiceMoveLeft);
		
		String insertQueryServiceMoveLeft = prepareInsertServiceQuery(deviceObj.getName(), serviceMoveLeft);
		
		//prepare insert services
		//moveRight
		ServiceDTO serviceMoveRight = new ServiceDTO(deviceObj.getAasIdentifier(), UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/move_right", EnumServiceMethod.POST.name(), true, "Service_MoveRight", "Moves feed tray to far right");

		List<ServiceParameterDTO> serviceOutputParametersServiceMoveRight = new ArrayList<ServiceParameterDTO>();
		serviceOutputParametersServiceMoveRight.add(new ServiceParameterDTO("message_moving_motor", "json", "{"
				+ "  \'message\': \'Moving right\',"
				+ "  \'motor_id\': \'move\'"
				+ "}"));
		serviceMoveRight.setInputParameters(serviceOutputParametersServiceMoveRight);
		
		//quality
		List<QualityParameterDTO> qosServiceMoveRight = new ArrayList<QualityParameterDTO>();
		qosServiceMoveRight.add(new QualityParameterDTO("SuccessRate", EnumQualityType.SERVICE.name() ,"Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), "SuccessRate >= 80"));
		qosServiceMoveRight.add(new QualityParameterDTO("AvgResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "AvgResponseTime <= 1000"));
		qosServiceMoveRight.add(new QualityParameterDTO("LastResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "LastResponseTime <= 1000"));
		qosServiceMoveRight.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "AvgNetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("HUMIDITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "HUMIDITY <= 50"));
		qosServiceMoveRight.add(new QualityParameterDTO("TEMPERATURE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "TEMPERATURE <= 30"));
		qosServiceMoveRight.add(new QualityParameterDTO("WEIGHT", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "WEIGHT <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("SIZE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "SIZE <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("BATTERY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BATTERY >= 65"));
		qosServiceMoveRight.add(new QualityParameterDTO("PROXIMITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "PROXIMITY <= 70"));
		serviceMoveRight.setQualityParameters(qosServiceMoveRight);
		
		String insertQueryServiceMoveRight = prepareInsertServiceQuery(deviceObj.getName(), serviceMoveRight);
		
		//prepare insert services
		//throwPiece
		ServiceDTO serviceThrowPiece = new ServiceDTO(deviceObj.getAasIdentifier(), UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/throw_piece", EnumServiceMethod.POST.name(), true, "Service_ThrowPiece", "Throws current piece out of the feed tray");

		List<ServiceParameterDTO> opsServiceThrowPiece = new ArrayList<ServiceParameterDTO>();
		opsServiceThrowPiece.add(new ServiceParameterDTO("message_piece_thrown", "json", "{"
				+ "  \'message\': \'piece thrown\',"
				+ "  \'next_color\': \'Yellow\',"
				+ "  \'thrown_color\': \'Blue\'"
				+ "}"));
		serviceThrowPiece.setInputParameters(opsServiceThrowPiece);
		
		//quality
		List<QualityParameterDTO> qosServiceThrowPiece = new ArrayList<QualityParameterDTO>();
		qosServiceThrowPiece.add(new QualityParameterDTO("SuccessRate", EnumQualityType.SERVICE.name() ,"Decimal", String.valueOf(Tools.GetRandomNumber(50, 100)), "SuccessRate >= 80"));
		qosServiceThrowPiece.add(new QualityParameterDTO("AvgResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "AvgResponseTime <= 1000"));
		qosServiceThrowPiece.add(new QualityParameterDTO("LastResponseTime", EnumQualityType.SERVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(100, 10000)), "LastResponseTime <= 1000"));
		qosServiceThrowPiece.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "AvgNetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("HUMIDITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "HUMIDITY <= 50"));
		qosServiceThrowPiece.add(new QualityParameterDTO("TEMPERATURE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "TEMPERATURE <= 30"));
		qosServiceThrowPiece.add(new QualityParameterDTO("WEIGHT", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "WEIGHT <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("SIZE", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "SIZE <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("BATTERY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BATTERY >= 65"));
		qosServiceThrowPiece.add(new QualityParameterDTO("PROXIMITY", EnumQualityType.SENSOR.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "PROXIMITY <= 70"));
		serviceThrowPiece.setQualityParameters(qosServiceThrowPiece);
		
		String insertQueryServiceThrowPiece = prepareInsertServiceQuery(deviceObj.getName(), serviceThrowPiece);

		//execute insert
		String allInserts = insertDeviceQuery + insertSensorQuery + insertQueryServiceMotorStatus + insertQueryServiceMoveLeft + insertQueryServiceMoveRight + insertQueryServiceThrowPiece;
		boolean resultInsertAllAtOnce = repManager.executeQuery(Tools.REPOSITORY_ID, allInserts);
		System.out.println(String.format("Insert all properties at once: %s", resultInsertAllAtOnce));
	}
	
	private static String prepareInsertDeviceQuery(DeviceDTO deviceObj) {
		String insertQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + deviceObj.getName() + " rdf:type dsOnt:Device ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:deviceName \"" + deviceObj.getName() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:deviceIPAddress \"" + deviceObj.getIpAddress() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:deviceDescription \"" + deviceObj.getDescription() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:deviceNetworkLatency \"" + deviceObj.getNetworkLatency() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + "_ApiDocumentation rdf:type dsOnt:ApiDocumentation ."
				+ "    dsOnt:" + deviceObj.getName() + "_ApiDocumentation dsOnt:deviceApiDocumentation \"" + deviceObj.getApiDocumentation() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:hasApiDocumentation dsOnt:" + deviceObj.getName() + "_ApiDocumentation ."
				+ "    dsOnt:" + deviceObj.getName() + "_AasIdentifier rdf:type dsOnt:AasIdentifier ."
				+ "    dsOnt:" + deviceObj.getName() + "_AasIdentifier dsOnt:deviceAasIdentifier \"" + deviceObj.getAasIdentifier() + "\" ."
				+ "    dsOnt:" + deviceObj.getName() + " dsOnt:hasAasIdentifier dsOnt:" + deviceObj.getName() + "_AasIdentifier"
				+ "};";
		return insertQuery;
	}
	
	private static String prepareInsertSensorQuery(DeviceDTO deviceObj) {
		if(deviceObj.getSensors() == null || deviceObj.getSensors().size() == 0) return null;
		
		String insertQuery = "";
		for (SensorDTO sensorObj : deviceObj.getSensors()) {
			String fullSensorName = String.format("%s_%s", deviceObj.getName(), sensorObj.getName());
			
			insertQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
					+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
					+ "INSERT DATA {"
					+ "    dsOnt:" + fullSensorName + " rdf:type dsOnt:Sensor ."
					+ "    dsOnt:" + fullSensorName + " dsOnt:sensorIdentifier \"" + sensorObj.getIdentifier() + "\" ."
					+ "    dsOnt:" + fullSensorName + " dsOnt:sensorName \"" + sensorObj.getName() + "\" ."
					+ "    dsOnt:" + fullSensorName + " dsOnt:sensorDescription \"" + sensorObj.getDescription() + "\" ."
					+ "    dsOnt:" + fullSensorName + " dsOnt:sensorType dsOnt:" + sensorObj.getType().name() + " ."
					+ "    dsOnt:" + fullSensorName + "_SensorValue rdf:type dsOnt:SensorValue ."
					+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueUnit \"" + sensorObj.getDataUnit() + "\" ."
					+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueDataType \"" + sensorObj.getDataType() + "\" ."
					+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueDataValue \"" + sensorObj.getDataValue() + "\" ."
					+ "    dsOnt:" + fullSensorName + " dsOnt:hasSensorValue dsOnt:" + fullSensorName + "_SensorValue ."
					+ "    dsOnt:" + deviceObj.getName() + " dsOnt:hasSensor dsOnt:" + fullSensorName
					+ "};";
		}
		return insertQuery;
	}
	
	private static String prepareInsertServiceQuery(String deviceName, ServiceDTO service) {		
		String fullserviceName = String.format("%s_%s", deviceName, service.getName());
		
		String insertQueryInputParams = "";
		if (service.getInputParameters() != null && service.getInputParameters().size() > 0) {
			for (ServiceParameterDTO item : service.getInputParameters()) {
				String paramName =  String.format("%s_Input_%s", fullserviceName, item.getName());
				insertQueryInputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Input ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterName \"" + item.getName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterType \"" + item.getType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterValue \"" + item.getValue() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasInput dsOnt:" + paramName
						+ " .";
			}			
		}
				
		String insertQueryOutputParams = "";
		if (service.getOutputParameters() != null && service.getOutputParameters().size() > 0) {
			for (ServiceParameterDTO item : service.getOutputParameters()) {
				String paramName =  String.format("%s_Output_%s", fullserviceName, item.getName());
				insertQueryOutputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Output ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterName \"" + item.getName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterType \"" + item.getType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:serviceParameterValue \"" + item.getValue() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasOutput dsOnt:" + paramName
						+ " .";
			}
		}
		
		String insertQueryQualityParams = "";
		if (service.getQualityParameters() != null && service.getQualityParameters().size() > 0) {
			for (QualityParameterDTO item : service.getQualityParameters()) {
				String paramName =  String.format("%s_Quality_%s", fullserviceName, item.getName());
				insertQueryQualityParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Quality ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterName \"" + item.getName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterCorrespondsTo \"" + item.getCorrespondsTo() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterType \"" + item.getDataType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterValue \"" + item.getValue() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterEvaluationExpression \"" + item.getEvaluationExpression() + "\" ."
						+ "    dsOnt:" + fullserviceName + " dsOnt:hasQuality dsOnt:" + paramName
						+ " .";
			}			
		}
		
		String insertServiceQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullserviceName + " rdf:type dsOnt:Service ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceIdentifier \"" + service.getServiceIdentifier() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceName \"" + service.getName() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceDescription \"" + service.getDescription() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceIsAsync \"" + service.isAsync() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceMethod \"" + service.getMethod() + "\" ."
				+ "    dsOnt:" + fullserviceName + " dsOnt:serviceURL \"" + service.getUrl() + "\" ."
				+ insertQueryInputParams
				+ insertQueryOutputParams
				+ insertQueryQualityParams
				+ "    dsOnt:" + deviceName + " dsOnt:hasService dsOnt:" + fullserviceName
				+ "};";
		return insertServiceQuery;
	}
}
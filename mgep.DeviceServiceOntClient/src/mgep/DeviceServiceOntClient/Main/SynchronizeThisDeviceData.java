package mgep.DeviceServiceOntClient.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mgep.ContextAwareAasBpmn.Enums.*;
import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.DataAccess.*;
import mgep.ContextAwareAasBpmn.Core.*;

public class SynchronizeThisDeviceData {

	public static void main(String[] args) {
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//delete all
		String prepareDelete = "delete where {?s ?o ?p};";;
		repManager.executeInsert(Tools.REPOSITORY_ID, prepareDelete);
		
		for (int i = 1; i <= 3; i++) {
			insertColorSorters(repManager, i, "192.168.56.10" + i);
		}
	}
	
	private static void insertColorSorters(RDFRepositoryManager repManager, int deviceId, String deviceIpAddress) {
		//prepare insert device data
		String deviceName = "Device_ColorSorter0" + deviceId;
		String deviceDescription = "Lego color sorter 01";
		String deviceNetworkLatency = String.format("%sms", ((Integer) (1 + new Random().nextInt(1000))).toString());
		String deviceApiDocumentationAddress = "http://" + deviceIpAddress + ":80/swagger";
		String deviceAasIdentifier = "AssetAdministrationShell---" + deviceId;
		String insertDeviceQuery = prepareInsertDeviceQuery(deviceName, deviceAasIdentifier, deviceIpAddress, deviceDescription, deviceNetworkLatency, deviceApiDocumentationAddress);
		
		//prepare insert battery sensor
		String sensorName = "Sensor_Battery";
		String sensorIdentifier = UUID.randomUUID().toString();
		String sensorDescription = "A battery sensor. values represent the remaining battery level from 0 to 100";
		EnumSensorType sensorType = EnumSensorType.sensorTypeBatteryLevel; 
		String sensorValueUnit ="Percent";
		String sensorValueDataType ="int";
		String sensorValueDataValue = String.valueOf(Tools.GetRandomNumber(1, 100));
		String insertSensorQuery = prepareInsertSensorQuery(deviceName, sensorName, sensorIdentifier, sensorDescription, sensorType, sensorValueUnit, sensorValueDataType, sensorValueDataValue);
		
		//prepare insert services
		//motorStatus
		ServiceDTO serviceMotorStatus = new ServiceDTO(deviceAasIdentifier, UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/brickpi/motor/{motor_id}/status", EnumServiceMethod.GET.name(), true, "Service_GetMotorStatus", "Gets motor status");
		
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
		qosServiceMotorStatus.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "NetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMotorStatus.add(new QualityParameterDTO("Humidity", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Humidity <= 50"));
		qosServiceMotorStatus.add(new QualityParameterDTO("Temperature", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Temperature <= 30"));
		qosServiceMotorStatus.add(new QualityParameterDTO("Weight", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Weight <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("Size", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Size <= 10"));
		qosServiceMotorStatus.add(new QualityParameterDTO("BatteryLevel", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BaterryLevel >= 65"));
		qosServiceMotorStatus.add(new QualityParameterDTO("DistanceAwayFromWorkStation", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "DistanceAwayFromWorkStation <= 70"));
		serviceMotorStatus.setQualityParameters(qosServiceMotorStatus);
		
		String insertQueryServiceMotorStatus = prepareInsertServiceQuery(deviceName, serviceMotorStatus);
		
		//prepare insert services
		//moveLeft
		ServiceDTO serviceMoveLeft = new ServiceDTO(deviceAasIdentifier, UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/move_left", EnumServiceMethod.POST.name(), true, "Service_MoveLeft", "Moves feed tray to far left");
		
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
		qosServiceMoveLeft.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "NetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMoveLeft.add(new QualityParameterDTO("Humidity", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Humidity <= 50"));
		qosServiceMoveLeft.add(new QualityParameterDTO("Temperature", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Temperature <= 30"));
		qosServiceMoveLeft.add(new QualityParameterDTO("Weight", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Weight <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("Size", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Size <= 10"));
		qosServiceMoveLeft.add(new QualityParameterDTO("BatteryLevel", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BaterryLevel >= 65"));
		qosServiceMoveLeft.add(new QualityParameterDTO("DistanceAwayFromWorkStation", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "DistanceAwayFromWorkStation <= 70"));
		serviceMoveLeft.setQualityParameters(qosServiceMoveLeft);
		
		String insertQueryServiceMoveLeft = prepareInsertServiceQuery(deviceName, serviceMoveLeft);
		
		//prepare insert services
		//moveRight
		ServiceDTO serviceMoveRight = new ServiceDTO(deviceAasIdentifier, UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/move_right", EnumServiceMethod.POST.name(), true, "Service_MoveRight", "Moves feed tray to far right");

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
		qosServiceMoveRight.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "NetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceMoveRight.add(new QualityParameterDTO("Humidity", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Humidity <= 50"));
		qosServiceMoveRight.add(new QualityParameterDTO("Temperature", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Temperature <= 30"));
		qosServiceMoveRight.add(new QualityParameterDTO("Weight", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Weight <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("Size", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Size <= 10"));
		qosServiceMoveRight.add(new QualityParameterDTO("BatteryLevel", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BaterryLevel >= 65"));
		qosServiceMoveRight.add(new QualityParameterDTO("DistanceAwayFromWorkStation", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "DistanceAwayFromWorkStation <= 70"));
		serviceMoveRight.setQualityParameters(qosServiceMoveRight);
		
		String insertQueryServiceMoveRight = prepareInsertServiceQuery(deviceName, serviceMoveRight);
		
		//prepare insert services
		//throwPiece
		ServiceDTO serviceThrowPiece = new ServiceDTO(deviceAasIdentifier, UUID.randomUUID().toString(), "http://" + deviceIpAddress + ":80/robot/throw_piece", EnumServiceMethod.POST.name(), true, "Service_ThrowPiece", "Throws current piece out of the feed tray");

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
		qosServiceThrowPiece.add(new QualityParameterDTO("AvgNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "NetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("LastNetworkLatency", EnumQualityType.DEVICE.name(), "Integer", String.valueOf(Tools.GetRandomNumber(10, 500)), "LastNetworkLatency <= 300"));
		qosServiceThrowPiece.add(new QualityParameterDTO("Humidity", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Humidity <= 50"));
		qosServiceThrowPiece.add(new QualityParameterDTO("Temperature", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "Temperature <= 30"));
		qosServiceThrowPiece.add(new QualityParameterDTO("Weight", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Weight <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("Size", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 20)), "Size <= 10"));
		qosServiceThrowPiece.add(new QualityParameterDTO("BatteryLevel", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "BaterryLevel >= 65"));
		qosServiceThrowPiece.add(new QualityParameterDTO("DistanceAwayFromWorkStation", EnumQualityType.DEVICE.name(), "Decimal", String.valueOf(Tools.GetRandomNumber(1, 100)), "DistanceAwayFromWorkStation <= 70"));
		serviceThrowPiece.setQualityParameters(qosServiceThrowPiece);
		
		String insertQueryServiceThrowPiece = prepareInsertServiceQuery(deviceName, serviceThrowPiece);

		//execute insert
		String allInserts = insertDeviceQuery + insertSensorQuery + insertQueryServiceMotorStatus + insertQueryServiceMoveLeft + insertQueryServiceMoveRight + insertQueryServiceThrowPiece;
		boolean resultInsertAllAtOnce = repManager.executeInsert(Tools.REPOSITORY_ID, allInserts);
		System.out.println(String.format("Insert all properties at once: %s", resultInsertAllAtOnce));
	}
	
	private static String prepareInsertDeviceQuery(String deviceName, String deviceAasIdentifier, String deviceIpAddress, String deviceDescription, String deviceNetworkLatency, 
			String deviceApiDocumentationAddress) {
		String insertQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + deviceName + " rdf:type dsOnt:Device ."
				+ "    dsOnt:" + deviceName + " dsOnt:deviceName \"" + deviceName + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:deviceIPAddress \"" + deviceIpAddress + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:deviceDescription \"" + deviceDescription + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:deviceNetworkLatency \"" + deviceNetworkLatency + "\" ."
				+ "    dsOnt:" + deviceName + "_ApiDocumentation rdf:type dsOnt:ApiDocumentation ."
				+ "    dsOnt:" + deviceName + "_ApiDocumentation dsOnt:deviceApiDocumentation \"" + deviceApiDocumentationAddress + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:hasApiDocumentation dsOnt:" + deviceName + "_ApiDocumentation ."
				+ "    dsOnt:" + deviceName + "_AasIdentifier rdf:type dsOnt:AasIdentifier ."
				+ "    dsOnt:" + deviceName + "_AasIdentifier dsOnt:deviceAasIdentifier \"" + deviceAasIdentifier + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:hasAasIdentifier dsOnt:" + deviceName + "_AasIdentifier"
				+ "};";
		return insertQuery;
	}
	
	private static String prepareInsertSensorQuery(String deviceName, String sensorName, String sensorIdentifier, String sensorDescription, EnumSensorType sensorType, 
			String sensorValueUnit, String sensorValueDataType, String sensorValueDataValue) {		
		String fullSensorName = String.format("%s_%s", deviceName, sensorName);
		
		String insertQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullSensorName + " rdf:type dsOnt:Sensor ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorIdentifier \"" + sensorIdentifier + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorName \"" + sensorName + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorDescription \"" + sensorDescription + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorType dsOnt:" + sensorType.name() + " ."
				+ "    dsOnt:" + fullSensorName + "_SensorValue rdf:type dsOnt:SensorValue ."
				+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueUnit \"" + sensorValueUnit + "\" ."
				+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueDataType \"" + sensorValueDataType + "\" ."
				+ "    dsOnt:" + fullSensorName + "_SensorValue dsOnt:sensorValueDataValue \"" + sensorValueDataValue + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:hasSensorValue dsOnt:" + fullSensorName + "_SensorValue ."
				+ "    dsOnt:" + deviceName + " dsOnt:hasSensor dsOnt:" + fullSensorName
				+ "};";
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
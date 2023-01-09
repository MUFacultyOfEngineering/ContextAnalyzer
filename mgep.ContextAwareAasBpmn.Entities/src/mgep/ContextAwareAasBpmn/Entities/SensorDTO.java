package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

import mgep.ContextAwareAasBpmn.Enums.EnumSensorType;

public class SensorDTO extends DeviceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String sensorDescription;
	private String sensorIdentifier;
	private String sensorName;
	private EnumSensorType sensorType;
	private String sensorValueDataType;
	private String sensorValueDataValue;
	private String sensorValueDataUnit;

	public SensorDTO() {
		super();
	}

	public String getSensorDescription() {
		return sensorDescription;
	}

	public void setSensorDescription(String sensorDescription) {
		this.sensorDescription = sensorDescription;
	}

	public String getSensorIdentifier() {
		return sensorIdentifier;
	}

	public void setSensorIdentifier(String sensorIdentifier) {
		this.sensorIdentifier = sensorIdentifier;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public EnumSensorType getSensorType() {
		return sensorType;
	}

	public void setSensorType(EnumSensorType sensorType) {
		this.sensorType = sensorType;
	}

	public String getSensorValueDataType() {
		return sensorValueDataType;
	}

	public void setSensorValueDataType(String sensorValueDataType) {
		this.sensorValueDataType = sensorValueDataType;
	}

	public String getSensorValueDataValue() {
		return sensorValueDataValue;
	}

	public void setSensorValueDataValue(String sensorValueDataValue) {
		this.sensorValueDataValue = sensorValueDataValue;
	}

	public String getSensorValueDataUnit() {
		return sensorValueDataUnit;
	}

	public void setSensorValueDataUnit(String sensorValueDataUnit) {
		this.sensorValueDataUnit = sensorValueDataUnit;
	}
	
}

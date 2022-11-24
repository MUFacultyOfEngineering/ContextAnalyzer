package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

import mgep.ContextAwareAasBpmn.Enums.EnumSensorType;

public class SensorDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String description;
	private String name;
	private String identifier;
	private EnumSensorType type;
	private String dataType;
	private String dataValue;
	private String dataUnit;

	public SensorDTO() {
		super();
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EnumSensorType getType() {
		return type;
	}
	public void setType(EnumSensorType type) {
		this.type = type;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	public String getDataUnit() {
		return dataUnit;
	}
	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
}

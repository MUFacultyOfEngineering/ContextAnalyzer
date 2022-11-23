package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class ServiceParameterDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public ServiceParameterDTO(String name, String type, String value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}
}

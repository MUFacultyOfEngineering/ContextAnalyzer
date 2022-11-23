package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class AssetAdminShellDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String Identifier;
	private String Name;
	
	public String getIdentifier() {
		return Identifier;
	}
	public void setIdentifier(String identifier) {
		Identifier = identifier;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	public AssetAdminShellDTO(String identifier, String name) {
		this.Identifier=identifier;
		this.Name=name;
	}
}

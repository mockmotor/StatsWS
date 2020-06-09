package stats;

import java.util.ArrayList;
import java.util.List;

public class OSBService {
	
	private String name;
	private String serviceType;
	private List <OSBResource> resource;
	
	public OSBService() {
	}
	
	public OSBService(String name, ArrayList<OSBResource> resource) {
		this.name=name;
		this.resource = resource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public List<OSBResource> getOSBResources() {
		return resource;
	}

	public void setOSBResource(List<OSBResource> resource) {
		this.resource = resource;
	}
}

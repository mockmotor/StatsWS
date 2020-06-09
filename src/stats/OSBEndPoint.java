package stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement(name = "Service")
//@XmlType(name = "EndPoint")
@XmlAccessorType(XmlAccessType.FIELD)

public class OSBEndPoint {
	
	@XmlAttribute(name="BusinessServiceName")
	private String businessServiceName;
	@XmlAttribute(name="BackendEndPoint")
	private String backendEndPoint;
	@XmlAttribute(name="BackendName")
	private String backendName;
	
	public OSBEndPoint() {
	}
	
	public OSBEndPoint(String businessServiceName, String backendEndPoint, String backendName) {
		this.businessServiceName=businessServiceName;
		this.backendEndPoint = backendEndPoint;
		this.backendName = backendName;
		
	}

	public String getBusinessServiceName() {
		return businessServiceName;
	}

	public void setBusinessServiceName(String businessServiceName) {
		this.businessServiceName = businessServiceName;
	}
	
	public String getBackendEndPoint() {
		return backendEndPoint;
	}

	public void setBackendEndPoint(String backendEndPoint) {
		this.backendEndPoint = backendEndPoint;
	}

	public String getBackendName() {
		return backendName;
	}

	public void setBackendName(String backendName) {
		this.backendName = backendName;
	}
}

package stats;

import java.util.List;

public class OSBResource {
	
	private String name;
	private String type;
	private List<OSBStat> stat;
	
	public OSBResource() {
	}
	
	public OSBResource(String name, String type, List<OSBStat> stat) {
		this.name=name;
		this.type = type;
		this.stat = stat;
		
	}

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

	public List<OSBStat> getOSBStat() {
		return stat;
	}

	public void setOSBStat(List<OSBStat> stat) {
		this.stat = stat;
	}

	@Override
	public String toString() {
		return "OSBResource [name=" + name + ", type=" + type + ", stat="
				+ stat + "]";
	}
}

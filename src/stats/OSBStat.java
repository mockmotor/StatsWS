package stats;

import java.util.List;

public class OSBStat {
	
	private String name;
	private String type;
	private List<OSBValue> value;
	
	public OSBStat() {
	}
	
	public OSBStat(String name, String type, List<OSBValue> value) {
		this.name=name;
		this.type = type;
		this.value = value;
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

	public List<OSBValue> getOSBValue() {
		return value;
	}

	public void setOSBValue(List<OSBValue> value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "OSBStat [" + name + "," + type + "," + value + "]";
	}
}

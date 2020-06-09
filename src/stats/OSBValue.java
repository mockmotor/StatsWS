package stats;

public class OSBValue {
	
	private String name;
	private String value;
	
	public OSBValue() {
	}
	
	public OSBValue(String name, String value) {
		this.name=name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}
}


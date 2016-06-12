package models;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class MeterForm {

	public String meterName;
	
	public String description;
	
	public String getMeterName() {
		return meterName;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
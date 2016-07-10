package models;

import javax.persistence.MappedSuperclass;

import play.data.validation.Constraints;

@MappedSuperclass
public class DayTypeForm {

	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String dayType;

	public String getDayType() {
		return dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
}
package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;

@Entity
@Table(name = "DayType")
public class DayType extends Model{

	@Id
	public Long id;
	
	public String dayType; 
	
	public Boolean isSelected;
	
	@ManyToOne
	public Meter meter;
	
	public DayType(String dayType) {
		this.dayType = dayType;
		this.isSelected = true;
	}
	
}
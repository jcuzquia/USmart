package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import play.data.validation.Constraints;

@Entity
@Table(name = "DayType")
public class DayType extends Model{
	

	public static final Finder<Long, DayType> find = new Model.Finder<Long, DayType>(DayType.class);
	@Id
	public Long id;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String dayType; 
	
	public Boolean isSelected;
	
	@ManyToOne
	public Meter meter;
	
	public DayType(String dayType) {
		this.dayType = dayType;
		this.isSelected = true;
	}

	public static DayType create(String daytype) {
		DayType dayType = new DayType(daytype);
		return dayType;
	}
	
	public static DayType findById(Long id){
		DayType dayType = find.byId(id);
		return dayType;
	}
	
}
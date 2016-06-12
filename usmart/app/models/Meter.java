package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.format.Formats;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;

@Entity
@Table(name = "meter")
public class Meter extends Model {

	@Id
	public Long id;
	
	@ManyToOne
	public Project project;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String meterName;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String description;
	
	public Double maxKWh, minKWh, maxKW, minKW;
	
	public Integer startYear, endYear, startMonth, endMonth, startDay, endDay;
	

	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date endDate;
	
	public Long startDateValue, endDateValue;
	
	public String jsStartDate, jsEndDate;
	
	@OneToMany(mappedBy = "meter", cascade=CascadeType.ALL)
	public List<Data> dataList = new ArrayList<Data>();
	
	@OneToMany(mappedBy = "meter", cascade=CascadeType.ALL)
	public List<DayType> dayTypeList = new ArrayList<DayType>();
	
	public static final Finder<Long, Meter> find = new Model.Finder<Long, Meter>(Meter.class);

	/**
	 * You can only create a meter once a list of data points has been 
	 * inserted
	 * @param dataList, project
	 */
	public Meter(List<Data> dataList, Project project){
		this.dataList = dataList;
		this.project = project;
		
		
	}
	
	/**
	 * Method that finds the Maximum, min kWh Value
	 * @param dataList
	 * @return
	 */
	private void findMeterItems(List<Data> dataList) {
		
		double maxKWh = dataList.get(0).getKWh();
		double minKWh = dataList.get(0).getKWh();
		DayType dayType = new DayType(dataList.get(0).getDayType());
		dayTypeList = new ArrayList<DayType>();
		
		dayTypeList.add(dayType);
		
		for(int i = 1; i < dataList.size(); i++){
			String dt = dataList.get(i).getDayType();
			if(dataList.get(i).getKWh() > maxKWh){
				maxKWh = dataList.get(i).getKWh();
			}
			if(dataList.get(i).getKWh() < minKWh){
				minKWh = dataList.get(i).getKWh();
			}
			
			if(dayTypeList.size() > 100){
				break;
			}
			
			if(!containsDayType(dt)){
				dayTypeList.add(new DayType(dt));
			}
			
			containsDayType(dt);
			
		}
		this.maxKWh = maxKWh;
		this.minKWh = minKWh;
		this.dayTypeList = new ArrayList<>(dayTypeList);
	}
	
	private boolean containsDayType(String dt) {
		for (DayType dT : dayTypeList ){
			if(dT.dayType.equals(dt)){
				return true;
			}
		}
		return false;
		
	}

	public static Meter findById(Long id){
		System.out.println("finding by Id: " + id);
		return find.byId(id);
	}
	
	@Override
	public String toString() {
		return "Meter [id=" + id + ", meterName=" + meterName + ", description=" + description + "]";
	}
	
	public List<Data> getDataList() {
		return dataList;
	}
	
	public List<TimeSeriesData> getTimeSeriesData(){
		// get number of intervals between a day
		int hour = 0;
		int minute = 0;
		List<TimeSeriesData> tsdList = new ArrayList<TimeSeriesData>();
		
		for (int i = 0; i < 96; i++) {

			TimeSeriesData tsd = new TimeSeriesData(hour, minute);
			List<Data> dataList = new ArrayList<Data>(); 
			dataList = searchListWithTime(hour, minute);
			tsd.setTimeData(dataList);
			System.out.println("We are adding hour: " + hour + " minute: " + minute);
			tsdList.add(tsd);
			if (minute == 0 && hour < 24) {
				minute += 15;
			} else if (minute >= 45 && hour < 24) {
				minute = 0;
				hour++;
			} else {
				minute += 15;
			}
		}
		
		
//		System.out.println("Finished loop with ending result of this size: " + tsdList.size() );
//		System.out.println("************************************************");
		return tsdList;
	}

	private List<Data> searchListWithTime(int hour, int minute) {
		List<Data> dl = new ArrayList<Data>();
		for(Data data : dataList){
			if (data.getDate().getHours() == hour && data.getDate().getMinutes() == minute){
				dl.add(data);
			}
		}
		
		return dl;
	}

	/**
	 * This is called from the project controller. It is actually the initializer of the
	 * meter object
	 * @param dataList
	 */
	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
		// Set start and end date of the meter
		startDate = this.dataList.get(0).getDate();
		endDate = this.dataList.get(this.dataList.size()-1).getDate();
		startDateValue = startDate.getTime();
		endDateValue = endDate.getTime();

		//get the string format for javascript template
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		startYear = cal.get(Calendar.YEAR);
		startMonth = cal.get(Calendar.MONTH);
		startDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(endDate);
		endYear = cal.get(Calendar.YEAR);
		endMonth = cal.get(Calendar.MONTH);
		endDay = cal.get(Calendar.DAY_OF_MONTH);
		System.out.println(this.dataList.size());
		findMeterItems(this.dataList);
		
	}

	public void activateDayType(String dayType) {
		for(DayType dt : dayTypeList){
			if(dt.dayType.equals(dayType)){
				dt.isSelected = !dt.isSelected;
			} 
		}
	}
	
}
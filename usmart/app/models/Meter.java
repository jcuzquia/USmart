package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.libs.Json;

import com.avaje.ebean.Model;

@Entity
@Table(name = "meter")
public class Meter extends Model {

	@Id
	public Long id;

	@ManyToOne
	public Project project;

	@OneToOne
	public String path;

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

	public static final String HEATMAP_HEADER = "Date,Time,kWh";

	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date startDate;

	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date endDate;

	public Long startDateValue, endDateValue;

	public String jsStartDate, jsEndDate;

	public DataContainer dataContainer;

	@OneToMany(mappedBy = "meter", cascade = CascadeType.REMOVE)
	public List<DayType> dayTypeList = new ArrayList<DayType>();

	public static final Finder<Long, Meter> find = new Model.Finder<Long, Meter>(Meter.class);

	/**
	 * You can only create a meter once a list of data points has been inserted
	 * 
	 * @param dataList,
	 *            project
	 */
	public Meter(DataContainer dataContainer, Project project) {
		this.dataContainer = dataContainer;
		this.project = project;

	}

	/**
	 * Method that finds the Maximum, min kWh Value
	 * 
	 * @param dataList
	 * @return
	 */
	private void findMeterItems(List<Data> dataList) {

		double maxKWh = dataList.get(0).getKWh();
		double minKWh = dataList.get(0).getKWh();
		DayType dayType = new DayType(dataList.get(0).getDayType());
		dayTypeList = new ArrayList<DayType>();

		dayTypeList.add(dayType);

		for (int i = 1; i < dataList.size(); i++) {
			String dt = dataList.get(i).getDayType();
			if (dataList.get(i).getKWh() > maxKWh) {
				maxKWh = dataList.get(i).getKWh();
			}
			if (dataList.get(i).getKWh() < minKWh) {
				minKWh = dataList.get(i).getKWh();
			}

			if (dayTypeList.size() > 100) {
				break;
			}

			if (!containsDayType(dt)) {
				dayTypeList.add(new DayType(dt));
			}

			containsDayType(dt);

		}
		this.maxKWh = maxKWh;
		this.minKWh = minKWh;
		this.dayTypeList = new ArrayList<>(dayTypeList);
	}

	private boolean containsDayType(String dt) {
		for (DayType dT : dayTypeList) {
			if (dT.dayType.equals(dt)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Simple method that finds the meter but doesn't load the data
	 * @param id
	 * @return
	 */
	public static Meter findById(Long id) {
		Meter meter = find.byId(id);
		return meter;
	}
	
	/**
	 * Method that finds the meter and loads it with the data
	 * @param id
	 * @return
	 */
	public static Meter loadMeter(Long id) {
		Logger.info("loadMeter() at models.Meter");
		Meter meter = find.byId(id);
		meter.dataContainer = loadDataContainer(meter.path);
		if(meter.path == null) {
			Logger.error("There is an error loading meter models.Meter.loadMeter(). No path found");
			return null;
		}
		
		return meter;
	}

	/**
	 * Getting called from the Project controller when showing meter
	 * Simply loads the DataContainer object containing all the data for the meter
	 * @param path
	 * @return
	 */
	public static DataContainer loadDataContainer(String path) {
		Logger.info("Loading data containerin models.Meter");
		DataContainer dataContainer = null;
		try{
			FileInputStream fileIn = new FileInputStream(path);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        dataContainer = (DataContainer) in.readObject();
	        in.close();
	        fileIn.close();
	        if(dataContainer.getDataList().isEmpty()){
	        	Logger.error("The dataList in the container is empty ");
	        	return null;
	        } else {
	        	Logger.info("Returning dataContainer with data size of: " + dataContainer.getDataList().size());
	        	return dataContainer;
	        }
	        
		} catch(IOException i){
			i.printStackTrace();
			return null;
		} catch(ClassNotFoundException c){
			Logger.error("Unable to find the data Container");
			c.printStackTrace();
			return null;
		}
		 
	}

	@Override
	public String toString() {
		return "Meter [id=" + id + ", meterName=" + meterName + ", description=" + description + "]";
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	public List<TimeSeriesData> getTimeSeriesData() {
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

		Logger.info("Finished loop with ending result of this size: " + tsdList.size() );
		return tsdList;
	}

	private List<Data> searchListWithTime(int hour, int minute) {
		List<Data> dl = new ArrayList<Data>();
		for (Data data : dataContainer.getDataList()) {
			if (data.getDate().getHours() == hour && data.getDate().getMinutes() == minute) {
				dl.add(data);
			}
		}

		return dl;
	}

	/**
	 * This is called from the project controller. It is actually the
	 * initializer of the meter object
	 * 
	 * @param dataList
	 */
	public void setDataList(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
		List<Data> dataList = dataContainer.getDataList();
		
		// Set start and end date of the meter
		startDate = dataList.get(0).getDate();
		endDate = dataList.get(dataList.size() - 1).getDate();
		startDateValue = startDate.getTime();
		endDateValue = endDate.getTime();

		// get the string format for javascript template
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		startYear = cal.get(Calendar.YEAR);
		startMonth = cal.get(Calendar.MONTH);
		startDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(endDate);
		endYear = cal.get(Calendar.YEAR);
		endMonth = cal.get(Calendar.MONTH);
		endDay = cal.get(Calendar.DAY_OF_MONTH);
		System.out.println(dataList.size());
		findMeterItems(dataList);

	}

	public List<String> getHeatMapData() {
		List<String> heatMapData = new ArrayList<String>();
		heatMapData.add(HEATMAP_HEADER);
		Logger.info("Calling the getHeatMapData at models.Meter and starting the main loop");

		for (Data data : this.dataContainer.getDataList()) {
			String dataLine = data.getDateString() + "," + data.getTime() + "," + data.getKWh();
			heatMapData.add(dataLine);
		}
		Logger.info("Completed loading the HeatMapData at meter.getheatMapData() with size: " + heatMapData.size());
		return heatMapData;
	}

	public void activateDayType(String dayType) {
		for (DayType dt : dayTypeList) {
			if (dt.dayType.equals(dayType)) {
				dt.isSelected = !dt.isSelected;
			}
		}
	}
	
	public void setPath(String path){
		this.path = path;
	}

}
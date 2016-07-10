package models;

import java.io.File;
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

	@OneToMany(mappedBy = "meter", cascade = CascadeType.ALL)
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
		Logger.info("Calling meter contructor. Size of DayTypeList is: " + this.dayTypeList.size());
	}

	private List<DayType> initializeDayTypeList(List<DailyData> dailyDataList) {
		List<DayType> dayTypeList = new ArrayList<>();
		
		dayTypeList.add(new DayType(dailyDataList.get(0).getDayType()));
		for (DailyData dailyData : dailyDataList){
			String dayType = dailyData.getDayType();
			if(!containsDayType(dayType, dayTypeList)){
				dayTypeList.add(new DayType(dayType));
			}
		}
		return dayTypeList;
		
	}
	
	private boolean containsDayType(String dt, List<DayType> dayTypeList) {
		for (DayType dT : dayTypeList) {
			if (dT.dayType.equals(dt)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Method that finds the Maximum, min kWh Value
	 * 
	 * @param dataList
	 * @return
	 */
	public void findMeterItems(List<Data> dataList) {

		double maxKWh = dataList.get(0).getKWh();
		double minKWh = dataList.get(0).getKWh();

		for (int i = 1; i < dataList.size(); i++) {
			if (dataList.get(i).getKWh() > maxKWh) {
				maxKWh = dataList.get(i).getKWh();
			}
			if (dataList.get(i).getKWh() < minKWh) {
				minKWh = dataList.get(i).getKWh();
			}


		}
		this.maxKWh = maxKWh;
		this.minKWh = minKWh;
		Logger.info("Set up maxkWh = " + this.maxKWh + " minkWh = " + this.minKWh + " dayTypeListSize = "
				+ dayTypeList.size());

	}


	/**
	 * Simple method that finds the meter but doesn't load the data
	 * 
	 * @param id
	 * @return
	 */
	public static Meter findById(Long id) {
		Meter meter = find.byId(id);
		return meter;
	}

	/**
	 * Method that finds the meter and loads it with the data
	 * 
	 * @param id
	 * @return
	 */
	public static Meter loadMeter(Long id) {
		Logger.info("loadMeter() at models.Meter");
		Meter meter = find.byId(id);
		meter.dataContainer = loadDataContainer(meter.path);

		meter.findMeterItems(meter.dataContainer.getDataList());

		if (meter.path == null) {
			Logger.error("There is an error loading meter models.Meter.loadMeter(). No path found");
			return null;
		}

		return meter;
	}

	/**
	 * Getting called from the Project controller when showing meter Simply
	 * loads the DataContainer object containing all the data for the meter
	 * 
	 * @param path
	 * @return
	 */
	public static DataContainer loadDataContainer(String path) {
		Logger.info("Loading data containerin models.Meter");
		DataContainer dataContainer = null;
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			dataContainer = (DataContainer) in.readObject();
			in.close();
			fileIn.close();
			if (dataContainer.getDataList().isEmpty()) {
				Logger.error("The dataList in the container is empty ");
				return null;
			} else {
				Logger.info("Returning dataContainer with data size of: " + dataContainer.getDataList().size());
				Logger.info("Returning dataContainer with TimeSeriesData size of: "
						+ dataContainer.getTimeSeriesDataList().size());
				return dataContainer;
			}

		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
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

	/**
	 * This is called from the project controller. It is actually the
	 * initializer of the meter object
	 * 
	 * @param dataList
	 */
	public void setDataList(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
		List<Data> dataList = dataContainer.getDataList();
		
		// initialize the dayTypeList
		this.dayTypeList = initializeDayTypeList(dataContainer.getDailyDataList());

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
		findMeterItems(dataList);

	}

	/**
	 * Method that is called when the heat map is called.
	 * 
	 * @return
	 */
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

	public void setPath(String path) {
		this.path = path;
	}


	/**
	 * Method to delete the meter file
	 */
	public void deleteMeterFile() {
		try {

			File file = new File(path);

			if (file.delete()) {
				Logger.info("File deleted: " + path);
			} else {
				Logger.error("There was an error deleting the meter file");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}
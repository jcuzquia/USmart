package models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import constants.Const;
import javafx.scene.control.ComboBox;
/**
 * This class gathers compiles all the data that is contained on a daily basis
 *
 * @author Jose Camilo Uzquiano
 */
public class DailyData implements Serializable{

	private static final long serialVersionUID = 1L;
	private double temperature;
	private List<Data> dailyIntervalList;
	private Date date;
	private long dateValue;
	private int dayOfWeek;
	private String dayType;
	private float totalDailykWh;
	// This is what is parsed in the table of the TimeSeries Page
	private String dateString;
	
	
	/**
	 * Constructor
	 * @param dailyIntervalList
	 * @param dateValue
	 */
	public DailyData(List<Data> dailyIntervalList, Long dateValue) {
		this.dailyIntervalList = dailyIntervalList;
		this.dateValue = dateValue;
		this.date = new Date(dateValue);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			dayType = Const.WEEKEND;
		} else {
			dayType = Const.WORKDAY;
		}
		
		this.dateString = generateDateString(this.date);
		this.totalDailykWh = getTotalDailyConsumption(this.dailyIntervalList);
	}
	
	
	private String generateDateString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
		String dateString = formatter.format(date);
		return dateString;
	}


	/**
	 * Simple method that adds up the total kWh for each day
	 * @param dailyIntervalList
	 * @return totalDailyConsumption
	 */
	private float getTotalDailyConsumption(List<Data> dailyIntervalList) {
		float totalDailyConsumption = 0;
		for(Data data : dailyIntervalList){
			totalDailyConsumption = totalDailyConsumption + data.getKWh() - data.getGenkWh();
		}
		return totalDailyConsumption;
	}

	public List<Data> getDailyIntervalList() {
		return dailyIntervalList;
	}
	public void setDailyIntervalList(List<Data> dailyIntervalList) {
		this.dailyIntervalList = dailyIntervalList;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public long getDateValue() {
		return dateValue;
	}
	public void setDateValue(long dateValue) {
		this.dateValue = dateValue;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getDayType() {
		return dayType;
	}
	public void setDayType(String dayType) {
		this.dayType = dayType;
	}
	public float getTotalDailykWh() {
		return totalDailykWh;
	}


	public String getDateString() {
		return dateString;
	}


	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
}
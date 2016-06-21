package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimeSeriesData implements Serializable {
	
	private static final long serialVersionUID = -4011452334923553641L;
	private int hour; 
	private int minute;
	
	private List<Data> timeData = new ArrayList<Data>();
	private String csvString;
	
	public TimeSeriesData(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		this.csvString = generateCSVString();
	}
	
	public String generateCSVString(){
		String s = hour + ":" + minute + ",";
		for (Data data : timeData){
			s = s + data.getKWh() + ",";
		}
		s+='\n';
		return s;
	}
	
	

	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}


	public List<Data> getTimeData() {
		return timeData;
	}


	public void setTimeData(List<Data> timeData) {
		this.timeData = timeData;
	}

	public String getCsvString() {
		return csvString;
	}

	public void setCsvString(String csvString) {
		this.csvString = csvString;
	}
	
	
	
	

	
	
	
	

}
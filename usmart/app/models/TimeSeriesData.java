package models;

import java.util.ArrayList;
import java.util.List;

public class TimeSeriesData {
	
	private int hour; 
	private int minute;
	
	private List<Data> timeData = new ArrayList<Data>();
	
	public TimeSeriesData(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public String getCSVString(){
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
	
	
	
	

	
	
	
	

}
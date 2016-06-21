package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Model;

import play.Logger;

public class DataContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<Data> dataList = new ArrayList<Data>();
	
	private List<TimeSeriesData> timeSeriesDataList = new ArrayList<TimeSeriesData>();
	private List<String> timeSeriesCSV = new ArrayList<String>();
	private List<String> dailyString = new ArrayList<String>();
	
	public DataContainer(List<Data> dataList) {
		Logger.info("Calling the Constructor of DataContainer");
		this.dataList = dataList;
		this.timeSeriesDataList = generateTimeSeriesDataList();
		this.timeSeriesCSV = generateTimeSeriesCSV();
		this.dailyString = generateDailyString();
		
	}
	
	public List<String> generateDailyString(){
		TimeSeriesData tsdl = timeSeriesDataList.get(0);
		List<String> dateList = new ArrayList<String>();
		for(Data data : tsdl.getTimeData()){
			dateList.add(data.getDateString());
		}
		return dateList;
	}
	
	private List<TimeSeriesData> generateTimeSeriesDataList() {
		// get number of intervals between a day
		int hour = 0;
		int minute = 0;
		List<TimeSeriesData> tsdList = new ArrayList<TimeSeriesData>();

		for (int i = 0; i < 96; i++) {

			TimeSeriesData tsd = new TimeSeriesData(hour, minute);
			List<Data> dataList = new ArrayList<Data>();
			dataList = searchListWithTime(hour, minute);
			tsd.setTimeData(dataList);
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
	
	private List<String> generateTimeSeriesCSV(){
		List<TimeSeriesData> tsdList = new ArrayList<TimeSeriesData>(timeSeriesDataList); 
		List<String> csv= new ArrayList<String>();
		String firstLine = "Categories";
		TimeSeriesData f = tsdList.get(0);
		
		for (Data d: f.getTimeData()){
			firstLine += "," + d.getDayType();
		}
		firstLine += "\n";
		
		csv.add(firstLine);
		
		for(TimeSeriesData tsd : tsdList){
			csv.add(tsd.generateCSVString());
		}
		return csv;
	}
	
	private List<Data> searchListWithTime(int hour, int minute) {
		List<Data> dl = new ArrayList<Data>();
		for (Data data : dataList) {
			if (data.getDate().getHours() == hour && data.getDate().getMinutes() == minute) {
				dl.add(data);
			}
		}

		return dl;
	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

	public List<TimeSeriesData> getTimeSeriesDataList() {
		return timeSeriesDataList;
	}

	public void setTimeSeriesDataList(List<TimeSeriesData> timeSeriesDataList) {
		this.timeSeriesDataList = timeSeriesDataList;
	}

	public List<String> getTimeSeriesCSV() {
		return timeSeriesCSV;
	}

	public void setTimeSeriesCSV(List<String> timeSeriesCSV) {
		this.timeSeriesCSV = timeSeriesCSV;
	}

	public List<String> getDailyString() {
		return dailyString;
	}

	public void setDailyString(List<String> dailyString) {
		this.dailyString = dailyString;
	}
	
}

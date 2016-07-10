package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Model;

import play.Logger;

public class DataContainer implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<Data> dataList = new ArrayList<Data>();
	
	private List<TimeSeriesData> timeSeriesDataList = new ArrayList<TimeSeriesData>();
	// this is the actual file that is being parsed as data
	private List<String> timeSeriesCSV = new ArrayList<String>();
	private List<String> dailyString = new ArrayList<String>();
	private List<DailyData> dailyDataList = new ArrayList<DailyData>();
	
	public DataContainer(List<Data> dataList) {
		Logger.info("Calling the Constructor of DataContainer");
		this.dataList = dataList;
		this.timeSeriesDataList = generateTimeSeriesDataList();
		this.timeSeriesCSV = generateTimeSeriesCSV();
		this.dailyString = generateDailyString();
		this.dailyDataList = generateDailyDataList(dataList);
		
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
	
	/**
	 * This method creates the csv list that is sent out to the actual page
	 * @return
	 */
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
	
	/**
	 * This will give the daily data that containes a set of all the values
	 * during the day
	 * @param dataList
	 * @return
	 */
	public List<DailyData> generateDailyDataList(List<Data> dataList) {
		List<DailyData> dayTimeIntervalData = new ArrayList<>();
		List<Data> deletionDataList = new ArrayList<Data>(dataList);

		Data firstData = deletionDataList.get(0);
		deletionDataList.remove(0);
		Data secondData = deletionDataList.get(0);
		
		/*
		 * Loop until the deletion list has no items in it
		 * This means that the overall transfer is complete
		 */
		while (deletionDataList.size() > 0) {

			//create the daily container
			List<Data> dayTimeData = new ArrayList<Data>();
			
			//while they are the same day keep adding to this specific day container
			while (firstData.getDate().getDate() == secondData.getDate().getDate()) { 

				firstData.setkW(firstData.getKWh() * 4);
				firstData.setGenkW(firstData.getGenkWh() * 4);

				dayTimeData.add(firstData);
				firstData = deletionDataList.get(0); // we get the first data
				deletionDataList.remove(0);
				secondData = deletionDataList.get(0);
				if(deletionDataList.size() == 1){
					break;
				}
			}
			
			dayTimeData.add(firstData);
			firstData = deletionDataList.get(0);
			deletionDataList.remove(0);
			
			if(deletionDataList.size() == 0 ){
				break;
			}
			secondData = deletionDataList.get(0);
			if (dayTimeData.size() != 96) {
				dayTimeData = fixDayTimeData(dayTimeData);
			}
			dayTimeIntervalData.add(new DailyData(dayTimeData, dayTimeData.get(0).getDateValue()));
		}
		
		return dayTimeIntervalData;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * This method returns 96 items per list to build the matrix of the heat map. 
	 * It checks only for repeated hours and minutes for each specific day, if the daily list
	 * is not equal to 96 items. This is mostly done for daylight savings
	 * @param dayTimeData
	 * @return
	 */
	private List<Data> fixDayTimeData(List<Data> dayTimeData) {
		
		java.util.Collections.sort(dayTimeData);
		
		for (int i = 0; i < dayTimeData.size()-2; i++){
			Date date1 = new Date(dayTimeData.get(i).getDateValue());
			
			int h1 = date1.getHours();
			int m1 = date1.getMinutes();
			for(int j = i+1; j < dayTimeData.size()-1; j++){
				Date date2 = new Date(dayTimeData.get(j).getDateValue());
				int h2 = date2.getHours();
				int m2 = date2.getMinutes();
				
				if(h1 == h2 && m1 == m2){ //check if the minute and hour are equal for each item
					dayTimeData.get(i).setKWh(dayTimeData.get(j).getKWh());
					dayTimeData.get(i).setkW(dayTimeData.get(j).getkW());
					dayTimeData.get(i).setGenkWh(dayTimeData.get(j).getGenkWh());
					dayTimeData.get(i).setGenkW(dayTimeData.get(j).getGenkW());
					dayTimeData.remove(j);
				}
				
			}
		}
		
		return dayTimeData;
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

	public List<DailyData> getDailyDataList() {
		return dailyDataList;
	}

	public void setDailyDataList(List<DailyData> dailyDataList) {
		this.dailyDataList = dailyDataList;
	}
	
}

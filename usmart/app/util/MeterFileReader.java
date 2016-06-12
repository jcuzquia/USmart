package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import constants.Const;
import models.Data;

/**
 * Object that reads cvs files
 * @author Jose Camilo Uzquiano
 *
 */
public class MeterFileReader {

	public MeterFileReader() {
	}
	
	public static List<Data> getDataListFromFile(File file){
		List<Data> dataList = new ArrayList<Data>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			// //////////reading the first line//////////
			bufferedReader.readLine();
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				Data data = createData(line);
				dataList.add(data);
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(dataList);

		dataList = deleteRepeatedDates(dataList);
		dataList = FillUpMissingData(dataList);
		dataList = trimToNearestDay(dataList);
		
		return dataList;
	}
	
	
	/**
	 * Method that deletes repeated dates in the data
	 * @param dataList
	 * @return
	 */
	private static List<Data> deleteRepeatedDates(List<Data> dataList) {
		//create 2 containers, one one to dump filtered data
		// the other to delete
		List<Data> deleteDataList = new ArrayList<Data>(dataList);
		List<Data> toAddList = new ArrayList<>();
		
		toAddList.add(deleteDataList.get(0)); // set up the lists
		deleteDataList.remove(0);
		
		while (deleteDataList.size()> 1){
			//Get the last one of the List
			Data data1 = toAddList.get(toAddList.size()-1);
			Data data2 = deleteDataList.get(0); //get the first of this list
			// if they are the same date
			if (data1.getDateValue() == data2.getDateValue()) {
				if (data1.getKWh() == data2.getKWh() || data1.getGenkWh() == data2.getGenkWh()){
					float con1 = data1.getKWh();
					float gen1 = data1.getGenkWh();
					
					float con2 = data2.getKWh();
					float gen2 = data2.getGenkWh();
					if(con1 != con2 || gen1 != gen2){
						data1.setGenkWh(gen1+gen2);
						data1.setKWh(con1+con2);
					}
					
				} 
				
				deleteDataList.remove(0);
			} else {
				toAddList.add(data2);
				deleteDataList.remove(0);
			}
		}
		
		dataList = toAddList;
		
		return dataList;
	}
	
	/**
	 * Fills up missing data with 0
	 * @param dataList
	 * @return
	 */
	private static List<Data> FillUpMissingData (List<Data> dataList){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		for (int i = 0; i < dataList.size() - 1; i++) {
			Data data1 = dataList.get(i);
			Data data2 = dataList.get(i + 1);
			if (data2.getDateValue() - data1.getDateValue() > Const.MINUTE * 15) { //if it is greater, fill with empty interval

				long dateVal = data1.getDateValue() + Const.MINUTE * 15;

				cal.setTime(new Date(dateVal));
				Data fillData = new Data(cal.getTimeInMillis());//new interval with no energy
				fillData.setKWh(0);
				dataList.add(i + 1, fillData);
				data1 = dataList.get(i);

				data2 = dataList.get(i + 1);

			} 
			//if the interval data is 5 minutes
			else if (data2.getDateValue() - data1.getDateValue() < Const.MINUTE * 15){ //if they are
				dataList.get(i).setKWh(data1.getKWh() + data2.getKWh());
				
				dataList.remove(i+1);
				i--;
			}
		}

		return dataList;
	}
	
	private static List<Data> trimToNearestDay(List<Data> dataList){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dataList.get(0).getDateValue());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);

		
		while (hour != 0 || min != 0) { // while the hour or minute are not 0
			dataList.remove(0);
			cal.setTimeInMillis(dataList.get(0).getDateValue());
			
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			
		}

		cal.setTimeInMillis(dataList.get(dataList.size()-1).getDateValue());
		int hour1 = cal.get(Calendar.HOUR_OF_DAY);
		
		while (hour1 != 23) { // while the hour and minute is not 23:45
			dataList.remove(dataList.size() - 1);
			cal.setTimeInMillis(dataList.get(dataList.size()-1).getDateValue());
			hour1 = cal.get(Calendar.HOUR_OF_DAY);
		}
		return dataList;
	}

	/**
	 * This method is called when we finished reading a line, after the line is 
	 * read, it passes to create the interval object
	 * @param line
	 * @return
	 */
	private static Data createData(String line) {
		String delims = "[	]+";
		String[] dataTokens = line.split(delims);
		String dateTokens = dataTokens[4];
		String year = dateTokens.substring(0, 4);
		String month = dateTokens.substring(4, 6);
		
		short y = Short.parseShort(year);
		short m = 0;
		if (Character.getNumericValue(month.charAt(0)) == 0) {
			m = (short) Character.getNumericValue(month.charAt(1));
		} else {
			m = Short.parseShort(month);
		}

		String day = dateTokens.substring(6, 8);
		short d = 0;
		if (Character.getNumericValue(day.charAt(0)) == 0) {
			d = (short) Character.getNumericValue(day.charAt(1));
		} else {
			d = Short.parseShort(day);
		}

		String hour = dateTokens.substring(8, 10);
		int h = 0;
		if (Character.getNumericValue(hour.charAt(0)) == 0) {
			h = Character.getNumericValue(hour.charAt(1));
		} else {
			h = Integer.parseInt(hour);
		}

		String minute = dateTokens.substring(10, 12);
		short min = Short.parseShort(minute);

		float con = Float.parseFloat(dataTokens[2]);
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		@SuppressWarnings("deprecation")
		Date date = new Date(y - 1900, m - 1, d, h, min);
		cal.setTime(date);

		Data interval = new Data(cal.getTimeInMillis());
		
		switch(dataTokens[5]){
		case ("KWH"):
			if (dataTokens.length == 9) {
				interval.setGenkWh(con);
				interval.setGenkW(con*4);
			} else {
				interval.setKWh(con);
				interval.setkW(con*4);
			}
			
		break;
		case("KW"):
			if (dataTokens.length == 9) {
				interval.setGenkWh(con);
				interval.setGenkW(con/4);
			} else {
				interval.setKWh(con);
				interval.setkW(con/4);
			}
		break;
		}

		
		interval.setDate(cal.getTime());
		interval.setDateValue(cal.getTime().getTime());
		//interval.setMeterNumber(Assistant.parsePropertiesString(dataTokens[0]));

		return interval;
	
	}

	
}
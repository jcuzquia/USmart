package models;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.avaje.ebean.enhance.agent.SysoutMessageOutput;

import constants.Const;
import javafx.beans.property.StringProperty;

/**
 * Model for interval data which is bout 15 minutes reading from a meter
 * 
 * @author Jose Camilo Uzquiano
 *
 */
public class Data implements Comparable<Data>, Serializable{

	public Long id;
	private float kWh, cost, kW, genkW, genkWh, kVarh, kVar;
	private short julianDay;
	private boolean isStartDay, isEndDay;
	private Date date;
	private long dateValue;
	private StringProperty meterNumber;
	private String dayType;
	
	public Meter meter;
	
	public Data(long dateValue) {
		this.dateValue = dateValue;
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(dateValue);
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			dayType = Const.WEEKEND;
		} else {
			dayType = Const.WORKDAY;
		}
		
		date = new Date(dateValue);
	}
	
	public Data(){
	}
	
	public float getKWh() {
		return kWh;
	}

	public void setKWh(float kWh) {
		this.kWh = kWh;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getkW() {
		return kW;
	}

	public short getJulianDay() {
		return julianDay;
	}

	public boolean isStartDay() {
		return isStartDay;
	}

	public void setStartDay(boolean isStartDay) {
		this.isStartDay = isStartDay;
	}

	public boolean isEndDay() {
		return isEndDay;
	}

	public void setEndDay(boolean isEndDay) {
		this.isEndDay = isEndDay;
	}

	

	

	@Override
	public String toString() {
		return "IntervalData [kW=" + kW + ", dateValue=" + dateValue + "]";
	}
	
	public String getDateString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String output = sdf.format(date);
		return output;
	}
	
	public float getTime(){
		@SuppressWarnings("deprecation")
		float hour = date.getHours();
		float minute = date.getMinutes();
		if (minute == 0){
			minute = 0;
		} else if(minute == 15){
			minute = 0.25f;
		} else if (minute == 30){
			minute = 0.5f;
		} else if (minute == 45) {
			minute = 0.75f;
		}
		return (hour+minute);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getDateValue() {
		return dateValue;
	}

	public void setDateValue(long dateValue) {
		this.dateValue = dateValue;
	}

	public void setJulianDay(short julianDay) {
		this.julianDay = julianDay;
	}

	public float getkVarh() {
		return kVarh;
	}

	public void setkVarh(float kVarh) {
		this.kVarh = kVarh;
	}

	public float getkVar() {
		return kVar;
	}

	public void setkVar(float kVar) {
		this.kVar = kVar;
	}

	public void setkW(float kW) {
		this.kW = kW;
	}

	@Override
	public int compareTo(Data data) {
		long comparedTime = data.getDateValue();
		if (this.dateValue > comparedTime) {
			return 1;
		} else if (this.dateValue == comparedTime) {
			return 0;
		} else {
			return -1;
		}
	}

	public float getGenkW() {
		return genkW;
	}

	public void setGenkW(float genkW) {
		this.genkW = genkW;
	}

	public float getGenkWh() {
		return genkWh;
	}

	public void setGenkWh(float genkWh) {
		this.genkWh = genkWh;
	}

	public StringProperty getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(StringProperty meterNumber) {
		this.meterNumber = meterNumber;
	}

	public String getDayType() {
		return dayType;
	}

	public void setDaytype(String daytype) {
		this.dayType = daytype;
	}

}
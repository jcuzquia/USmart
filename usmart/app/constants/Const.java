package constants;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

/**
 * List of constants to be used throughout the application
 * @author Jose Camilo Uzquiano
 *
 */
public class Const {

	// Application constants
	public static final String HOME = "home";
	public static final String NAV_HOME = "home";
	public static final String NAV_ABOUT = "about";
	public static final String NAV_CONTACT = "contact";
	public static final String NAV_DASHBOARD = "dashboard";
	public static final String UPLOADING_METER = "uploading meter";
	public static final String PROJECT_PAGE = "Project Page";
	public static final String TIME_SERIES = "Time Series";
	public static final String METER_PAGE = "Meter Page";
	public static final String MY_METERS = "My Meters"; //title My meters passed to the projectPage
	
	// daytypesused for the daily time series
	public static final String WEEKEND = "Weekend";
	public static final String WORKDAY = "WorkDay";
	
	public static final String KWH = "kWh";
	
	public static final long HOUR = 3600000; //the hour lenght in millisecods
	public static final long MINUTE = 60000; // minute length in Milliseconds
	public static final long DAY = 3600000*24; // day length in milliseconds
	
	//lenght of months in days
	public static final int[] MONTH_LENGTHS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	//tab delimitation
	public static final String TAB_DELIMITED= "\t";
	public static final String COMMA_DELIMITED= "\t";
	
	public static final String NREL_URL_USAFN = "http://rredc.nrel.gov/solar/old_data/nsrdb/1991-2005/tmy3/by_USAFN.html";
	public static final String NREL_URL_CVS_FILES = "http://rredc.nrel.gov/solar/old_data/nsrdb/1991-2005/tmy3/";
	

	public static final BufferedImage HEAT_MAP_IMAGE = getHeatMapImage();
	

	public enum DayType {

		BUSSINESS_DAY,
		SUMMER_DAY,
		WEEKEND,
		HOLIDAY;
		
	}

	private static BufferedImage getHeatMapImage() {
		
		
		BufferedImage img = null;
		try{
			
			img = ImageIO.read(new File("resources/HeatMap.PNG"));
		}catch (IOException e){
			e.printStackTrace();
			System.out.println("Cannot find file");
		}
		return img;
	}
	
	
	/*
	 * This is a map that displays all the states in the US so we can select 
	 * the appropriate list for each state
	 */
	public static final LinkedHashMap<String, String> STATE_MAP;


	static {
	    STATE_MAP = new LinkedHashMap<>();
	    STATE_MAP.put("Alabama","AL");
	    STATE_MAP.put("Alaska","AK");
	    STATE_MAP.put("Arizona","AZ");
	    STATE_MAP.put("Arkansas","AR");
	    STATE_MAP.put("California","CA");
	    STATE_MAP.put("Colorado","CO");
	    STATE_MAP.put("Connecticut","CT");
	    STATE_MAP.put("Delaware","DE");
	    STATE_MAP.put("Florida","FL");
	    STATE_MAP.put("Georgia","GA");
	    STATE_MAP.put("Hawaii","HI");
	    STATE_MAP.put("Idaho","ID");
	    STATE_MAP.put("Illinois","IL");
	    STATE_MAP.put("Indiana","IN");
	    STATE_MAP.put("Iowa","IA");
	    STATE_MAP.put("Kansas","KS");
	    STATE_MAP.put("Kentucky","KY");
	    STATE_MAP.put("Louisiana","LA");
	    STATE_MAP.put("Maine","ME");
	    STATE_MAP.put("Maryland","MD");
	    STATE_MAP.put("Massachusetts","MA");
	    STATE_MAP.put("Michigan","MI");
	    STATE_MAP.put("Minnesota","MN");
	    STATE_MAP.put("Mississippi","MS");
	    STATE_MAP.put("Missouri","MO");
	    STATE_MAP.put("Montana","MT");
	    STATE_MAP.put("Nebraska","NE");
	    STATE_MAP.put("Nevada","NV");
	    STATE_MAP.put("New Hampshire","NH");
	    STATE_MAP.put("New Jersey","NJ");
	    STATE_MAP.put("New Mexico","NM");
	    STATE_MAP.put("New York","NY");
	    STATE_MAP.put("North Carolina","NC");
	    STATE_MAP.put("North Dakota","ND");
	    STATE_MAP.put("Ohio","OH");
	    STATE_MAP.put("Oklahoma","OK");
	    STATE_MAP.put("Oregon", "OR");
	    STATE_MAP.put("Pennsylvania","PA");
	    STATE_MAP.put("Rhode Island","RI");
	    STATE_MAP.put("South Carolina","SC");
	    STATE_MAP.put("South Dakota","SD");
	    STATE_MAP.put("Tennessee","TN");
	    STATE_MAP.put("Texas","TX");
	    STATE_MAP.put("Utah","UT");
	    STATE_MAP.put("Vermont","VT");
	    STATE_MAP.put("Virginia","VA");
	    STATE_MAP.put("Washington","WA");
	    STATE_MAP.put("West Virginia","WV");
	    STATE_MAP.put("Wisconsin","WI");
	    STATE_MAP.put("Wyoming","WY");
	}
}

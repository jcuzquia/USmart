package util;

import constants.Const;
import models.Meter;
import models.Project;
import play.twirl.api.Html;
import services.UserProvider;
import views.html.project.meter_list;
import views.html.project.meter_page;
import views.html.project.project_page;
import views.html.project.time_series_page;

public class HtmlFactory {

	/**
	 * this method generates the required HTML pages
	 * 
	 * @param meter
	 * @param key
	 * @return
	 */
	public static Html getPage(Project project, Meter meter, String key) {
		Html page = null;
		Html meterListPage;
		switch (key) {
		case (Const.PROJECT_PAGE): //shows the project page containing the meter table
//			meterListPage = meter_list.render(project);
//			page = project_page.render(project, Const.MY_METERS, meterListPage);
			break;
		case (Const.METER_PAGE):
//			meterListPage = meter_list.render(project);
//			page = meter_page.render(project, Const.MY_METERS, meterListPage, meter);
			break;
		case (Const.TIME_SERIES):
//			meterListPage = meter_list.render(project);
//			page = time_series_page.render(project, Const.MY_METERS, meter);
			break;
		}
		return page;
	}
	
	public static Html getProjectPage(UserProvider userProvider, Project project, String key){
		Html page;
		Html meterListPage;
		meterListPage = meter_list.render(project);
		page = project_page.render(userProvider, project, Const.MY_METERS, meterListPage);
		return page;
	}
	
	public static Html getMeterPage(UserProvider userProvider, Project project, Meter meter, String key){
		
		Html page = meter_page.render(userProvider, project, Const.MY_METERS, meter);
		return page;
	}
	
	public static Html getTimeSeriesPage(UserProvider userProvider, Project project, Meter meter, String key){
		Html meterListPage = time_series_page.render(userProvider, project, key, meter);
		return meterListPage;
	}
	
	
}

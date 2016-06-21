package controllers;

import javax.inject.Inject;

import com.feth.play.module.pa.PlayAuthenticate;

import constants.Const;
import models.Meter;
import models.Project;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import play.twirl.api.Html;
import providers.MyUsernamePasswordAuthProvider;
import services.UserProvider;
import util.HtmlFactory;

/**
 * Control the time series page of the meter
 * @author Uzquianoj1
 *
 */
public class TimeSeriesController extends Controller{

	private final PlayAuthenticate auth;
	private final UserProvider userProvider;
	private final MyUsernamePasswordAuthProvider myUsrPaswProvider;
	
	@Inject
	public TimeSeriesController(final PlayAuthenticate auth, final UserProvider userProvider,
			   final MyUsernamePasswordAuthProvider myUsrPaswProvider){
		this.auth = auth;
		this.userProvider = userProvider;
		this.myUsrPaswProvider = myUsrPaswProvider;
	}
	
	
	public Result showTimeSeriesPage(Long meterId){
		String key = Const.TIME_SERIES;
		Meter meter = Meter.loadMeter(meterId);
		if (meter == null){
			return badRequest();
		}
		Project project = meter.project;
		Logger.info("Showing TimeSeriesPage with dayTypeSize of: " + meter.dayTypeList.size());
		Html dailyTimeSeriesPage = HtmlFactory.getTimeSeriesPage(userProvider, project, meter, key);
		return ok(dailyTimeSeriesPage);
	}
	
	/**
	 * This is called from the list of daytype checkboxes in the time_series_page
	 * @param dayType
	 * @param meterId
	 * @return
	 */
	public Result activateDayType(String dayType, String meterId){
		System.out.println("Activating dayType: " + dayType + " ...Meter: " +meterId);
		Long id = new Long(meterId);
		Meter meter = Meter.findById(id);
		meter.activateDayType(dayType);
		meter.update();
		return redirect(routes.TimeSeriesController.showTimeSeriesPage(id));
	}
	
	public Result jsTimeSeriesRoutes(){
		return ok(
				JavaScriptReverseRouter.create("jsTimeSeriesRoutes", 
						routes.javascript.TimeSeriesController.activateDayType())
				).as("text/javascript");
				
	}
	
	public Result addNewDayType(Long meterId){
		return TODO;
	}
}
package controllers;

import javax.inject.Inject;

import com.feth.play.module.pa.PlayAuthenticate;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.DayType;
import models.DayTypeForm;
import models.Meter;
import models.Project;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
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
	private final FormFactory formFactory;
	private final Form<DayTypeForm> DAYTYPE_FORM;
	@Inject
	public TimeSeriesController(final PlayAuthenticate auth, final UserProvider userProvider,
			   final MyUsernamePasswordAuthProvider myUsrPaswProvider, FormFactory formFactory){
		this.auth = auth;
		this.userProvider = userProvider;
		this.myUsrPaswProvider = myUsrPaswProvider;
		this.DAYTYPE_FORM = formFactory.form(DayTypeForm.class);
		this.formFactory = formFactory;
	}
	
	
	public Result showTimeSeriesPage(Long meterId){
		String key = Const.TIME_SERIES;
		Meter meter = Meter.loadMeter(meterId);
		if (meter == null){
			return badRequest();
		}
		Project project = meter.project;
		Logger.info("Showing TimeSeriesPage with dayTypeSize of: " + meter.dayTypeList.size());
		Html dailyTimeSeriesPage = HtmlFactory.getTimeSeriesPage(userProvider, project, meter, key, DAYTYPE_FORM);
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
	
	/**
	 * This is called from the dayType controller to delete the daytype of
	 * an specific meter
	 * @param meterId
	 * @param dayTypeId
	 * @return
	 */
	public Result deleteDayType(Long meterId, Long dayTypeId){
		DayType dayType = DayType.findById(dayTypeId);
		Meter meter = Meter.findById(meterId);
		Logger.info("Deleting dayType: "+ dayType.dayType + " from meter: " + meter.id );
		dayType.delete();
		return ok(Json.toJson(meter.dayTypeList));
	}
	
	/**
	 * This method will add the dayType to the meter so it then shows it as one of the values to be changed
	 * @param meterid
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result saveNewDayType (Long meterId){
		Form<DayTypeForm> dayTypeForm = formFactory.form(DayTypeForm.class).bindFromRequest();
		if (dayTypeForm.hasErrors()){
			Logger.info("DayType form has some errors");
			flash("error", "DayType Form Has Errors");
			return badRequest("Error");
		} else {
			String daytype = dayTypeForm.get().dayType;
			Logger.info("Saving " + daytype + " succesfully");
			DayType dayType = DayType.create(daytype);
			Meter meter = Meter.findById(meterId);
			meter.dayTypeList.add(dayType);
			Logger.info("The list is of size: " + meter.dayTypeList.size());
			meter.update();
			return redirect(routes.TimeSeriesController.showTimeSeriesPage(meterId));
		}
		
		
		
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
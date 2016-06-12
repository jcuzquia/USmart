package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.feth.play.module.pa.PlayAuthenticate;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import controllers.Account.Accept;
import models.Data;
import models.Meter;
import models.MeterForm;
import models.Project;
import models.ProjectForm;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;
import play.twirl.api.Html;
import providers.MyUsernamePasswordAuthProvider;
import services.UserProvider;
import util.HtmlFactory;
import util.MeterFileReader;
import views.html.dashboard.project_info;
import views.html.project.meter_info;


/**
 * Controls the project page
 * @author Camilo Uzquiano
 *
 */
public class ProjectController extends Controller {

	private final PlayAuthenticate auth;
	private final UserProvider userProvider;
	private final MyUsernamePasswordAuthProvider myUsrPaswProvider;
	
	
	private final Form<ProjectForm> PROJECT_FORM ;
	private final Form<MeterForm> METER_FORM;
	
	@Inject
	public ProjectController(final PlayAuthenticate auth, final UserProvider userProvider,
			   final MyUsernamePasswordAuthProvider myUsrPaswProvider, FormFactory formFactory) {
		this.auth = auth;
		this.userProvider = userProvider;
		this.myUsrPaswProvider = myUsrPaswProvider;
		
		this.PROJECT_FORM = formFactory.form(ProjectForm.class);
		this.METER_FORM = formFactory.form(MeterForm.class);
	}
	
	
	/**
	 * Displays a list of project in which the user is part of
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result list() {
		return TODO;
	}
	
	
	/**
	 *  Show project page. This project page has my meter_list html,
	 * @param id of the project
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result showProject(Long projectId, Long meterId){
		String key = Const.PROJECT_PAGE;
		Project project = Project.findById(projectId);
		Meter meter = Meter.findById(meterId);
		
		
		if(project != null){
			Html projectPage = HtmlFactory.getProjectPage(userProvider, project, key);
			return ok(projectPage);
		} else {
			return badRequest();
		}
	}
	
	
	/**
	 * Show the heat map of the meter
	 * @param id of the meter
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result showMeterPage(Long meterId){
		String key = Const.METER_PAGE;
		Meter meter = Meter.findById(meterId);
		Project project = meter.project;
		if(meter != null){ //never suppossed to be 0
			Html heatMapPage = HtmlFactory.getMeterPage(userProvider, project, meter, key);
			
			return ok(heatMapPage);
		} else {
			return badRequest();
		}
	}
	
	
	
	/**
	 * Shows the form that will be used to add a project
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result addProject(){
		final User localUser = userProvider.getUser(session());
		return ok(project_info.render(PROJECT_FORM, userProvider, ""));
	}
	
	/**
	 * Sends the request to save the new project into the database
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result saveProject(){
		Form<ProjectForm> projectForm = Form.form(ProjectForm.class).bindFromRequest();
		final User localUser = userProvider.getUser(session());
		
		if(projectForm.hasErrors()){
			flash("error", "Form has errors");
			return badRequest(project_info.render(PROJECT_FORM, userProvider, ""));
		}
		String title = projectForm.get().title;
		String description = projectForm.get().description;
		Project project = Project.create(localUser,title,description);
		
		if(project != null){
			localUser.projects.add(project);
			localUser.save();
			return redirect(routes.HomeController.dashboard());
		}
		return badRequest(project_info.render(PROJECT_FORM, userProvider, ""));
	}
	
	/**
	 * Deletes the project from the database
	 * @param id
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result deleteProject(Long id){
		Project project = Project.findById(id);
		if(project == null) {
			badRequest("Project not found");
		}
		project.delete();
		return redirect(routes.HomeController.dashboard());
	}
	
	/**
	 * Handling the raw meter data upload
	 * 
	 * @return
	 */
	public Result getMeterData(String mode, Long projectId) {
		
		//working with the form
		Form<Meter> meterForm = Form.form(Meter.class).bindFromRequest();
		if(meterForm.hasErrors()){
			flash("Error", "The meter form has errors");
			return badRequest("Meter Form has errors");
		}
		
		Meter meter = meterForm.get();
		//getting the file data
		MultipartFormData<File> body = request().body().asMultipartFormData();
		FilePart<File> filePart = body.getFile("meter_data");

		Project project = Project.findById(projectId);
		
		List<Data> dataList = new ArrayList<Data>();
		if(filePart != null) {
			
			File file = filePart.getFile();
			dataList = MeterFileReader.getDataListFromFile(file);
			//if data list is not empty then create a meter instance
			if(!dataList.isEmpty()){
				meter.setDataList(dataList);
				project.addMeter(meter);
				project.update();
				meter.save();
			}
			flash("Message", "Upload Successful");
			return redirect(routes.ProjectController.showProject(project.id, meter.id));
			
		} else {
			flash ("Error", "failed uploading file");
			return redirect(routes.ProjectController.showProject(projectId, 0));
		}
		
	}
	
	public Result addMeter(Long projectId){
		Project project = Project.findById(projectId);
		return ok(meter_info.render(userProvider, "",METER_FORM, project));
	}
	
	
	/**
	 * This is called from the javascript method acter clicking a button,
	 * from the meter list
	 * @param meterId
	 * @return
	 */
	@SuppressWarnings("unused")
	public Result retrieveHeatmapData(Long meterId){
		Meter meter = Meter.findById(meterId);
		Long projectId = meter.project.id;
		
		return redirect(routes.ProjectController.showProject(projectId, meterId));
	}
	
	/**
	 * Finds and deletes an specific meter
	 * @param id
	 * @return
	 */
	public Result deleteMeter(Long id){
		Meter meter = Meter.findById(id);
		Project project = meter.project;
		meter.delete();
		return redirect(routes.ProjectController.showProject(project.id, 0));
	}
	
	
	
	/**
	 * Routes that are called from javascript code in the template
	 * @return
	 */
	public Result jsProjectRoutes() {
		return ok(
				JavaScriptReverseRouter.create("jsProjectRoutes", 
						routes.javascript.ProjectController.getMeterData()
						)
				);
	}
	
}
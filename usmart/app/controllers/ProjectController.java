package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.feth.play.module.pa.PlayAuthenticate;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.Data;
import models.DataContainer;
import models.Meter;
import models.MeterForm;
import models.Project;
import models.ProjectForm;
import models.User;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
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

	@SuppressWarnings("unused")
	private final PlayAuthenticate auth;
	private final UserProvider userProvider;
	@SuppressWarnings("unused")
	private final MyUsernamePasswordAuthProvider myUsrPaswProvider;
	
	private final FormFactory formFactory;
	private final Form<ProjectForm> PROJECT_FORM ;
	private final Form<MeterForm> METER_FORM;
	
	@Inject
	public ProjectController(final PlayAuthenticate auth, final UserProvider userProvider,
			   final MyUsernamePasswordAuthProvider myUsrPaswProvider, FormFactory formFactory) {
		this.auth = auth;
		this.userProvider = userProvider;
		this.myUsrPaswProvider = myUsrPaswProvider;
		this.formFactory = formFactory;
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
		Meter meter = Meter.loadMeter(meterId);
		Project project = meter.project;
		Html heatMapPage = HtmlFactory.getMeterPage(userProvider, project, meter, key);
		
		return ok(heatMapPage);
	}
	
	
	
	/**
	 * Shows the form that will be used to add a project
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result addProject(){
		return ok(project_info.render(PROJECT_FORM, userProvider, ""));
	}
	
	/**
	 * Sends the request to save the new project into the database
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result saveProject(){
		Form<ProjectForm> projectForm = formFactory.form(ProjectForm.class).bindFromRequest();
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
			project.createFileDirectory(localUser, project.id);
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
		Logger.info("Starting to Delete project ProjectController.deleteProject()");
		if(project == null) {
			Logger.error("No project was found to be deletedd");
			badRequest("Project not found");
		} else {
			// check if the project file directory exists
			File projectFolder = new File(project.projectPath);
			if(!projectFolder.exists()){
				Logger.info("During project deletion in ProjectController.deleteProject(), there was no file at: " + project.projectPath);
			} else {
				Logger.debug("Need to add folder deletion at ProjectController.deleteProject()");
			}
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
		Form<Meter> meterForm = formFactory.form(Meter.class).bindFromRequest();
		if(meterForm.hasErrors()){
			Logger.error("submission of meter Form has error");
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
			final DataContainer dataContainer = new DataContainer(dataList);
			//if data list is not empty then create a meter instance
			if(!dataList.isEmpty()){
				meter.setDataList(dataContainer);
				project.addMeter(meter);
				project.update();
				final String meterPath = project.projectPath + "\\" + meter.id + Const.FILE_EXTENTION;
				meter.setPath(meterPath);
				project.update();
				serializeMeterData(meter, meter.path);
			}
			flash("Message", "Upload Successful");
			return redirect(routes.ProjectController.showProject(project.id, meter.id));
			
		} else {
			flash ("Error", "failed uploading file");
			return redirect(routes.ProjectController.showProject(projectId, 0));
		}
		
	}
	
	/**
	 * We are Serializing the data container of the meter
	 * @param meter
	 * @param project folder path
	 */
	public void serializeMeterData(Meter meter, String meterPath){
		   
		   try{
			   
			FileOutputStream fout = new FileOutputStream(meterPath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(meter.getDataContainer());
			oos.close();
			System.out.println("Done");
			   
		   }catch(Exception ex){
			   ex.printStackTrace();
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
	public Result getHeatMapJson(String meterId){
		
		Meter meter = Meter.loadMeter(Long.parseLong(meterId));
		Logger.info("Calling the GetheatMap Json and Loading the meter...");
		List<String> heatmapData = new ArrayList<String>();
		if (meter == null){
			flash ("Error", "Unable to find the meter " + meterId);
			Logger.error("Were not able to find the meter " + meterId + " in getHeatMapJson at ProjectController");
			redirect(routes.HomeController.dashboard());
		} else {
			
			flash ("Message", "Showing meter " + meterId);
			heatmapData = new ArrayList<>(meter.getHeatMapData());
			if(heatmapData.isEmpty()){
				Logger.error("The data was not able to load at ProjectController.getHeatMapJson()");
			}
		}
		
		return ok(Json.toJson(heatmapData.size()));
	}
	
	/**
	 * Finds and deletes an specific meter
	 * @param id
	 * @return
	 */
	public Result deleteMeter(Long meterId){
		Meter meter = Meter.findById(meterId);
		Project project = meter.project;
		meter.deleteMeterFile();
		meter.delete();
		
		return redirect(routes.ProjectController.showProject(project.id, 0));
	}
	
	
	
	/**
	 * Routes that are called from javascript code in the template
	 * @return
	 */
	public Result jsProjectRoutes() {
//		System.out.println("Calling jsProjectRoutes");
		return ok(
				JavaScriptReverseRouter.create("jsProjectRoutes", 
						routes.javascript.ProjectController.getHeatMapJson()
						)
				).as("text/javascript");
	}
	
}
package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import constants.Const;
import models.Project;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import services.UserProvider;
import views.html.index;
import views.html.contact;
import views.html.about;
import views.html.signup;
import views.html.dashboard.my_projects;
import views.html.login;
import views.html.dashboard_main;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	
	private final PlayAuthenticate auth;
	
	private final MyUsernamePasswordAuthProvider provider;
	
	public final UserProvider userProvider;
	
	public static String formatTimestamp(final long t){
		return new SimpleDateFormat("yyy-dd-MM HH:mm:ss").format(new Date());
	}
	
	@Inject
	public HomeController(final PlayAuthenticate auth, final MyUsernamePasswordAuthProvider provider,
			final UserProvider userProvider) {
		this.auth = auth;
		this.provider = provider;
		this.userProvider = userProvider;
	}
	
	/**
	 * Shows the home page
	 * @return
	 */
	public Result index() {
		Html html = index.render(this.userProvider, Const.NAV_HOME);
		return ok(html);
	}
	
	/**
	 * Shows the about page
	 * @return
	 */
	public Result about() {
		Html html = about.render(this.userProvider, Const.NAV_ABOUT);
		return ok(html);
	}
	
	/**
	 * Shows the contact page
	 * @return
	 */
	public Result contact() {
		Html html = contact.render(this.userProvider, Const.NAV_CONTACT);
		return ok(html);
	}
	
	/**
	 * Show profile page. This is restricted and can only be accessed once authenticated
	 * @return
	 */
	@Restrict(@Group(HomeController.USER_ROLE))
	public Result profile() {
		final User localUser = userProvider.getUser(session());
		
		return TODO;
	}
	
	public static String getUserRole() {
		return USER_ROLE;
	}

	public UserProvider getUserProvider() {
		return userProvider;
	}

	@Restrict(@Group(HomeController.USER_ROLE))
	public Result dashboard(){
		final User localUser = userProvider.getUser(session());
		List<Project> projects = Project.findAllByUser(localUser.email);
		return ok(dashboard_main.render(userProvider, 
				Const.NAV_DASHBOARD,
				my_projects.render(projects)
					)
				);
	}
	
	public Result jsRoutes() {
		return TODO;
	}
	
	public Result doLogin () {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = this.provider.getLoginForm();
		if (filledForm.hasErrors()){
			// user did not fill everything properly
			return badRequest();
		} else {
			// everything was filled
			// do something with your part of the form before handling user
			// signup
			return this.provider.handleLogin(ctx());
		}
	}
	
	public Result login() {
		return ok(login.render(this.auth, this.userProvider,  this.provider.getLoginForm()));
	}

	public Result signup() {
		Html html = signup.render(this.auth, this.userProvider, this.provider.getSignupForm());
		return ok(html);
	}
	
	public Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = this.provider.getSignupForm().bindFromRequest();
		
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(this.auth, this.userProvider, filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			
			return this.provider.handleSignup(ctx());
		}
	}

}

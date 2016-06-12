package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import play.data.validation.Constraints;

import com.avaje.ebean.Model;

@Entity
@Table(name = "project")
public class Project extends Model {
	
	@Id
	public Long id;
	
	@ManyToMany(cascade=CascadeType.ALL)
	public List<User> members = new ArrayList<User>();
	
	@OneToMany(cascade=CascadeType.ALL)
	public List<Meter> meters = new ArrayList<Meter>();

	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String title;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String description;
	
	private static Model.Finder<String, Project> find = new Model.Finder<String, Project>(Project.class);
	
	
	public Project(User owner, String title, String description){
		members.add(owner);
		this.title = title;
		this.description = description;
		
	}
	
	
	
	
	/**
	 * Method that finds all the projects globally
	 * @return
	 */
	public static List<Project> findAll(){
		return Project.find.orderBy("id").findList();
	}
	
	public static Project create(User owner, String title, String description){
		Project project = new Project(owner, title, description);
		return project;
	}
	
	public static List<Project> findAllByUser(String email){
		return find.where()
				.eq("members.email", email).findList();
	}
	
	public static Project findById(Long id){
		return find.byId(id.toString());
	}
	
	public void addMeter(Meter meter){
		meters.add(meter);
	}
	
	
	
}


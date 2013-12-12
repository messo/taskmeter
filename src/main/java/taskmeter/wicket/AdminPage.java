package taskmeter.wicket;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;

import taskmeter.domain.Project;
import taskmeter.domain.Role;
import taskmeter.domain.User;
import taskmeter.ejb.ProjectManager;
import taskmeter.ejb.UserManager;
import taskmeter.wicket.util.UserChoiceRenderer;

@AuthorizeInstantiation("ADMIN")
public class AdminPage extends BasePage {

	private static final long serialVersionUID = 1L;

	private User user;

	private String project;
	private User leader;

	@EJB
	private ProjectManager projectManager;
	@EJB
	private UserManager userManager;

	private List<Role> roles;

	private Map<Role, Boolean> hasThatRole;

	@SuppressWarnings("serial")
	public AdminPage() {
		roles = userManager.getRoles();
		adminNavLi.add(AttributeAppender.replace("class", "active"));

		// Userek szerkesztése
		Form<Void> editUserForm = new Form<>("editUserForm");
		add(editUserForm);

		editUserForm.add(new DropDownChoice<User>("users", new PropertyModel<User>(this, "user"),
				new ListModel<User>() {
					@Override
					public List<User> getObject() {
						return userManager.getUsers();
					}
				}, new UserChoiceRenderer()).setNullValid(true));

		editUserForm.add(new Button("editUser"));

		editUserForm.add(new Button("addUser") {
			@Override
			public void onSubmit() {
				user = new User();
			}
		});

		// User form
		WebMarkupContainer userDatas = new WebMarkupContainer("userDatas") {

			@Override
			protected void onConfigure() {
				setVisible(user != null);
			}
		};
		add(userDatas);

		Form<User> userForm = new Form<>("userForm", new CompoundPropertyModel<>(new PropertyModel<User>(this, "user")));
		userForm.add(new EmailTextField("email"));
		userForm.add(new TextField<>("lastName"));
		userForm.add(new TextField<>("firstName"));
		userForm.add(new PasswordTextField("password").setRequired(false));
		userForm.add(new Button("saveUser") {

			@Override
			public void onSubmit() {
				userManager.update(user);
				user = null;
			}
		});
		userForm.add(new Button("delUser") {

			@Override
			public void onSubmit() {
				if (user.getId() == null) {
					user = null;
					return;
				}

				try {
					userManager.removeById(user.getId(), User.class);
				} catch (EJBException ex) {
					error("Nem törölhető, nem lehet hogy még projektvezető valahol?");
				}
				user = null;
			}
		});
		userDatas.add(userForm);

		// Szerepkörök
		Form<Void> rolesForm = new Form<>("rolesForm");
		rolesForm.add(new ListView<Role>("roles", roles) {

			@Override
			protected void onConfigure() {
				hasThatRole = new HashMap<>();
				for (Role role : roles) {
					hasThatRole.put(role, false);
				}
				if (user.getId() != null) {
					for (Role role : userManager.getRolesByUser(user)) {
						hasThatRole.put(role, true);
					}
				}
			}

			@Override
			protected void populateItem(final ListItem<Role> item) {
				item.add(new CheckBox("checkbox", new IModel<Boolean>() {

					@Override
					public void detach() {
					}

					@Override
					public Boolean getObject() {
						return hasThatRole.get(item.getModel().getObject());
					}

					@Override
					public void setObject(Boolean has) {
						hasThatRole.put(item.getModel().getObject(), has);
					}
				}));
				item.add(new Label("name", item.getModel().getObject().getDescription()));
			}
		});
		rolesForm.add(new Button("saveRoles") {

			@Override
			public void onSubmit() {
				List<Role> newRoles = new LinkedList<>();
				for (Entry<Role, Boolean> entry : hasThatRole.entrySet()) {
					if (entry.getValue()) {
						newRoles.add(entry.getKey());
					}
				}
				try {
					userManager.setNewRolesFor(newRoles, user);
				} catch (EJBException ex) {
					error("Hiba történt a mentés során!");
				}
			}
		});
		userDatas.add(rolesForm);

		// Meglévő projektek listázása
		Form<Void> projectsForm = new Form<>("projectsForm");
		add(projectsForm);

		projectsForm.add(new ListView<Project>("projects", new ListModel<Project>() {

			@Override
			public List<Project> getObject() {
				return projectManager.getProjects();
			}
		}) {

			@Override
			protected void populateItem(final ListItem<Project> projectItem) {
				projectItem.setModel(new CompoundPropertyModel<>(projectItem.getModel().getObject()));
				projectItem.add(new TextField<>("name"));
				projectItem.add(new DropDownChoice<User>("leader", new ListModel<User>() {
					@Override
					public List<User> getObject() {
						return userManager.getLeaders();
					}
				}, new UserChoiceRenderer()));
				projectItem.add(new Button("save") {
					@Override
					public void onSubmit() {
						try {
							projectManager.merge(projectItem.getModel().getObject());
						} catch (Exception ex) {
							error("Hiba történt a frissítésnél -- lehet már létezik ilyen névvel projekt?");
						}
					}
				});

				projectItem.add(new Button("delete") {
					@Override
					public void onSubmit() {
						projectManager.removeById(projectItem.getModel().getObject().getId(), Project.class);
					}
				});
			}
		});

		Form<AdminPage> newProjectForm = new Form<AdminPage>("newProjectForm");
		add(newProjectForm);
		newProjectForm.add(new RequiredTextField<>("newProject", new PropertyModel<>(this, "project")));
		newProjectForm.add(new DropDownChoice<User>("newLeader", new PropertyModel<User>(this, "leader"),
				new ListModel<User>() {
					@Override
					public List<User> getObject() {
						return userManager.getLeaders();
					}
				}, new UserChoiceRenderer()).setRequired(true));
		newProjectForm.add(new Button("add") {

			@Override
			public void onSubmit() {
				if (project != null) {
					try {
						projectManager.createNewProject(project, leader);
					} catch (EJBException ex) {
						error("Hiba történt az adatbázisba történő mentésnél -- lehet már létezik ilyen projekt?");
					}
					project = "";
					leader = null;
				} else {
					error("Üres névvel nem lehet létrehozni!");
				}
			}
		});

		add(new FeedbackPanel("fp"));

		// Új projekt felvétele
		// Form<AdminPage> newProjectForm = new Form<AdminPage>("newProject", new
		// CompoundPropertyModel<AdminPage>(this)) {
		//
		// @Override
		// protected void onSubmit() {
		// if (project != null) {
		// try {
		// projectManager.createNewProject(project);
		// } catch (EJBException ex) {
		// error("Hiba történt az adatbázisba történő mentésnél -- lehet már létezik ilyen projekt?");
		// }
		// project = "";
		// } else {
		// error("Üres névvel nem lehet létrehozni!");
		// }
		// }
		// };
		// newProjectForm.add(new FeedbackPanel("fp", new ContainerFeedbackMessageFilter(newProjectForm)));
		// add(newProjectForm.add(new TextField<>("project")));
	}
}

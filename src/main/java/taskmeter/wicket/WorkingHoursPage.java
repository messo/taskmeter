package taskmeter.wicket;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import taskmeter.domain.Project;
import taskmeter.domain.UnitOfWork;
import taskmeter.ejb.ProjectManager;

@AuthorizeInstantiation("PARTICIPANT")
public class WorkingHoursPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@EJB
	ProjectManager projectManager;

	@SuppressWarnings("serial")
	public WorkingHoursPage() {
		workingHoursNavLi.add(AttributeAppender.replace("class", "active"));

		LinkedList<ProjectWork> works = new LinkedList<>();

		Project lastProject = null;
		List<UnitOfWork> stuff = projectManager.getUOWsByUserOrderedByProject(getSession().getCurrentUser());

		// project szerint rendezve, tehát külön tudjuk válogatni:
		for (UnitOfWork uow : stuff) {
			if (lastProject != uow.getProject()) {
				// új csomaghoz érkeztünk:
				ProjectWork pw = new ProjectWork(uow.getProject());
				works.add(pw);
				lastProject = uow.getProject();
			}

			works.getLast().addUnitOfWork(uow);
		}

		// megnézzük, hogy kimaradt-e olyan projekt, amihez nincs még log, de résztvevők vagyunk ezért logolhatunk rá
		List<Project> projects = projectManager.getProjectsByParticipantId(getSession().getCurrentUser().getId());
		for (Project project : projects) {
			boolean found = false;
			for (ProjectWork pw : works) {
				if (pw.getProject().equals(project)) {
					found = true;
					break;
				}
			}
			if (!found) {
				// ez a projekt nem szerepel a listában, mert nincs hozzá log, ezért szúrjuk be.
				ProjectWork pw = new ProjectWork(project);
				works.add(pw);
			}
		}

		add(new ListView<ProjectWork>("projects", works) {

			private UnitOfWork unitOfWork = new UnitOfWork();

			@Override
			protected void populateItem(ListItem<ProjectWork> item) {
				final ProjectWork pw = item.getModelObject();
				item.add(new Label("project", pw.getProject().getName()));

				Form<UnitOfWork> form = new Form<UnitOfWork>("form", new CompoundPropertyModel<>(
						new PropertyModel<UnitOfWork>(this, "unitOfWork"))) {

					@Override
					protected void onSubmit() {
						unitOfWork.setUser(WorkingHoursPage.this.getSession().getCurrentUser());
						unitOfWork.setProject(pw.getProject());
						projectManager.createNewUnitOfWork(unitOfWork);

						unitOfWork = new UnitOfWork();

						throw new RestartResponseException(WorkingHoursPage.class);
					}
				};
				item.add(form);

				form.add(new TextField<>("fromDate"));
				form.add(new TextField<>("toDate"));
				form.add(new TextField<>("description"));

				item.add(new ListView<UnitOfWork>("units", pw.getUnits()) {

					@Override
					protected void populateItem(ListItem<UnitOfWork> item) {
						item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
						item.add(new Label("fromDate"));
						item.add(new Label("toDate"));
						item.add(new Label("description"));
					}
				});
			}
		});
	}

	public static class ProjectWork {
		private Project project;
		private List<UnitOfWork> units;

		public ProjectWork(Project project) {
			this.project = project;
			this.units = new LinkedList<>();
		}

		public void addUnitOfWork(UnitOfWork uow) {
			units.add(uow);
		}

		public Project getProject() {
			return project;
		}

		public List<UnitOfWork> getUnits() {
			return units;
		}
	}
}

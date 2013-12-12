package taskmeter.wicket;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

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

		add(new ListView<ProjectWork>("projects", works) {

			@Override
			protected void populateItem(ListItem<ProjectWork> item) {
				ProjectWork pw = item.getModelObject();
				item.add(new Label("project", pw.getProject().getName()));
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

package taskmeter.wicket;

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

@AuthorizeInstantiation("LEADER")
public class HoursLeaderPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@EJB
	ProjectManager projectManager;

	@SuppressWarnings("serial")
	public HoursLeaderPage(Project project) {
		leaderNavLi.add(AttributeAppender.replace("class", "active"));

		add(new Label("project", project.getName()));

		add(new ListView<UnitOfWork>("units", projectManager.getUOWsByProject(project)) {

			@Override
			protected void populateItem(ListItem<UnitOfWork> item) {
				item.setModel(new CompoundPropertyModel<>(item.getModelObject()));
				item.add(new Label("user"));
				item.add(new Label("fromDate"));
				item.add(new Label("toDate"));
				item.add(new Label("description"));
			}
		});
	}
}

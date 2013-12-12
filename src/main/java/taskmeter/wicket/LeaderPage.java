package taskmeter.wicket;

import javax.ejb.EJB;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import taskmeter.domain.Project;
import taskmeter.ejb.ProjectManager;
import taskmeter.wicket.util.ProjectChoiceRenderer;

@AuthorizeInstantiation("LEADER")
public class LeaderPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@EJB
	ProjectManager projectManager;

	private Project project;

	@SuppressWarnings("serial")
	public LeaderPage() {
		leaderNavLi.add(AttributeAppender.replace("class", "active"));

		Form<Void> chooseProjectForm = new Form<>("chooseProjectForm");
		add(chooseProjectForm);

		add(new FeedbackPanel("fp"));
		
		chooseProjectForm.add(new DropDownChoice<>("projects", new PropertyModel<Project>(this, "project"),
				projectManager.getProjectsByLeader(getSession().getCurrentUser()), new ProjectChoiceRenderer()).setRequired(true));
		chooseProjectForm.add(new Button("chooseProjectForHours") {

			@Override
			public void onSubmit() {
				setResponsePage(new HoursLeaderPage(project));
			}
		});

		chooseProjectForm.add(new Button("chooseProjectForParticipants") {

			@Override
			public void onSubmit() {
				setResponsePage(new ParticipantsLeaderPage(project));
			}
		});
	}
}

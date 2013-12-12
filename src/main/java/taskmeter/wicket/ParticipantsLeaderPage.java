package taskmeter.wicket;

import java.util.List;

import javax.ejb.EJB;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;

import taskmeter.domain.Project;
import taskmeter.domain.User;
import taskmeter.ejb.ProjectManager;
import taskmeter.ejb.UserManager;
import taskmeter.wicket.util.UserChoiceRenderer;

public class ParticipantsLeaderPage extends BasePage {

	private static final long serialVersionUID = 1L;

	@EJB
	ProjectManager projectManager;
	@EJB
	UserManager userManager;

	private User participant;

	@SuppressWarnings("serial")
	public ParticipantsLeaderPage(final Project project) {
		leaderNavLi.add(AttributeAppender.replace("class", "active"));

		Form<Void> projectsForm = new Form<>("participantsForm");
		add(projectsForm);

		projectsForm.add(new ListView<User>("participants", new ListModel<User>() {

			@Override
			public List<User> getObject() {
				return projectManager.getParticipants(project);
			}
		}) {

			@Override
			protected void populateItem(final ListItem<User> userItem) {
				final User user = userItem.getModelObject();
				userItem.add(new Label("name", user.toString()));
				userItem.add(new Label("email", user.getEmail()));
				userItem.add(new Button("delete") {

					@Override
					public void onSubmit() {
						projectManager.removeParticipant(project, user);
					}
				});
			}
		});

		Form<Void> newParticipantForm = new Form<>("newParticipantForm");
		add(newParticipantForm);

		newParticipantForm.add(new DropDownChoice<User>("newParticipant", new PropertyModel<User>(this, "participant"),
				new ListModel<User>() {
					@Override
					public List<User> getObject() {
						List<User> addable = userManager.getParticipants();
						addable.removeAll(projectManager.getParticipants(project));
						return addable;
					}
				}, new UserChoiceRenderer()).setRequired(true));

		newParticipantForm.add(new Button("add") {

			@Override
			public void onSubmit() {
				if (participant != null) {
					projectManager.addParticipant(project, participant);
					participant = null;
				} else {
					error("Üres névvel nem lehet létrehozni!");
				}
			}
		});
	}
}

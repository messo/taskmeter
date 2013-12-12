package taskmeter.wicket.util;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

import taskmeter.domain.User;

public class UserChoiceRenderer implements IChoiceRenderer<User> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(User user) {
		return String.format("%s %s (%s)", user.getLastName(), user.getFirstName(), user.getEmail());
	}

	@Override
	public String getIdValue(User user, int arg1) {
		return String.valueOf(user.getId());
	}
}

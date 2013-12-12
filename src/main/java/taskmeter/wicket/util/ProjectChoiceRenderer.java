package taskmeter.wicket.util;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

import taskmeter.domain.Project;

public class ProjectChoiceRenderer implements IChoiceRenderer<Project> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(Project object) {
		return object.getName();
	}

	@Override
	public String getIdValue(Project object, int index) {
		return String.valueOf(object.getId());
	}
}

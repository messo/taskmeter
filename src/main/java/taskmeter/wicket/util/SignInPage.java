package taskmeter.wicket.util;

import taskmeter.wicket.BasePage;

public class SignInPage extends BasePage {

	private static final long serialVersionUID = 1L;

	public SignInPage() {
		add(new SignInPanel("signInPanel"));
	}
}

package taskmeter.wicket.util;

import org.apache.wicket.markup.html.WebPage;

import taskmeter.wicket.HomePage;

public class SignOutPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public SignOutPage() {
		getSession().invalidate();
		setResponsePage(HomePage.class);
	}
}

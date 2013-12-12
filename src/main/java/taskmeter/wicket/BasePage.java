package taskmeter.wicket;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import taskmeter.wicket.util.SignOutPage;

public class BasePage extends WebPage {

	private static final long serialVersionUID = 1L;

	protected WebMarkupContainer workingHoursNavLi;
	protected WebMarkupContainer leaderNavLi;
	protected WebMarkupContainer adminNavLi;

	public BasePage() {
		add((workingHoursNavLi = new WebMarkupContainer("workingHoursNavLi")).add(new BookmarkablePageLink<>(
				"workingHoursNavLink", WorkingHoursPage.class)));
		add((leaderNavLi = new WebMarkupContainer("leaderNavLi")).add(new BookmarkablePageLink<>(
				"leaderNavLink", LeaderPage.class)));
		add((adminNavLi = new WebMarkupContainer("adminNavLi")).add(new BookmarkablePageLink<>("adminNavLink",
				AdminPage.class)));

		add(new BookmarkablePageLink<>("home", WicketApplication.get().getHomePage()));

		WebMarkupContainer logoutNavLi = new WebMarkupContainer("logoutNavLi");
		add(logoutNavLi);
		logoutNavLi.setVisible(getSession().isSignedIn());
		logoutNavLi.add(new BookmarkablePageLink<>("logout", SignOutPage.class));
	}

	@Override
	public MySession getSession() {
		return (MySession) super.getSession();
	}
}

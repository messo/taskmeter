package taskmeter.wicket;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.markup.html.WebPage;

import taskmeter.wicket.util.SignInPage;
import taskmeter.wicket.util.SignOutPage;

public class WicketApplication extends AuthenticatedWebApplication {

	@Override
	public Class<? extends WebPage> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return MySession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}

	@Override
	public void init() {
		super.init();

		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

		mountPage("hours", WorkingHoursPage.class);
		mountPage("leader", LeaderPage.class);
		mountPage("admin", SignInPage.class);
		mountPage("logout", SignOutPage.class);
		mountPage("login", SignInPage.class);

		BeanManager manager;
		try {
			manager = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");

			// configure wicket/cdi
			new CdiConfiguration(manager).configure(this);
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}
}

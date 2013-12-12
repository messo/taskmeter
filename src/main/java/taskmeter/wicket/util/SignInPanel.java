package taskmeter.wicket.util;

public class SignInPanel extends org.apache.wicket.authroles.authentication.panel.SignInPanel {

	private static final long serialVersionUID = 1L;

	public SignInPanel(String id, boolean includeRememberMe) {
		super(id, includeRememberMe);
	}

	public SignInPanel(String id) {
		super(id);
	}
}

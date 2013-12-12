package taskmeter.wicket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJB;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import taskmeter.domain.User;
import taskmeter.ejb.UserManager;

public class MySession extends AuthenticatedWebSession {

	private static final long serialVersionUID = 1L;

	@EJB
	UserManager userManager;

	private User currentUser;

	public MySession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String username, String password) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(password.getBytes("UTF-8"));

			User user = userManager.findUserByName(username);

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; ++i) {
				sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
			}

			if (user != null && user.getPassword().toLowerCase().equals(sb.toString())) {
				currentUser = user;
				return true;
			}

			return false;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Roles getRoles() {
		if (currentUser == null) {
			return null;
		}

		return new Roles(userManager.getRolesForUser(currentUser));
	}

	public User getCurrentUser() {
		return currentUser;
	}
}

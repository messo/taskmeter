package taskmeter.ejb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import taskmeter.domain.Project;
import taskmeter.domain.Role;
import taskmeter.domain.User;

@Stateless
@LocalBean
public class UserManager extends AbstractManager<User> {

	private static final String LEADER = "LEADER";

	private static final String PARTICIPANT = "PARTICIPANT";

	@PersistenceContext
	EntityManager em;

	public void magic() {
		Project project = new Project();
		project.setName("magic");
		em.persist(project);
	}

	public User findUserByName(String email) {
		try {
			return em.createNamedQuery(User.FIND_BY_NAME, User.class).setParameter("email", email).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public String[] getRolesForUser(User currentUser) {
		User user = em.find(User.class, currentUser.getId());
		List<Role> roles = user.getRoles();

		String[] result = new String[roles.size()];
		int i = 0;
		for (Role role : roles) {
			result[i++] = role.getRoleType();
		}

		return result;
	}

	public List<User> getLeaders() {
		return getByRole(LEADER);
	}

	public List<User> getParticipants() {
		return getByRole(PARTICIPANT);
	}

	private List<User> getByRole(String roleName) {
		Role role = em.createNamedQuery(Role.FIND_BY_NAME, Role.class).setParameter("name", roleName).getSingleResult();
		// mivel a role oldalt nem frissítjük -- nehézkes, ezért itt kell egy refresh.
		em.refresh(role);
		return role.getUsers();
	}

	public List<User> getUsers() {
		return em.createNamedQuery(User.FIND_ALL, User.class).getResultList();
	}

	public User update(User u) {
		// új user vagy régi?
		if (u.getId() == null) {
			u.setPassword(getMD5(u.getPassword()));
			em.persist(u);
			return u;
		} else {
			User user = em.find(User.class, u.getId());
			if (u.getPassword() == null || u.getPassword().trim().isEmpty()) {
				// nincs új jelszó
				user.setEmail(u.getEmail());
				user.setFirstName(u.getFirstName());
				user.setLastName(u.getLastName());
				return em.merge(user);
			} else {
				// jelszó is új, az egészet mergeöljük, de előtte md5.
				u.setPassword(getMD5(u.getPassword()));
				return em.merge(u);
			}
		}
	}

	private String getMD5(String password) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");

			byte[] digest = md5.digest(password.getBytes("UTF-8"));

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; ++i) {
				sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public List<Role> getRoles() {
		return em.createNamedQuery(Role.FIND_ALL, Role.class).getResultList();
	}

	public List<Role> getRolesByUser(User currentUser) {
		User user = em.find(User.class, currentUser.getId());
		return user.getRoles();
	}

	public void setNewRolesFor(List<Role> newRoles, User currentUser) {
		User user = em.find(User.class, currentUser.getId());

		// List<Role> removedRoles = new LinkedList<>(user.getRoles());
		// removedRoles.removeAll(newRoles); // töröltek
		// for (Role removedRole : removedRoles) {
		// removedRole.getUsers().remove(currentUser);
		// em.merge(removedRole);
		// }
		//
		// List<Role> addedRoles = new LinkedList<>(newRoles);
		// addedRoles.removeAll(user.getRoles()); // most hozzáadottak
		// for (Role addedRole : addedRoles) {
		// addedRole.getUsers().add(currentUser);
		// em.merge(addedRole);
		// }

		user.setRoles(newRoles);
		em.merge(user);
	}
}

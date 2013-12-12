package taskmeter.ejb;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import taskmeter.domain.Project;
import taskmeter.domain.UnitOfWork;
import taskmeter.domain.User;
import taskmeter.ws.model.WorkingSlot;

@Stateless
@LocalBean
public class ProjectManager extends AbstractManager<Project> {

	public void createNewProject(String projectName, User leader) {
		Project project = new Project();
		project.setName(projectName);
		project.setLeader(leader);
		em.persist(project);
	}

	public List<Project> getProjects() {
		return em.createNamedQuery(Project.FIND_ALL, Project.class).getResultList();
	}

	public List<UnitOfWork> getUOWsByUserOrderedByProject(User user) {
		return em.createNamedQuery(UnitOfWork.FIND_BY_USER_ORDER_BY_PROJECT, UnitOfWork.class)
				.setParameter("userId", user.getId()).getResultList();
	}

	public List<Project> getProjectsByLeader(User currentUser) {
		return em.createNamedQuery(Project.FIND_BY_LEADER, Project.class).setParameter("leaderId", currentUser.getId())
				.getResultList();
	}

	public List<UnitOfWork> getUOWsByProject(Project project) {
		return em.createNamedQuery(UnitOfWork.FIND_BY_PROJECT, UnitOfWork.class)
				.setParameter("projectId", project.getId()).getResultList();
	}

	public List<User> getParticipants(Project project) {
		return em.find(Project.class, project.getId()).getParticipants();
	}

	public void removeParticipant(Project p, User participant) {
		Project project = em.find(Project.class, p.getId());
		project.getParticipants().remove(participant);
		em.find(User.class, participant.getId()).getProjectsAsParticipants().remove(project);
	}

	public void addParticipant(Project p, User participant) {
		Project project = em.find(Project.class, p.getId());
		project.getParticipants().add(participant);
		em.find(User.class, participant.getId()).getProjectsAsParticipants().add(project);
	}

	public List<Project> getProjectsByParticipantId(int userId) {
		User user = em.find(User.class, userId);
		return user.getProjectsAsParticipants();
	}

	public List<Integer> synchronize(List<WorkingSlot> slots) {
		List<Integer> magic = new LinkedList<Integer>();

		for (WorkingSlot ws : slots) {
			User user = em.find(User.class, ws.getUserID());
			Project project = em.find(Project.class, ws.getProjectID());

			// ha valamelyik nem található
			if (user == null || project == null)
				continue;

			// ezeket kell elmenteni.
			UnitOfWork uow = new UnitOfWork();
			uow.setUser(user);
			uow.setProject(project);
			uow.setDescription("-- programból mentve --");
			uow.setFromDate(new Timestamp(ws.getStartTimestamp()));
			uow.setToDate(new Timestamp(ws.getStopTimestamp()));
			em.persist(uow);

			magic.add(ws.getId());
		}

		return magic;
	}
}

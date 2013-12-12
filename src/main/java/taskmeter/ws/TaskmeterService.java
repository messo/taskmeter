package taskmeter.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import taskmeter.domain.Project;
import taskmeter.domain.User;
import taskmeter.ejb.ProjectManager;
import taskmeter.ejb.UserManager;
import taskmeter.ws.model.WorkingSlot;

@WebService(serviceName = "TaskmeterWS")
public class TaskmeterService {

	@EJB
	ProjectManager projectManager;

	@EJB
	UserManager userManager;

	@WebMethod(operationName = "listProjects")
	public List<taskmeter.ws.model.Project> listProjects(int userId) {
		List<Project> projects = projectManager.getProjectsByParticipantId(userId);

		List<taskmeter.ws.model.Project> result = new ArrayList<>(projects.size());
		for (Project project : projects) {
			result.add(new taskmeter.ws.model.Project(project.getId(), project.getName()));
		}

		return result;
	}

	@WebMethod(operationName = "login")
	public int login(String username, String password) {
		User user = userManager.findUserByName(username);

		if (user == null)
			return -1;

		if (user.getPassword().toLowerCase().equals(password.toLowerCase())) {
			return user.getId();
		} else {
			return -1;
		}
	}

	@WebMethod(operationName = "synchronize")
	public List<Integer> synchronize(List<WorkingSlot> slots) {
		return projectManager.synchronize(slots);
	}
}

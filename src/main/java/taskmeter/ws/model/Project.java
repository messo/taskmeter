package taskmeter.ws.model;

public class Project {

	private int projectID;
	private String projectName;

	public Project() {
	}

	public Project(int projectID, String projectName) {
		setProjectID(projectID);
		setProjectName(projectName);
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}

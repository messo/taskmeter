package taskmeter.ws.model;

public class WorkingSlot implements Comparable<WorkingSlot> {

	private int id;
	private int projectID, userID;
	private long startTimestamp, stopTimestamp;

	public WorkingSlot() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getStopTimestamp() {
		return stopTimestamp;
	}

	public void setStopTimestamp(long stopTimestamp) {
		this.stopTimestamp = stopTimestamp;
	}

	@Override
	public int compareTo(WorkingSlot o) {
		return (this.startTimestamp < o.startTimestamp ? -1 : (this.startTimestamp > o.startTimestamp ? 1 : 0));
	}

	@Override
	public String toString() {
		return "{#" + id + " --- #" + projectID + " project of (#" + userID + ") user: " + startTimestamp + "-"
				+ stopTimestamp + " duration: " + getDuration() + "}";
	}

	public long getDuration() {
		return stopTimestamp - startTimestamp;
	}
}

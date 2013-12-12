package taskmeter.domain;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "unit_of_works")
@NamedQueries({
		@NamedQuery(name = UnitOfWork.FIND_BY_USER_ORDER_BY_PROJECT, query = "SELECT u FROM UnitOfWork u WHERE u.user.id = :userId ORDER BY u.project.id, u.fromDate, u.toDate"),
		@NamedQuery(name = UnitOfWork.FIND_BY_PROJECT, query = "SELECT u FROM UnitOfWork u WHERE u.project.id = :projectId ORDER BY u.fromDate, u.toDate") })
public class UnitOfWork implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_USER_ORDER_BY_PROJECT = "UnitOfWork.findByUserOrderByProject";

	public static final String FIND_BY_PROJECT = "UnitOfWork.findByProjet";

	@Id
	private int id;

	@Lob
	private String description;

	@Column(name = "from_date")
	private Timestamp fromDate;

	@Column(name = "to_date")
	private Timestamp toDate;

	// bi-directional many-to-one association to Project
	@ManyToOne
	private Project project;

	// bi-directional many-to-one association to User
	@ManyToOne
	private User user;

	public UnitOfWork() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getFromDate() {
		return this.fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return this.toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UnitOfWork [id=" + id + ", description=" + description + ", fromDate=" + fromDate + ", toDate="
				+ toDate + "]";
	}
}
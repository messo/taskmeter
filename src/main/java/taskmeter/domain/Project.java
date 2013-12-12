package taskmeter.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the projects database table.
 * 
 */
@Entity
@Table(name = "projects")
@NamedQueries({ @NamedQuery(name = Project.FIND_ALL, query = "SELECT p FROM Project p"),
		@NamedQuery(name = Project.FIND_BY_LEADER, query = "SELECT p FROM Project p WHERE p.leader.id = :leaderId") })
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "Project.findAll";

	public static final String FIND_BY_LEADER = "Project.findByLeader";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "leader_id")
	private User leader;

	// bi-directional many-to-one association to UnitOfWork
	@OneToMany(mappedBy = "project")
	private List<UnitOfWork> unitOfWorks;

	// bi-directional many-to-many association to User
	@ManyToMany
	@JoinTable(name = "users_are_on_project", joinColumns = { @JoinColumn(name = "project_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	private List<User> participants;

	public Project() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getLeader() {
		return this.leader;
	}

	public void setLeader(User user) {
		this.leader = user;
	}

	public List<UnitOfWork> getUnitOfWorks() {
		return this.unitOfWorks;
	}

	public void setUnitOfWorks(List<UnitOfWork> unitOfWorks) {
		this.unitOfWorks = unitOfWorks;
	}

	public UnitOfWork addUnitOfWork(UnitOfWork unitOfWork) {
		getUnitOfWorks().add(unitOfWork);
		unitOfWork.setProject(this);

		return unitOfWork;
	}

	public UnitOfWork removeUnitOfWork(UnitOfWork unitOfWork) {
		getUnitOfWorks().remove(unitOfWork);
		unitOfWork.setProject(null);

		return unitOfWork;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}
}

package taskmeter.domain;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name = "users")
@NamedQueries({ @NamedQuery(name = User.FIND_BY_NAME, query = "SELECT u FROM User u WHERE u.email = :email"),
		@NamedQuery(name = User.FIND_ALL, query = "SELECT u FROM User u ORDER by u.lastName, u.firstName") })
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_NAME = "User.findByName";

	public static final String FIND_ALL = "User.findAll";

	@Id
	private Integer id;

	private String email;

	private String firstName;

	private String lastName;

	private String password;

	// bi-directional many-to-one association to Project
	@OneToMany(mappedBy = "leader")
	private List<Project> projectsAsLeader;

	// bi-directional many-to-one association to UnitOfWork
	@OneToMany(mappedBy = "user")
	private List<UnitOfWork> unitOfWorks;

	// bi-directional many-to-many association to Role
	@ManyToMany
	@JoinTable(name = "users_has_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private List<Role> roles;

	// bi-directional many-to-many association to Project
	@ManyToMany(mappedBy = "participants")
	private List<Project> projectsAsParticipants;

	public User() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Project> getProjectsAsLeader() {
		return this.projectsAsLeader;
	}

	public void setProjectsAsLeader(List<Project> projects1) {
		this.projectsAsLeader = projects1;
	}

	public Project addProjectAsLeader(Project project) {
		getProjectsAsLeader().add(project);
		project.setLeader(this);

		return project;
	}

	public Project removeProjectAsLeader(Project project) {
		getProjectsAsLeader().remove(project);
		project.setLeader(null);

		return project;
	}

	public List<UnitOfWork> getUnitOfWorks() {
		return this.unitOfWorks;
	}

	public void setUnitOfWorks(List<UnitOfWork> unitOfWorks) {
		this.unitOfWorks = unitOfWorks;
	}

	public UnitOfWork addUnitOfWork(UnitOfWork unitOfWork) {
		getUnitOfWorks().add(unitOfWork);
		unitOfWork.setUser(this);

		return unitOfWork;
	}

	public UnitOfWork removeUnitOfWork(UnitOfWork unitOfWork) {
		getUnitOfWorks().remove(unitOfWork);
		unitOfWork.setUser(null);

		return unitOfWork;
	}

	public List<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Project> getProjectsAsParticipants() {
		return projectsAsParticipants;
	}

	public void setProjectsAsParticipants(List<Project> projectsAsParticipants) {
		this.projectsAsParticipants = projectsAsParticipants;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s %s", lastName, firstName);
	}
}

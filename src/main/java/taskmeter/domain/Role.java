package taskmeter.domain;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name = "roles")
@NamedQueries({ @NamedQuery(name = Role.FIND_BY_NAME, query = "SELECT r FROM Role r WHERE r.roleType = :name"),
		@NamedQuery(name = Role.FIND_ALL, query = "SELECT r FROM Role r ORDER BY r.description") })
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_NAME = "Role.findByName";

	public static final String FIND_ALL = "Role.findAll";

	@Id
	private int id;

	private String description;

	private String roleType;

	// bi-directional many-to-many association to User
	@ManyToMany(mappedBy = "roles")
	private List<User> users;

	public Role() {
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

	public String getRoleType() {
		return this.roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Role other = (Role) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
package taskmeter.ejb;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractManager<T> {

	@PersistenceContext
	EntityManager em;

	public void removeById(int id, Class<T> clazz) {
		em.remove(em.find(clazz, id));
	}

	public void remove(T entity) {
		em.remove(entity);
	}

	public T merge(T object) {
		return em.merge(object);
	}
}

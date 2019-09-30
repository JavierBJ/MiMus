package model;

/**
 * A Hierarchical Unit is a type of Unit which is stored
 * on the DB using a superclass-subclass hierarchy. 
 * 
 * These units have two IDs: one for the superclass table,
 * which stores information common to all members of the
 * hierarchy, and which corresponds with Unit.getId(); and another
 * for the subclass table, which corresponds with 
 * HierarchicalUnit.getSpecificId().
 * 
 * @author Javier Beltrán Jorba
 *
 */
public interface IHierarchicalUnit {
	
	public int getSpecificId();
	
	public void setSpecificId(int id);
	
}

package com.quantego.clp;

/**
 * Holds a constraint of a {@link CLP} model.
 * @author Nils Loehndorf
 *
 */
public class CLPConstraint {
	
	CLP _solver;
	TYPE _type;
	String _name;
	int _index;
	double _rhs;
	
	CLPConstraint(CLP solver, int index, TYPE type) {
		_solver = solver;
		_index = index;
		_type = type;
	}
	
	/**
	 * Constraint name
	 * @param name 
	 * @return
	 */
	public CLPConstraint name(String name) {
		_solver.setConstraintName(this, name);
		return this;
	}
	
	/**
	 * Set the left-hand side coefficient of the given variable. Throws an exception if the variable is not in the constraint.
	 * @param variable 
	 * @param value
	 * @return
	 */
	public CLPConstraint setLhs(CLPVariable variable, double value) {
		_solver.setConstraintCoefficient(this, variable, value);
		return this;
	}
	
	/**
	 * Set the right-hand side of this constraint.
	 * @param value
	 * @return
	 */
	public CLPConstraint setRhs(double value) {
		_rhs = value;
		switch(_type) {
		case EQ:
			_solver.setConstraintBounds(this, value, value);
			break;
		case GEQ:
			_solver.setConstraintLowerBound(this, value);
			break;
		case LEQ:
			_solver.setConstraintUpperBound(this, value);
			break;
		default:
			break;
		}
		return this;
	}
	
	/**
	 * Set the constraint as free.
	 * @return
	 */
	public CLPConstraint free() {
		_solver.setConstraintBounds(this, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return this;
	}
	
	/**
	 * 
	 * @return Get the dual solution value of this constraint.
	 */
	public double getSolution() {
		return _solver.getDualSolution(this);
	}
	
	/**
	 * 
	 * @return the right-hand side coeffcient of this constraint
	 */
	public double getRhs() {
		return _rhs;
	}
	
	/**
	 * Constraint type.
	 * @author Nils Loehndorf
	 *
	 */
	public enum TYPE {
		LEQ, GEQ, EQ, NEQ
	}
	
	@Override
	public String toString() {
		return _solver.getConstraintName(_index);
	}

	
}

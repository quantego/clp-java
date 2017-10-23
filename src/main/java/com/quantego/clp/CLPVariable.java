package com.quantego.clp;

/**
 * Holds a variable of a {@link CLP} model.
 * @author Nils Loehndorf
 *
 */
public class CLPVariable {
	
	CLP _solver;
	int _index;
	
	CLPVariable(CLP solver, int index) {
		_solver = solver;
		_index = index;
	}
	
	/**
	 * Variable name
	 * @param name
	 * @return builder
	 */
	public CLPVariable name(String name) {
		_solver.setVariableName(this, name);
		return this;
	}
	
	/**
	 * Objective coefficient of this variable.
	 * @param value
	 * @return builder
	 */
	public CLPVariable obj(double value) {
		_solver.setObjectiveCoefficient(this,value);
		return this;
	}
	
	/**
	 * Quadratic objective coefficient of this variable.
	 * @param value
	 * @return builder
	 */
	public CLPVariable quad(double value) {
		_solver.setQuadraticObjectiveCoefficient(this,value);
		return this;
	}
	
	/**
	 * Set variable bounds. By default variables are 0 <= x <= inf.
	 * @param lb
	 * @param ub
	 * @return builder
	 */
	public CLPVariable bounds(double lb, double ub) {
		_solver.setVariableBounds(this, lb, ub);
		return this;
	}
	
	/**
	 * Set lower bound. By default variables are 0 <= x <= inf.
	 * @param lb
	 * @param ub
	 * @return builder
	 */
	public CLPVariable lb(double value) {
		_solver.setVariableLowerBound(this, value);
		return this;
	}
	
	/**
	 * Set upper bound. By default variables are 0 <= x <= inf.
	 * @param lb
	 * @param ub
	 * @return builder
	 */
	public CLPVariable ub(double value) {
		_solver.setVariableUpperBound(this, value);
		return this;
	}
	
	/**
	 * Defines this vaiable as free, -inf <= x <= inf.
	 * @return builder
	 */
	public CLPVariable free() {
		_solver.setVariableBounds(this, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return this;
	}
	
	/**
	 * 
	 * @return the solution value
	 */
	public double getSolution() {
		return _solver.getSolution(this);
	}
	
	@Override
	public String toString() {
		return _solver.getVariableName(_index);
	}

}

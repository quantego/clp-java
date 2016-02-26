package com.quantego.clp;

/**
 * Holds a constraint of a {@link CLP} model.
 * @author Nils Loehndorf
 *
 */
public class CLPObjective {
	
	CLP _solver;
	
	CLPObjective(CLP solver) {
		_solver = solver;
	}
	
	/**
	 * Set the coefficient of the given variable.
	 * @param variable 
	 * @param value
	 * @return builder
	 */
	public CLPObjective setTerm(CLPVariable variable, double value) {
		_solver.setObjectiveCoefficient(variable, value);
		return this;
	}
	
	/**
	 * Set the coefficient of the given variable.
	 * @param variable 
	 * @param value
	 * @return builder
	 */
	public CLPObjective setTerm(double value) {
		_solver.setObjectiveOffset(value);
		return this;
	}
	
	
	
}

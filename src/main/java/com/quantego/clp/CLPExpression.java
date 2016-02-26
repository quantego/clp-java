package com.quantego.clp;

import java.util.Map;
import java.util.HashMap;

/**
 * Builder class to create the left-hand side of a {@link CLPConstraint}. Use {@link CLP#buildExpression()} to create a new instance.
 * @author Nils Loehndorf
 *
 */
public class CLPExpression {
	
	CLP _solver;
	Map<CLPVariable,Double> _terms = new HashMap<>();
	double _constant;
	
	CLPExpression(CLP solver) {
		_solver = solver;
	}
	
	/**
	 * Add a constant term to the expression. Terms are collected automatically.
	 * @param value
	 * @return builder
	 */
	public CLPExpression add(double value) {
		_constant += value;
		return this;
	}
	
	/**
	 * Add a new term to the expression. Terms are collected automatically.
	 * @param value
	 * @param variable
	 * @return builder
	 */
	public CLPExpression add(double value, CLPVariable variable) {
		Double old;
		if ((old = _terms.get(variable)) != null)
			value += old.doubleValue();
		_terms.put(variable,value);
		return this;
	}
	
	/**
	 * Add a new term to the expression. Terms are collected automatically.
	 * @param value
	 * @param variable
	 * @return builder
	 */
	public CLPExpression add(CLPVariable variable, double value) {
		return add(value,variable);
	}
	
	/**
	 * Add the sum of multiple variables.
	 * @param value
	 * @param variable
	 * @return builder
	 */
	public CLPExpression add(CLPVariable... variable) {
		for (CLPVariable var : variable)
			add(1.,var);
		return this;
	}
	
	/**
	 * Add the sum of multiple variables, each multiplied by the same scalar.
	 * @param value
	 * @param variable
	 * @return builder
	 */
	public CLPExpression add(double value, CLPVariable... variable) {
		for (CLPVariable var : variable)
			add(value,var);
		return this;
	}
	
	/**
	 * Add the sum of multiple terms.
	 * @param value
	 * @param variable
	 * @return builder
	 */
	public CLPExpression add(double[] values, CLPVariable[] variables) {
		if (values.length != variables.length)
			throw new IllegalArgumentException("Arrays must be of equal length.");
		for (int i=0; i<values.length; i++)
			add(values[i],variables[i]);
		return this;
	}
	
	/**
	 * Add the sum of multiple terms.
	 * @param variables
	 * @return builder
	 */
	public CLPExpression add(Map<CLPVariable,Double> variables) {
		for (CLPVariable var : variables.keySet()) {
			add(variables.get(var),var);
		}
		return this;
	}
	
	/**
	 * Add this expression as less-or-equal constraint to the {@link CLP} model.
	 * @param value right-hand side
	 * @return this expression as {@link CLPConstraint}
	 */
	public CLPConstraint leq(double value) {
		return _solver.addConstraint(_terms, CLPConstraint.TYPE.LEQ, value-_constant);
	}
	
	/**
	 * Add this expression as not-equal constraint to the {@link CLP} model.
	 * @param value right-hand side
	 * @return this expression as {@link CLPConstraint}
	 */
	public CLPConstraint neq(double value) {
		return _solver.addConstraint(_terms, CLPConstraint.TYPE.NEQ, value-_constant);
	}
	
	/**
	 * Add this expression as greater-or-equal constraint to the {@link CLP} model.
	 * @param value right-hand side
	 * @return this expression as {@link CLPConstraint}
	 */
	public CLPConstraint geq(double value) {
		return _solver.addConstraint(_terms, CLPConstraint.TYPE.GEQ, value-_constant);
	}
	
	/**
	 * Add this expression as equality constraint to the {@link CLP} model.
	 * @param value right-hand side
	 * @return this expression as {@link CLPConstraint}
	 */
	public CLPConstraint eq(double value) {
		return _solver.addConstraint(_terms, CLPConstraint.TYPE.EQ, value-_constant);
	}
	
	/**
	 * Set this expression as objective function of the {@link CLP} model.
	 * @return this expression as {@link CLPObjective}
	 */
	public CLPObjective asObjective() {
		return _solver.addObjective(_terms, _constant);
	}
	
	@Override
	public String toString() {
		String str = "";
		for (CLPVariable var : _terms.keySet()) 
			str += CLP.termToString(_terms.get(var),var.toString())+" ";
		str.trim();
		if (str.startsWith("+"))
			return str.substring(2);
		return str;
	}
	
}

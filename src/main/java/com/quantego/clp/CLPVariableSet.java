package com.quantego.clp;


/**
 * Builder class for a set of {@link CLPVariable} with identical parameters.
 * @author Nils Loehndorf
 *
 */
public class CLPVariableSet {
	
	CLP _solver;
	int _size;
	double _lb;
	double _ub = Double.POSITIVE_INFINITY;
	double _obj;
	String _name;
	
	CLPVariableSet(CLP solver, int size) {
		if (size<1) throw new IllegalArgumentException("The size of the set of variables must be greater than 1.");
		_solver = solver;
		_size = size;
	}
	
	/**
	 * Common name of these variables. An underscore followed by the index will be added automaticall.y
	 * @param name
	 * @return builder
	 */
	public CLPVariableSet name(String name) {
		_name = name;
		return this;
	}
	
	/**
	 * Objective coefficient for these variables.
	 * @param value
	 * @return builder
	 */
	public CLPVariableSet obj(double value) {
		_obj = value;
		return this;
	}
	
	/**
	 * Lower bounds of these variables. By default variables are 0 <= x <= inf.
	 * @param lb
	 * @param ub
	 * @return builder
	 */
	public CLPVariableSet lb(double value) {
		_lb = value;
		return this;
	}
	
	/**
	 * Upper bounds of these variables. By default variables are 0 <= x <= inf.
	 * @param lb
	 * @param ub
	 * @return builder
	 */
	public CLPVariableSet ub(double value) {
		_ub = value;
		return this;
	}
	
	/**
	 * Defines this vaiable as free, -inf <= x <= inf.
	 * @return builder
	 */
	public CLPVariableSet free() {
		_lb = Double.NEGATIVE_INFINITY;
		_ub = Double.POSITIVE_INFINITY;
		return this;
	}
	
	/**
	 * @return a list of {@link CLPVariable} with the given parameters.
	 */
	public CLPVariable[] build() {
		CLPVariable[] set = new CLPVariable[_size];
		for (int i=0; i<_size; i++) {
			CLPVariable var = _solver.addVariable();
			if (_name!=null) var.name(_name+"_"+i);
			if (_lb!=0)var.lb(_lb);
			if (_ub!=Double.POSITIVE_INFINITY) var.ub(_ub);
			if (_obj!=0) var.obj(_obj);
			set[i] = var;
		}
		return set;
	}

}

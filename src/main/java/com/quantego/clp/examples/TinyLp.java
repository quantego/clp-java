package com.quantego.clp.examples;

import com.quantego.clp.CLP;
import com.quantego.clp.CLPConstraint;
import com.quantego.clp.CLPVariable;

public class TinyLp {

	public static void main(String[] args) {
		CLP solver = new CLP();
		CLPVariable x0 = solver.addVariable().ub(1.0);
	    CLPVariable x1 = solver.addVariable().ub(0.3);
		CLPVariable x2 = solver.addVariable().ub(0.3);
		CLPVariable x3 = solver.addVariable().free();
		//obj: + 2.655523 x_1 - 2.70917 x_2 + x_3
		solver.createExpression().add(2.655523,x1).add(-2.70917,x2).add(x3).asObjective();
		//ctr_0: - 3.4843450000000002 x_0 + x_3 <= 0.302499
	    solver.createExpression().add(-2,x0).add(-1.484345,x0).add(x3).leq(0.302499);
	    //ctr_1: - 3.074807 x_0 + x_3 <= 0.507194
	    solver.createExpression().add(-3.074807,x0).add(x3).leq(0.507194);
	    //ctr_2: + x_0 + 1.01 x_1 - 0.99 x_2 = 0.594
	    solver.createExpression().add(x0).add(1.01,x1).add(-.99,x2).eq(0.594);
	    solver.maximize();
	    solver.printModel();
	    System.out.println("Objective value: "+solver.getObjectiveValue());
	}

}

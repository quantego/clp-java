package com.quantego.clp;

import static org.junit.Assert.*;

import org.junit.Test;

public class CLPTest {

	@Test
	public void testCLP() {
		CLP solver = new com.quantego.clp.CLP().buffer(2).maximization();
	    CLPVariable x0 = solver.addVariable().ub(1.0);
	    CLPVariable x1 = solver.addVariable().ub(0.3).obj(2.655523).name("var_1");
	    CLPVariable x2 = solver.addVariable().ub(0.3).obj(-2.70917);
	    CLPVariable x3 = solver.addVariable().free().obj(1);
	    solver.createExpression().add(-2,x0).add(-1.484345,x0).add(x3).leq(0.302499);
	    solver.createExpression().add(-3.074807,x0).add(x3).leq(0.507194);
	    solver.createExpression().add(x0).add(1.01,x1).add(-.99,x2).eq(0.594).name("eq_ctr");
	    String str = "Maximize\n"
	    		+ "obj: + 2.655523 var_1 - 2.70917 x_2 + x_3\n"
	    		+ "Subject To\n"
	    		+ "ctr_0: - 3.4843450000000002 x_0 + x_3 <= 0.302499\n"
	    		+ "ctr_1: - 3.074807 x_0 + x_3 <= 0.507194\n"
	    		+ "eq_ctr: + x_0 + 1.01 var_1 - 0.99 x_2 = 0.594\n"
	    		+ "Bounds\n"
	    		+ "x_0 <= 1.0\n"
	    		+ "var_1 <= 0.3\n"
	    		+ "x_2 <= 0.3\n"
	    		+ "-inf <= x_3 <= inf\n"
	    		+ "End";
	    assertEquals(str,solver.toString());
	}

}

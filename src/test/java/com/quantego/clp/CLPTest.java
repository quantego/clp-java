package com.quantego.clp;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.quantego.clp.CLP.ALGORITHM;


public class CLPTest {
	
	@Test
	public void testBuffers() {
		CLP model = new CLP().buffer(10).presolve(false).maxIterations(1);
		int size1 = 10;
		int size2 = 1000;
		Random gen = new Random(11);
		CLPVariable[][] massTransport = new CLPVariable[size1][size2];
		for (int i=0; i<size1; i++) {
			double rnd = gen.nextGaussian();
			for (int j=0; j<size2; j++) 
				massTransport[i][j] = model.addVariable()
				.ub(1./size2)
				.obj(Math.pow(gen.nextGaussian()-rnd,2)); //L2-Wasserstein distance
			
		}
		for (int i=0; i<size1; i++) 
			model.createExpression().add(massTransport[i]).eq(1./size1);
		for (int j=0; j<size2; j++) {
			CLPExpression e = model.createExpression();
			for (int i=0; i<size1; i++) 
				e.add(massTransport[i][j]);
			e.eq(1./size2);
		}
		CLP.STATUS ret = model.minimize();
		assertTrue(ret==CLP.STATUS.LIMIT);
	}

	@Test
	public void testToString() {
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
	
	@Test
	public void testQuad() {
		CLP clp = new CLP().maximization();
		CLPVariable var = clp.addVariable().obj(2).quad(-1);
		clp.solve();
		assertTrue(clp.getObjectiveValue()==1);
		assertTrue(var.getSolution()==1);
	}

}

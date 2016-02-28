package com.quantego.clp;

import java.util.*;

import org.bridj.Pointer;

import com.quantego.clp.CLPConstraint.TYPE;
import com.quantego.clp.CLPNative.*;

/**
 * Java interface for the CLP linear solver. 
 * @author Nils Loehndorf
 *
 */
public class CLP {
	
	static {
		NativeLoader.load();
	}
	
	Pointer<CLPSimplex> _model;
	Pointer<CLPSolve> _solve;
	Pointer<Double> _elements;
	int[] _starts;
	int[] _index;
	Pointer<Double> _rowLower;
	Pointer<Double> _rowUpper;
	Pointer<Double> _obj;
	Pointer<Double> _colLower;
	Pointer<Double> _colUpper;
	Pointer<Double> _primal;
	Pointer<Double> _dual;
	
	Map<Integer,String> _varNames = new HashMap<>();
	Map<Integer,String> _ctrNames = new HashMap<>();
	int _numCols;
	int _numRows;
	int _numElements;
	double _objValue = Double.NaN;
	boolean _maximize;
	
	int _bufferSize = 10000;
	double _smallestElement = 1.e-20;
	int _numNativeCols;
	int _numNativeRows;
	ColBuffer _colBuffer = new ColBuffer();
	RowBuffer _rowBuffer = new RowBuffer();
	
	/**
	 * Create an new model instance.
	 */
	public CLP () {
		_model = init();
	}
	
	Pointer<CLPSimplex> init() {
		Pointer<CLPSimplex> model = CLPNative.clpNewModel();
		CLPNative.clpSetLogLevel(model,0);
		CLPNative.clpSetSmallElementValue(model, 0.);
		CLPNative.clpScaling(model, 0);
		return model;
	}
		
	private void flushBuffers() {
		if (_colBuffer.size()>0)
			addCols();
		if (_rowBuffer.size()>0)
			addRows();
	}
	
	private void addRows() {
		CLPNative.clpAddRows(_model, _rowBuffer.size(), 
				Pointer.pointerToDoubles(_rowBuffer.lower()), 
				Pointer.pointerToDoubles(_rowBuffer.upper()), 
				Pointer.pointerToInts(_rowBuffer.starts()), 
				Pointer.pointerToInts(_rowBuffer.columns()), 
				Pointer.pointerToDoubles(_rowBuffer.elements()));
		_numElements += _rowBuffer._elements.size();
		_elements = CLPNative.clpGetElements(_model);
		_index = CLPNative.clpGetIndices(_model).getInts(_numElements);
		_starts = CLPNative.clpGetVectorStarts(_model).getInts(_numCols+1);
		_rowLower = CLPNative.clpGetRowLower(_model);
		for (int i=0; i<_rowBuffer.size(); i++) {
			if (_rowBuffer._lower.get(i)==Double.NEGATIVE_INFINITY)
				_rowLower.set(i+_numNativeRows,Double.NEGATIVE_INFINITY);
		}
		_rowUpper = CLPNative.clpGetRowUpper(_model);
		for (int i=0; i<_rowBuffer.size(); i++) {
			if (_rowBuffer._upper.get(i)==Double.POSITIVE_INFINITY)
				_rowUpper.set(i+_numNativeRows,Double.POSITIVE_INFINITY);
		}
		_rowBuffer = new RowBuffer();
		_dual = CLPNative.clpDualRowSolution(_model);
		_numNativeRows = _numRows;
	}
	
	private void addCols() {
		CLPNative.clpResize(_model, _numNativeRows, _numCols);
		_obj = CLPNative.clpGetObjCoefficients(_model);
		_colLower = CLPNative.clpGetColLower(_model);
		_colUpper = CLPNative.clpGetColUpper(_model);
		for (Integer index : _colBuffer.objectives.keySet())
			_obj.set(index, _colBuffer.objectives.get(index));
		for (Integer index : _colBuffer.lower.keySet())
			_colLower.set(index, _colBuffer.lower.get(index));
		for (Integer index : _colBuffer.upper.keySet())
			_colUpper.set(index, _colBuffer.upper.get(index));
		_colBuffer = new ColBuffer();
		_numNativeCols = _numCols;
		_primal = CLPNative.clpPrimalColumnSolution(_model);
	} 
	
	String getVariableName(int index) {
		String name = _varNames.get(index);
		if (name==null)
			name = "x_"+index;
		return name;
	}
	
	String getConstraintName(int index) {
		String name = _ctrNames.get(index);
		if (name==null)
			name = "ctr_"+index;
		return name;
	}
	
	/**
	 * Store the model in a proprietary CLP format.
	 * @param filename
	 */
	public void storeModel(String filename) {
		CLPNative.clpSaveModel(_model, Pointer.pointerToCString(filename));
	}
	
	/**
	 * 
	 * @param constraint 
	 * @return Get dual solution value.
	 */
	public double getDualSolution(CLPConstraint constraint) {
		if (constraint._index >= _numNativeRows) 
			flushBuffers();
		return _dual.get(constraint._index);
	}
	
	/**
	 * 
	 * @param variable
	 * @return Get solution value.
	 */
	public double getSolution(CLPVariable variable) {
		if (variable._index >= _numNativeCols)
			flushBuffers();
		return _primal.get(variable._index);
	}
	
	/**
	 * Set variable bounds. See {@link CLPVariable#bounds(double, double)}.
	 * @param variable
	 * @param lb
	 * @param ub
	 */
	public void setVariableBounds(CLPVariable variable, Double lb, Double ub) {
		if (variable._index < _numNativeCols) {
			_colLower.set(variable._index,lb);
			_colUpper.set(variable._index,ub);
		}
		else {
			_colBuffer.lower.put(variable._index, lb);
			_colBuffer.upper.put(variable._index, ub);
		}
	}
	
	/**
	 * Set variable lower bound. See {@link CLPVariable#lb(double)}.
	 * @param variable
	 * @param value
	 */
	public void setVariableLowerBound(CLPVariable variable, Double value) {
		value = checkValue(value);
		if (variable._index < _numNativeCols)
			_colLower.set(variable._index,value);
		else
			_colBuffer.lower.put(variable._index, value);
	}
	
	/**
	 * Set variable upper bound. See {@link CLPVariable#ub(double)}.
	 * @param variable
	 * @param value
	 */
	public void setVariableUpperBound(CLPVariable variable, Double value) {
		value = checkValue(value);
		if (variable._index < _numNativeCols)
			_colUpper.set(variable._index,value);
		else
			_colBuffer.upper.put(variable._index, value);
	}
	
	/**
	 * 
	 * @param variable
	 * @param name
	 */
	public void setVariableName(CLPVariable variable, String name) {
		_varNames.put(variable._index,name);
	}
	
	/**
	 * Set the left-hand side coefficient of a constraint. See {@link CLPConstraint#setLhs(CLPVariable, double)}.
	 * @param constraint
	 * @param variable
	 * @param value
	 */
	public void setConstraintCoefficient(CLPConstraint constraint, CLPVariable variable, Double value) {
		if (constraint._index < _numNativeRows) {
			int pos = _starts[variable._index];
			int end = pos + _starts[variable._index+1];
			while(_index[pos++]!=constraint._index) {
				if (pos>=end)
					throw new IllegalStateException(String.format("Constraint %s does not contain variable %s. Coefficient not set.",constraint.toString(),variable.toString()));
			}
			pos--;
			value = checkValue(value);
			_elements.set(pos,value);
		}
		else {
			_rowBuffer.setElement(constraint._index,variable._index, value);
		}
	}
	
	private Double checkValue(Double value) {
		if (Math.abs(value)>=_smallestElement)
			return value;
		return 0.;
	}
	
	/**
	 * Set constraint bounds. See {@link CLPConstraint#setRhs(double)}.
	 * @param constraint
	 * @param lb
	 * @param ub
	 */
	public void setConstraintBounds(CLPConstraint constraint, Double lb, Double ub) {
		lb = checkValue(lb);
		ub = checkValue(ub);
		if (constraint._index < _numNativeRows) {
			_rowLower.set(constraint._index,lb);
			_rowUpper.set(constraint._index,ub);
		}
		else {
			_rowBuffer._lower.set(constraint._index,lb);
			_rowBuffer._upper.set(constraint._index,ub);
			
		}
	}
	
	/**
	 * Set constraint lower bound. See {@link CLPConstraint#setRhs(double)}.
	 * @param constraint
	 * @param value
	 */
	public void setConstraintLowerBound(CLPConstraint constraint, Double value) {
		value = checkValue(value);
		if (constraint._index < _numNativeRows)
			_rowLower.set(constraint._index,value);
		else 
			_rowBuffer._lower.set(constraint._index,value);
	}
	
	/**
	 * Set constraint upper bound. See {@link CLPConstraint#setRhs(double)}.
	 * @param constraint
	 * @param value
	 */
	public void setConstraintUpperBound(CLPConstraint constraint, Double value) {
		value = checkValue(value);
		if (constraint._index < _numNativeRows) 
			_rowUpper.set(constraint._index,value);
		else 
			_rowBuffer._upper.set(constraint._index,value);
			
	}
	
	/**
	 * 
	 * @param constraint
	 * @param name
	 */
	public void setConstraintName(CLPConstraint constraint, String name) {
		_ctrNames.put(constraint._index,name);
	}
	
	/**
	 * Print log to standard out (default=0).
	 * @param level
	 * @return builder
	 */
	public CLP verbose(int level) {
		CLPNative.clpSetLogLevel(_model,level);
		return this;
	}
	
	/**
	 * A number of constraints remain in heap space during model building and will be passed to the native methods when needed.
	 * The buffer gets flushed either when {@link CLP#solve()} gets executed or when the maximum buffer size is reached (default=10000).
	 * @return builder
	 */
	public CLP buffer(int size) {
		_bufferSize = size;
		return this;
	}
	
	/**
	 * Solve as maximization problem.
	 * @return builder
	 */
	public CLP maximization() {
		_maximize = true;
		CLPNative.clpSetOptimizationDirection(_model, -1);
		return this;
	}
	
	/**
	 * Solve as maximization problem.
	 * @return builder
	 */
	public CLP minimization() {
		_maximize = false;
		CLPNative.clpSetOptimizationDirection(_model, 1);
		return this;
	}
	
	/**
	 * Add a new variable to the model
	 * @return 
	 */
	public CLPVariable addVariable() {
		if (_colBuffer.size()>=_bufferSize)
			addCols();
		_colBuffer.addCol();
		return new CLPVariable(this, _numCols++);
	}
	
	/**
	 * Create a set of variables.
	 * @param size
	 * @return
	 */
	public CLPVariableSet addVariables(int size) {
		return new CLPVariableSet(this,size);
	}

	/**
	 * Set the objective coefficient of the given variable.
	 * @param variable
	 * @param value
	 */
	public void setObjectiveCoefficient(CLPVariable variable, Double value) {
		value = checkValue(value);
		if (variable._index < _numNativeCols) 
			_obj.set(variable._index, value);
		else 
			_colBuffer.objectives.put(variable._index, value);
	}
	
	/**
	 * Set objective constant term.
	 * @param value
	 */
	public void setObjectiveOffset(Double value) {
		value = checkValue(value);
		CLPNative.clpSetObjectiveOffset(_model, value);
	}
	
	/**
	 * Create an expression builder to add a constraint or formulate the objective function.
	 * @return builder
	 */
	public CLPExpression createExpression() {
		return new CLPExpression(this);
	}
	
	/**
	 * Set the objective function to the model without using {@link CLP#buildExpression()}. Objective coefficients not set remain unchanged.
	 * @param lhs terms on the left-hand side
	 * @param type constraint type
	 * @param rhs right-hand side coefficient
	 * @return
	 */ 
	public CLPObjective addObjective(Map<CLPVariable,Double> terms, Double offset) {
		for (CLPVariable var : terms.keySet())
			setObjectiveCoefficient(var,terms.get(var));
		setObjectiveOffset(offset);
		return new CLPObjective(this);
	}
	
	/**
	 * Add a new constraint to the model without using {@link CLP#buildExpression()}.
	 * @param lhs terms on the left-hand side
	 * @param type constraint type
	 * @param rhs right-hand side coefficient
	 * @return
	 */
	public CLPConstraint addConstraint(Map<CLPVariable,Double> lhs, TYPE type, Double rhs) {
		if (lhs.isEmpty()) throw new IllegalArgumentException("The constraint does not contain variables.");
		if (_rowBuffer.size()>=_bufferSize)
			flushBuffers();
		_rowBuffer.addRow(lhs,type,rhs);
		return new CLPConstraint(this,_numRows++,type);
	}
	
	/**
	 * Solve the optimization problem. 
	 * @return solution {@link STATUS}
	 */
	public STATUS solve() {
		flushBuffers();
		if (_solve!=null)
			CLPNative.clpInitialSolveWithOptions(_model,_solve);
		else
			CLPNative.clpInitialSolve(_model);
		_objValue = CLPNative.clpGetObjValue(_model);
		int status = CLPNative.clpStatus(_model);
		if (status == 0) 
			return STATUS.OPTIMAL;
		if (status == 1) 
			return STATUS.INFEASIBLE;
		if (status == 2) 
			return STATUS.UNBOUNDED;
		if (status == 3) 
			return STATUS.LIMIT;
		if (status == 4) 
			return STATUS.ERROR;
		return STATUS.UNKNOWN;
	}
	
	/**
	 * Rebuild the model from scratch including
	 */
 	public void reset() {
		flushBuffers();
		Pointer<CLPSimplex> newModel = init(); 
		CLPNative.clpResize(newModel, _numRows, 0);
		CLPNative.clpAddColumns(newModel, 
				_numCols, 
				Pointer.pointerToDoubles(_colLower.getDoubles(_numCols)), 
				Pointer.pointerToDoubles(_colUpper.getDoubles(_numCols)), 
				Pointer.pointerToDoubles(_obj.getDoubles(_numCols)), 
				Pointer.pointerToInts(_starts), 
				Pointer.pointerToInts(_index), 
				Pointer.pointerToDoubles(_elements.getDoubles(_numElements)));
		Pointer<Double> rowLower = _rowLower;
		_rowLower = CLPNative.clpGetRowLower(newModel);
		for (int i=0; i<_numRows; i++)
			_rowLower.set(i,rowLower.get(i));
		Pointer<Double> rowUpper = _rowUpper;
		_rowUpper = CLPNative.clpGetRowUpper(newModel);
		for (int i=0; i<_numRows; i++)
			_rowUpper.set(i,rowUpper.get(i));
		Pointer<Double> colLower = _colLower;
		_colLower = CLPNative.clpGetColLower(newModel);
		for (int i=0; i<_numCols; i++) 
			_colLower.set(i,colLower.get(i));
		Pointer<Double> colUpper = _colUpper;
		_colUpper = CLPNative.clpGetColUpper(newModel);
		for (int i=0; i<_numCols; i++)
			_colUpper.set(i,colUpper.get(i));
		CLPNative.clpSetOptimizationDirection(newModel, _maximize?-1:1);
		_elements = CLPNative.clpGetElements(newModel);
		_index = CLPNative.clpGetIndices(newModel).getInts(_numElements);
		_starts = CLPNative.clpGetVectorStarts(newModel).getInts(_numCols+1);
		_dual = CLPNative.clpDualRowSolution(newModel);
		_obj = CLPNative.clpGetObjCoefficients(newModel);
		_primal = CLPNative.clpPrimalColumnSolution(newModel);
		CLPNative.clpDeleteModel(_model);
		_model = newModel;
	}
	
	/**
	 * Solve the problem as maximization problem.
	 * @return solution {@link STATUS}
	 */
	public STATUS maximize() {
		maximization();
		return solve();
	}
	
	/**
	 * Solve the problem as minimization problem.
	 * @return solution {@link STATUS}
	 */
	public STATUS minimize() {
		minimization();
		return solve();
	}
	
	/**
	 * 
	 * @return the optimal objective value
	 */
	public double getObjectiveValue() {
		return _objValue;
	}
	
	/**
	 * 
	 * @return number of variables in model
	 */
	public int getNumVariables() {
		return _numCols;
	}
	
	/**
	 * 
	 * @return number of constraints in model
	 */
	public int getNumConstraints() {
		return _numRows;
	}
	
	/**
	 * Set the dual tolerance (default=1.e-7).
	 * @param value
	 * @return builder
	 */
	public CLP dualTolerance(double value) {
		CLPNative.clpSetDualTolerance(_model, value);
		return this;
	}
	
	/**
	 * Set the smallest coefficient value considered as non-zero (default=1.e-10). All values smaller than this will be set to zero.
	 * @param value
	 * @return builder
	 */
	public CLP smallestCoefficient(double value) {
		if (value<0)
			throw new IllegalArgumentException("The smallest coefficient must be >= 0");
		_smallestElement = value;
		return this;
	}
	
	/**
	 * Set the primal tolerance (default=1.e-7).
	 * @param value
	 * @return builder
	 */
	public CLP primalTolerance(double value) {
		CLPNative.clpSetPrimalTolerance(_model, value);
		return this;
	}
	
	/**
	 * Set the maximum number of iterations of the solution algorithm.
	 * @param iter
	 * @return builder
	 */
	public CLP maxIterations(int iter) {
		CLPNative.clpSetMaximumIterations(_model, iter);
		return this;
	}
	
	/**
	 * Set the maximum number of seconds for the solution process.
	 * @param seconds
	 * @return builder
	 */
	public CLP maxSeconds(int seconds) {
		CLPNative.clpSetMaximumSeconds(_model, seconds);
		return this;
	}
	
	/**
	 * Define the parameter scaling defined by {@link SCALING} (default=OFF).
	 * @param scaling
	 * @return builder
	 */
	public CLP scaling(SCALING scaling) {
		switch(scaling) {
		case OFF:
			CLPNative.clpScaling(_model, 0);
			break;
		case EQULIBRIUM:
			CLPNative.clpScaling(_model, 1);
			break;
		case GEOMETRIC:
			CLPNative.clpScaling(_model, 2);
			break;
		default:
			CLPNative.clpScaling(_model, 3);
			break;
		}
		return this;
	}
	
	/**
	 * Turn presolve on (default=true).
	 * @param on
	 * @return builder
	 */
	public CLP presolve(boolean on) {
		if (_solve==null) _solve = CLPNative.clpSolveNew();
		CLPNative.clpSolveSetPresolveType(_solve, on ? 0 : 1, -1);
		return this;
	}
	
	/**
	 * Set the solution {@link ALGORITHM} (default=AUTO).
	 * @param algorithm
	 * @return builder
	 */
	public CLP algorithm(ALGORITHM algorithm) {
		if (_solve==null) _solve = CLPNative.clpSolveNew();
		switch(algorithm) {
		case DUAL:
			CLPNative.clpSolveSetSolveType(_solve, 0, -1);
			break;
		case PRIMAL:
			CLPNative.clpSolveSetSolveType(_solve, 1, -1);
			break;
		case PRIMAL_SPRINT:
			CLPNative.clpSolveSetSolveType(_solve, 2, -1);
			break;
		case BARRIER:
			CLPNative.clpSolveSetSolveType(_solve, 3, -1);
			break;
		case BARRIER_NO_CROSSOVER:
			CLPNative.clpSolveSetSolveType(_solve, 4, -1);
			break;
		default:
			CLPNative.clpSolveSetSolveType(_solve, 5, -1);
			break;
		}
		return this;
	}

	/**
	 * Solution algorithm
	 * @author Nils Loehndorf
	 *
	 */
	public enum ALGORITHM {
		AUTO, DUAL, PRIMAL, PRIMAL_SPRINT, BARRIER, BARRIER_NO_CROSSOVER
	}
	
	/**
	 * Solution status
	 * @author Nils Loehndorf
	 *
	 */
	public enum STATUS {
		OPTIMAL, INFEASIBLE, UNBOUNDED, LIMIT, ERROR, UNKNOWN
	}
	
	/**
	 * Parameter scaling
	 * @author Nils Loehndorf
	 *
	 */
	public enum SCALING {
		OFF, EQULIBRIUM, GEOMETRIC, AUTO
	}
	
	@Override
	public void finalize() {
		CLPNative.clpDeleteModel(_model);
		CLPNative.clpSolveDelete(_solve); 
	}
	
	
	
	private int[] toIntArray(List<Integer> ints) {
		int[] ar = new int[ints.size()];
		int i=0;
		for (Integer j : ints)
			ar[i++] = j;
		return ar;
	}
	
	private double[] toDoubleArray(List<Double> doubles) {
		double[] ar = new double[doubles.size()];
		int i=0;
		for (Double d : doubles)
			ar[i++] = d;
		return ar;
	}
	
	/**
	 * Buffer for new variables added to the model.
	 * @author Nils Loehndorf
	 *
	 */
	private class ColBuffer {
		int _size;
		Map<Integer,Double> objectives = new HashMap<>();
		Map<Integer,Double> lower = new HashMap<>();
		Map<Integer,Double> upper = new HashMap<>();
		
		void addCol() {
			_size++;
		}
		
		int size() {
			return _size;
		}
	}
	
	/**
	 * Buffer for new constraints added to the model. 
	 * @author Nils Loehndorf
	 *
	 */
	private class RowBuffer {
		
		List<Integer> _columns = new ArrayList<>();
		List<Integer> _starts = new ArrayList<>();
		List<Double> _elements = new ArrayList<>();
		List<Double> _lower = new ArrayList<>();
		List<Double> _upper = new ArrayList<>();
		
		RowBuffer() {
			_starts.add(0);
		}
		
		int size() {
			return _lower.size();
		}
		
		int[] columns() {
			return toIntArray(_columns);
		}
		
		int[] starts() {
			return toIntArray(_starts);
		}
		
		double[] elements() {
			return toDoubleArray(_elements);
		}
		
		double[] lower() {
			return toDoubleArray(_lower);
		}
		
		double[] upper() {
			return toDoubleArray(_upper);
		}
		
		void setElement(int rowIndex, int colIndex, Double value) {
			checkValue(value);
			int pos = _starts.get(rowIndex);
			int end = pos + _starts.get(rowIndex+1);
			while(_columns.get(pos++)!=colIndex) {
				if (pos>=end)
					throw new IllegalStateException("Constraint does not contain this variable. Must be redefined first.");
			}
			pos--;
			value = checkValue(value);
			_elements.set(pos,value);
		}
		
		void addRow(Map<CLPVariable,Double> lhs, TYPE type, Double rhs) {
			rhs = checkValue(rhs);
			switch(type) {
			case EQ:
				_lower.add(rhs);
				_upper.add(rhs);
				break;
			case GEQ:
				_lower.add(rhs);
				_upper.add(Double.POSITIVE_INFINITY);
				break;
			case LEQ:
				_lower.add(Double.NEGATIVE_INFINITY);
				_upper.add(rhs);
				break;
			default:
				_lower.add(Double.NEGATIVE_INFINITY);
				_upper.add(Double.POSITIVE_INFINITY);
				break;
			}
			_starts.add(_elements.size()+lhs.size());
			for (CLPVariable variable : lhs.keySet()) {
				_columns.add(variable._index);
				Double value = lhs.get(variable);
				value = checkValue(value);
				_elements.add(value);
			}
		}
	}
	
	/**
	 * Print the model in lp format to standard output. 
	 * @see {@link CLP#toString()}
	 */
	public void printModel() {
		System.out.println(toString());
	}
	
	/**
	 * Return the model in lp format.
	 */
	@Override
	public String toString() {
		flushBuffers();
		StringBuilder modelString = new StringBuilder();
		modelString.append(_maximize ? "Maximize" : "Minimize").append("\nobj:");
		//objective function
		for (int col=0; col<_numCols; col++) {
			modelString.append(termToString(_obj.getDoubleAtIndex(col),getVariableName(col)));
		}
		modelString.append("\nSubject To\n");
		//constraints
		List<StringBuilder> constraintStrings = new ArrayList<>(_numRows);
		for (int row=0; row<_numRows; row++)
			constraintStrings.add(new StringBuilder().append(getConstraintName(row)).append(":"));
		for (int col=0; col<_numCols; col++) {
			int begin = _starts[col];
			int end = _starts[col+1];
			for (int j=begin; j<end; j++) {
				int row = _index[j];
				double element = _elements.get(j);
				if (element != 0)
					constraintStrings.get(row).append(termToString(element,getVariableName(col)));
			}
		}
		for (int row=0; row<_numRows; row++) {
			Double lb = _rowLower.get(row);
			Double ub = _rowUpper.get(row);
			if (Double.compare(lb,ub)==0)
				modelString.append(constraintStrings.get(row).append(" = ").append(lb).append("\n"));
			else if (lb==Double.NEGATIVE_INFINITY && ub<Double.POSITIVE_INFINITY)
				modelString.append(constraintStrings.get(row).append(" <= ").append(ub).append("\n"));
			else if (ub==Double.POSITIVE_INFINITY && lb>Double.NEGATIVE_INFINITY)
				modelString.append(constraintStrings.get(row).append(" >= ").append(lb).append("\n"));
		}
		//bounds
		modelString.append("Bounds\n");
		for (int col=0; col<_numCols; col++) {
			Double lb = _colLower.get(col);
			Double ub = _colUpper.get(col);
			if (lb==0 && ub<Double.POSITIVE_INFINITY) 
				modelString.append(getVariableName(col)).append(" <= ").append(ub).append("\n");
			else if (lb==Double.NEGATIVE_INFINITY && ub==Double.POSITIVE_INFINITY)
				modelString.append("-inf <= ").append(getVariableName(col)).append(" <= inf\n");
			else if (lb!=0 && lb>Double.POSITIVE_INFINITY && ub==Double.POSITIVE_INFINITY)
				modelString.append(lb).append(" <= ").append(getVariableName(col)).append(" <= inf").append("\n");
			else if (lb==Double.NEGATIVE_INFINITY && ub<Double.POSITIVE_INFINITY)
				modelString.append("-inf <= ").append(getVariableName(col)).append(" <= ").append(ub).append("\n");
			else if (lb>Double.NEGATIVE_INFINITY && ub<Double.POSITIVE_INFINITY)
				modelString.append(lb).append(" <= ").append(getVariableName(col)).append(" <= ").append(ub).append("\n");
		}
		modelString.append("End");
		return modelString.toString();
	}
	
	static String termToString(double d, String s) {
		if (d==0) return "";
		if (d==1)
			return " + "+s;
		if (d==-1)
			return " - "+s;
		if (d>0)
			return " + "+d+" "+s;
		return " - "+(-d)+" "+s;	
	}

}

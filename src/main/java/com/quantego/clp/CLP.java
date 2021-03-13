package com.quantego.clp;

import com.quantego.clp.CLPConstraint.TYPE;
import com.quantego.clp.CLPNative.CLPSimplex;
import com.quantego.clp.CLPNative.CLPSolve;
import org.bridj.Pointer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Java interface for the CLP linear solver. The implementation provides a light-weight wrapper that 
 * creates as little overhead as possible. If no variables or constraints are referenced, the memory 
 * footprint in Java heap is negligible.</p>
 * 
 * <p>Chunks of a model are buffered in heap for model building before 
 * being sent to the native lib. The size of the buffer can be set with {@link CLP#buffer(int)}. The buffer 
 * helps to formulate models in a row-by-row fashion, without bothering about possible performance bottlenecks. 
 * Models with millions of constraints can be generated quickly.</p>
 * 
 * <p>To update model coefficients, the model is accessed 
 * directly in native memory via direct byte buffers that are provided by the 
 * <a href="https://github.com/nativelibs4java/BridJ">BridJ</a> native interface. 
 * When the model gets gc'ed, native memory will be released automatically.</p>
 * 
 * <p> For debugging a model, use {@link CLP#verbose(int)} to set the internal log level of CLP which will show some solution information
 * during the solution process. {@link CLP#toString()} returns the model as string in .lp format (Xpress style), 
 * and {@link CLP#printModel()} sends it to standard out in the same format.</p> 
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
	Pointer<Double> _rowLower;
	Pointer<Double> _rowUpper;
	Pointer<Double> _obj;
	Pointer<Double> _colLower;
	Pointer<Double> _colUpper;
	Pointer<Double> _primal;
	Pointer<Double> _dual;
	int[] _starts;
	int[] _index;
	
	Map<Integer,String> _varNames = new HashMap<>();
	Map<Integer,String> _ctrNames = new HashMap<>();
	int _numCols;
	int _numRows;
	int _numElements;
	double _objValue = Double.NaN;
	boolean _maximize;
	double _offset;
	
	int _bufferSize = 100000;
	double _smallestElement = 1.e-20;
	int _numNativeCols;
	int _numNativeRows;
	ColBuffer _colBuffer = new ColBuffer();
	RowBuffer _rowBuffer = new RowBuffer();
	QuadraticObjective _qobj;
	
	/**
	 * Create an new model instance.
	 */
	public CLP () {
		_model = init();
	}

	public static CLP createFromMPS(File f) {
		// Make BridJ Pointer to file
		String path = f.toPath().toString()+'\0';
		Pointer<Byte> ptr = Pointer.allocateBytes(path.getBytes().length);
		ptr.setBytes(path.getBytes());

		// Read MPS
		CLP clp = new CLP();
		CLPNative.clpReadMps(clp._model, ptr, 1, 0);

		// Explicitly release manually allocated memory to be on the safe side
		// This is normally freed by finalize method from BridJ
		ptr.release();

		return clp;
	}

	Pointer<CLPSimplex> init() {
		Pointer<CLPSimplex> model = CLPNative.clpNewModel();
		CLPNative.clpSetLogLevel(model,0);
		CLPNative.clpSetSmallElementValue(model, 0.);
		CLPNative.clpScaling(model, 0);
		return model;
	}
		
	private void flushBuffers() {
		CLPNative.clpSetObjectiveOffset(_model, (_maximize?1:-1)*_offset);
		if (_colBuffer.size()>0)
			addCols();
		if (_numRows == 0)
			_rowBuffer.addDummyRow();
		if (_rowBuffer.size()>0)
			addRows();
		if (_qobj != null)
			_qobj.update(_model);	
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
		_rowLower = CLPNative.clpGetRowLower(_model);
		for (int i=0; i<_rowBuffer.size(); i++) {
			if (_rowBuffer._lower.get(i)==Double.NEGATIVE_INFINITY)
				_rowLower.setDoubleAtIndex(i+_numNativeRows,Double.NEGATIVE_INFINITY);
		}
		_rowUpper = CLPNative.clpGetRowUpper(_model);
		for (int i=0; i<_rowBuffer.size(); i++) {
			if (_rowBuffer._upper.get(i)==Double.POSITIVE_INFINITY)
				_rowUpper.setDoubleAtIndex(i+_numNativeRows,Double.POSITIVE_INFINITY);
		}
		_rowBuffer = new RowBuffer();
		_dual = CLPNative.clpDualRowSolution(_model);
		_numNativeRows = _numRows;
		_index = null;
		_starts = null;
	}
	
	private int[] getIndex() {
		if (_index==null)
			_index = CLPNative.clpGetIndices(_model).getInts(_numElements);
		return _index;
		
	}
	
	private int[] getStarts() {
		if (_starts==null)
			_starts = CLPNative.clpGetVectorStarts(_model).getInts(_numCols+1);
		return _starts;
	}
	
	private void addCols() {
		CLPNative.clpResize(_model, _numNativeRows, _numCols);
		_obj = CLPNative.clpGetObjCoefficients(_model);
		_colLower = CLPNative.clpGetColLower(_model);
		_colUpper = CLPNative.clpGetColUpper(_model);
		for (Integer index : _colBuffer.objectives.keySet())
			_obj.setDoubleAtIndex(index, _colBuffer.objectives.get(index));
		for (Integer index : _colBuffer.lower.keySet())
			_colLower.setDoubleAtIndex(index, _colBuffer.lower.get(index));
		for (Integer index : _colBuffer.upper.keySet())
			_colUpper.setDoubleAtIndex(index, _colBuffer.upper.get(index));
		_colBuffer = new ColBuffer();
		_numNativeCols = _numCols;
		_primal = CLPNative.clpPrimalColumnSolution(_model);
	} 
	
	private void neg() {
		if (_colBuffer.size()>0)
			addCols();
		if (_rowBuffer.size()>0)
			addRows();
		for (int col=0; col<_numCols; col++) 
			_obj.setDoubleAtIndex(col, -_obj.getDoubleAtIndex(col));
		if (_qobj != null)
			_qobj.neg();
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
	 * Store the model in a proprietary CLP file format.
	 * @param filename
	 */
	public void storeModel(String filename) {
		CLPNative.clpSaveModel(_model, Pointer.pointerToCString(filename));
	}
	
	/**
	 * Restore a model from a proprietary CLP file format.
	 * @param filename
	 * @throws IOException 
	 */
	public void restoreModel(String filename) throws IOException {
		if (!new File(filename).canRead())
			throw new IOException(String.format("File '%s' does not exist or cannot be read.",filename));
		CLPNative.clpRestoreModel(_model, Pointer.pointerToCString(filename));
		_rowLower = CLPNative.clpGetRowLower(_model);
		_rowUpper = CLPNative.clpGetRowUpper(_model);
		_colLower = CLPNative.clpGetColLower(_model);
		_colUpper = CLPNative.clpGetColUpper(_model);
		_elements = CLPNative.clpGetElements(_model);
		_maximize = CLPNative.clpGetObjSense(_model) == -1;
		_index = null;
		_starts = null;
		_dual = CLPNative.clpDualRowSolution(_model);
		_obj = CLPNative.clpGetObjCoefficients(_model);
		_primal = CLPNative.clpPrimalColumnSolution(_model);
		_numCols = CLPNative.clpGetNumCols(_model);
		_numRows = CLPNative.clpGetNumRows(_model);
		_numNativeCols = _numCols;
		_numNativeRows = _numRows;
		_numElements = CLPNative.clpGetNumElements(_model);
	}
	
	/**
	 * 
	 * @param constraint 
	 * @return Get dual solution value.
	 */
	public double getDualSolution(CLPConstraint constraint) {
		if (constraint._index >= _numNativeRows) 
			flushBuffers();
		return _maximize ? -_dual.getDoubleAtIndex(constraint._index) : _dual.getDoubleAtIndex(constraint._index);
	}
	
	/**
	 * 
	 * @param variable
	 * @return Get solution value.
	 */
	public double getSolution(CLPVariable variable) {
		if (variable._index >= _numNativeCols)
			flushBuffers();
		return _primal.getDoubleAtIndex(variable._index);
	}
	
	/**
	 * Set variable bounds. See {@link CLPVariable#bounds(double, double)}.
	 * @param variable
	 * @param lb
	 * @param ub
	 */
	public void setVariableBounds(CLPVariable variable, double lb, double ub) {
		if (variable._index < _numNativeCols) {
			_colLower.setDoubleAtIndex(variable._index,lb);
			_colUpper.setDoubleAtIndex(variable._index,ub);
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
	public void setVariableLowerBound(CLPVariable variable, double value) {
		value = checkValue(value);
		if (variable._index < _numNativeCols)
			_colLower.setDoubleAtIndex(variable._index,value);
		else
			_colBuffer.lower.put(variable._index, value);
	}
	
	/**
	 * Set variable upper bound. See {@link CLPVariable#ub(double)}.
	 * @param variable
	 * @param value
	 */
	public void setVariableUpperBound(CLPVariable variable, double value) {
		value = checkValue(value);
		if (variable._index < _numNativeCols)
			_colUpper.setDoubleAtIndex(variable._index,value);
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
	public void setConstraintCoefficient(CLPConstraint constraint, CLPVariable variable, double value) {
		if (constraint._index < _numNativeRows) {
			int[] index = getIndex();
			int[] starts = getStarts();
			int pos = starts[variable._index];
			int end = starts[variable._index+1];
			while(index[pos++]!=constraint._index) {
				if (pos>=end)
					throw new IllegalStateException(String.format("Constraint %s does not contain variable %s. Coefficient not set.",constraint.toString(),variable.toString()));
			}
			value = checkValue(value);
			_elements.setDoubleAtIndex(pos-1,value);
		}
		else
			_rowBuffer.setElement(constraint._index,variable._index, value);
	}
	
	private double checkValue(double value) {
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
	public void setConstraintBounds(CLPConstraint constraint, double lb, double ub) {
		lb = checkValue(lb);
		ub = checkValue(ub);
		if (constraint._index < _numNativeRows) {
			_rowLower.setDoubleAtIndex(constraint._index,lb);
			_rowUpper.setDoubleAtIndex(constraint._index,ub);
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
	public void setConstraintLowerBound(CLPConstraint constraint, double value) {
		value = checkValue(value);
		if (constraint._index < _numNativeRows)
			_rowLower.setDoubleAtIndex(constraint._index,value);
		else 
			_rowBuffer._lower.set(constraint._index,value);
	}
	
	/**
	 * Set constraint upper bound. See {@link CLPConstraint#setRhs(double)}.
	 * @param constraint
	 * @param value
	 */
	public void setConstraintUpperBound(CLPConstraint constraint, double value) {
		value = checkValue(value);
		if (constraint._index < _numNativeRows) 
			_rowUpper.setDoubleAtIndex(constraint._index,value);
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
	 * Print CLP log to standard out (default=0).</br>
	 * 0 - none </br>
     * 1 - just final info</br>
     * 2 - basic factorization info</br>
     * 3 - finer factorization info</br>
     * 4 - verbose
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
		if (!_maximize)
			neg();
		_maximize = true;
		return this;
	}
	
	/**
	 * Solve as maximization problem.
	 * @return builder
	 */
	public CLP minimization() {
		if (_maximize)
			neg();
		_maximize = false;
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
	public void setObjectiveCoefficient(CLPVariable variable, double value) {
		value = checkValue(value);
		if (_maximize)
			value = -value;
		if (variable._index < _numNativeCols) 
			_obj.setDoubleAtIndex(variable._index, value);
		else 
			_colBuffer.objectives.put(variable._index, value);
	}
	
	/**
	 * Set the objective coefficient of the given variable.
	 * @param variable
	 * @param value
	 */
	public void setQuadraticObjectiveCoefficient(CLPVariable variable, double value) {
		value = checkValue(value);
		if (_maximize && value>0)
			throw new IllegalArgumentException(String.format(
					"Quadratic objective coefficient of variable %s must not be greater than zero.", variable.toString()));
		if (!_maximize && value<0)
			throw new IllegalArgumentException(String.format(
					"Quadratic objective coefficient of variable %s must not be less than zero.", variable.toString()));
		if (variable._index >= _numCols)
			setObjectiveCoefficient(variable,0);
		if (_qobj == null)
			_qobj = new QuadraticObjective();
		_qobj.put(variable._index, _maximize ? -value : value);
	}
	
	/**
	 * Set objective constant term.
	 * @param value
	 */
	public void setObjectiveOffset(double value) {
		value = checkValue(value);
		_offset = value;
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
	public CLPObjective addObjective(Map<CLPVariable,Double> terms, double offset) {
		for (CLPVariable var : terms.keySet())
			setObjectiveCoefficient(var,terms.get(var));
		setObjectiveOffset(_offset+offset);
		return new CLPObjective(this);
	}
	
	/**
	 * Add a new constraint to the model without using {@link CLP#buildExpression()}.
	 * @param lhs terms on the left-hand side
	 * @param type constraint type
	 * @param rhs right-hand side coefficient
	 * @return
	 */
	public CLPConstraint addConstraint(Map<CLPVariable,Double> lhs, TYPE type, double rhs) {
		if (lhs.isEmpty()) throw new IllegalArgumentException("The constraint does not contain variables.");
		if (_rowBuffer.size()>=_bufferSize)
			flushBuffers();
		_rowBuffer.addRow(lhs,type,rhs);
		return new CLPConstraint(this,_numRows++,type);
	}
	
	/**
	 * Add a new constraint to the model without using {@link CLP#buildExpression()}.
	 * @param lhs terms on the left-hand side
	 * @param type constraint type
	 * @param rhs right-hand side coefficient
	 * @return
	 */
	public CLPConstraint addConstraint(List<CLPVariable> variables, List<Double> lhs, TYPE type, double rhs) {
		if (lhs.size()==0) throw new IllegalArgumentException("The constraint does not contain variables.");
		if (variables.size()!=lhs.size()) throw new IllegalArgumentException("Arrays of unequal size.");
		if (_rowBuffer.size()>=_bufferSize)
			flushBuffers();
		_rowBuffer.addRow(variables,lhs,type,rhs);
		return new CLPConstraint(this,_numRows++,type);
	}
	
	/**
	 * Solve the optimization problem. 
	 * @return solution {@link STATUS}
	 */
	public STATUS solve() {
		//take care of empty problem
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
	 * Rebuild the model from scratch. The previous model is deleted from memory.
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
				Pointer.pointerToInts(getStarts()), 
				Pointer.pointerToInts(getIndex()), 
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
		_elements = CLPNative.clpGetElements(newModel);
		_index = null;
		_starts = null;
		_dual = CLPNative.clpDualRowSolution(newModel);
		if (_qobj != null) 
			CLPNative.clpLoadQuadraticObjective(newModel, _qobj._numElements, _qobj._starts, _qobj._index, _qobj._elements);
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
		return _maximize? -_objValue : _objValue;
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
	 * Set the smallest coefficient value considered as non-zero (default = 1.e-20). All values smaller than this will be set to zero.
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
	
	private class QuadraticObjective {
		HashMap<Integer,Double> _buffer = new HashMap<>();
		private Pointer<Double> _elements;
		private Pointer<Integer> _starts;
		private Pointer<Integer> _index;
		int _numElements;
		boolean _hasChange;
		
		void flush() {
			if (_buffer.isEmpty()) return;
			int[] starts = new int[_numCols+1];
			starts[_numCols] = _numCols;
			int[] index = new int[_numCols];
			for (int i=0; i<_numCols; i++) {
				starts[i] = i;
				index[i] = i;
			}
			_starts = Pointer.pointerToInts(starts);
			_index = Pointer.pointerToInts(index);
			Pointer<Double> elements = Pointer.allocateDoubles(_numNativeCols);
			if (_elements != null)
				_elements.copyTo(elements,_numElements);
			_numElements = _numNativeCols;
			_elements = elements;
			for (Integer i : _buffer.keySet())
				_elements.setDoubleAtIndex(i, _buffer.get(i));
			_buffer.clear();
		}
		
		void update(Pointer<CLPSimplex> model) {
			if (!_hasChange) return;
			flush();
			CLPNative.clpLoadQuadraticObjective(model, _numCols, _starts, _index, _elements);
			_obj = CLPNative.clpGetObjCoefficients(model);
		}
		
		void put(int index, double value) {
			value *= 2;
			if (index < _numElements)
				_elements.setDoubleAtIndex(index, value);
			else
				_buffer.put(index, value);
			_hasChange = true;
		}
		
		double get(int index) {
			return _elements.getDoubleAtIndex(index);
		}
		
		void neg() {
			for (int i=0; i<_numCols; i++)
				_elements.setDoubleAtIndex(i, -_elements.getDoubleAtIndex(i));
			_hasChange = true;
			update(_model);
		}
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
		
		void setElement(int rowIndex, int colIndex, double value) {
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
		
		void addDummyRow() {
			_lower.add(Double.NEGATIVE_INFINITY);
			_upper.add(Double.POSITIVE_INFINITY);
			_starts.add(_elements.size()+1);
			_columns.add(0);
			_elements.add(1.);
			_numRows++;
		}
		
		void addRow(Map<CLPVariable,Double> lhs, TYPE type, double rhs) {
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
				double value = lhs.get(variable);
				value = checkValue(value);
				_elements.add(value);
			}
		}
		
		void addRow(List<CLPVariable> variables, List<Double> lhs, TYPE type, double rhs) {
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
			for (int i=0; i<variables.size(); i++) {
				_columns.add(variables.get(i)._index);
				_elements.add(checkValue(lhs.get(i)));
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
		if (_offset != 0) modelString.append(" "+_offset);
		//objective function
		for (int col=0; col<_numCols; col++) {
			double c = _obj.getDoubleAtIndex(col);
			modelString.append(termToString(_maximize ? -c : c,getVariableName(col)));
		}
		if (_qobj != null) {
			modelString.append(" + [");
			for (int col=0; col<_numCols; col++) {
				double c = _qobj.get(col);
				modelString.append(termToString(_maximize ? -c : c, getVariableName(col)+"^2"));
			}
			modelString.append(" ] / 2");
		}
		modelString.append("\nSubject To\n");
		//constraints
		List<StringBuilder> constraintStrings = new ArrayList<>(_numRows);
		for (int row=0; row<_numRows; row++)
			constraintStrings.add(new StringBuilder().append(getConstraintName(row)).append(":"));
		int[] starts = getStarts();
		int[] index = getIndex();
		for (int col=0; col<_numCols; col++) {
			int begin = starts[col];
			int end = starts[col+1];
			for (int j=begin; j<end; j++) {
				int row = index[j];
				double element = _elements.get(j);
//				if (element != 0)
					constraintStrings.get(row).append(termToString(element,getVariableName(col)));
			}
		}
		for (int row=0; row<_numRows; row++) {
			double lb = _rowLower.getDoubleAtIndex(row);
			double ub = _rowUpper.get(row);
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
			double lb = _colLower.get(col);
			double ub = _colUpper.get(col);
			if (lb==0 && ub<Double.MAX_VALUE) 
				modelString.append(getVariableName(col)).append(" <= ").append(ub).append("\n");
			else if (lb<=-Double.MAX_VALUE && ub>=Double.MAX_VALUE)
				modelString.append("-inf <= ").append(getVariableName(col)).append(" <= inf\n");
			else if (lb!=0 && lb>-Double.MAX_VALUE && ub>=Double.MAX_VALUE)
				modelString.append(lb).append(" <= ").append(getVariableName(col)).append(" <= inf").append("\n");
			else if (lb<=-Double.MAX_VALUE && ub<Double.MAX_VALUE)
				modelString.append("-inf <= ").append(getVariableName(col)).append(" <= ").append(ub).append("\n");
			else if (lb>-Double.MAX_VALUE && ub<Double.MAX_VALUE)
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

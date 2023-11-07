package com.quantego.clp;
import jnr.ffi.*;

// Define the interface that is a part of JNR-FFI
public interface CLPNative {
	
	String Clp_Version();
	
	int clpVersionMajor();
	
	int Clp_VersionMinor();
	
	int Clp_VersionRelease();
	
	Pointer Clp_newModel();
	
	void Clp_deleteModel(Pointer model);
	
	Pointer ClpSolve_new();
	
	void ClpSolve_delete(Pointer solve);
	
	void Clp_loadProblem(Pointer model, int numcols, int numrows, Pointer start, Pointer index, Pointer value, Pointer collb, Pointer colub, Pointer obj, Pointer rowlb, Pointer rowub);
	/**
	 * Original signature : <code>COINLINKAGE Clp_loadQuadraticObjective(Clp_Simplex*, const int, const CoinBigIndex*, const int*, const double*)</code><br>
	 * <i>native declaration : line 96</i>
	 */
	void Clp_loadQuadraticObjective(Pointer model, int numberColumns, Pointer start, Pointer column, Pointer element);
	/**
	 * Original signature : <code>int Clp_readMps(Clp_Simplex*, const char*, int, int)</code><br>
	 * <i>native declaration : line 103</i>
	 */
	int Clp_readMps(Pointer model, String filename, int keepNames, int ignoreErrors);
	/**
	 * Original signature : <code>void Clp_copyInIntegerInformation(Clp_Simplex*, const char*)</code><br>
	 * <i>native declaration : line 107</i>
	 */
	void Clp_copyInIntegerInformation(Pointer model, String information);
	/**
	 * Original signature : <code>void Clp_deleteIntegerInformation(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 109</i>
	 */
	void Clp_deleteIntegerInformation(Pointer model);
	/**
	 * Original signature : <code>void Clp_resize(Clp_Simplex*, int, int)</code><br>
	 * <i>native declaration : line 111</i>
	 */
	void Clp_resize(Pointer model, int newNumberRows, int newNumberColumns);
	/**
	 * Original signature : <code>void Clp_deleteRows(Clp_Simplex*, int, const int*)</code><br>
	 * <i>native declaration : line 113</i>
	 */
	void Clp_deleteRows(Pointer model, int number, Pointer which);
	/**
	 * Original signature : <code>void Clp_addRows(Clp_Simplex*, int, const double*, const double*, const int*, const int*, const double*)</code><br>
	 * <i>native declaration : line 115</i>
	 */
	void Clp_addRows(Pointer model, int number, Pointer rowLower, Pointer rowUpper, Pointer rowStarts, Pointer columns, Pointer elements);
	/**
	 * Original signature : <code>void Clp_deleteColumns(Clp_Simplex*, int, const int*)</code><br>
	 * <i>native declaration : line 121</i>
	 */
	void Clp_deleteColumns(Pointer model, int number, Pointer which);
	/**
	 * Original signature : <code>void Clp_addColumns(Clp_Simplex*, int, const double*, const double*, const double*, const int*, const int*, const double*)</code><br>
	 * <i>native declaration : line 123</i>
	 */
	void Clp_addColumns(Pointer model, int number, Pointer columnLower, Pointer columnUpper, Pointer objective, Pointer columnStarts, Pointer rows, Pointer elements);
	/**
	 * Original signature : <code>void Clp_chgRowLower(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 129</i>
	 */
	void Clp_chgRowLower(Pointer model, Pointer rowLower);
	/**
	 * Original signature : <code>void Clp_chgRowUpper(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 131</i>
	 */
	void Clp_chgRowUpper(Pointer model, Pointer rowUpper);
	/**
	 * Original signature : <code>void Clp_chgColumnLower(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 133</i>
	 */
	void Clp_chgColumnLower(Pointer model, Pointer columnLower);
	/**
	 * Original signature : <code>void Clp_chgColumnUpper(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 135</i>
	 */
	void Clp_chgColumnUpper(Pointer model, Pointer columnUpper);
	/**
	 * Original signature : <code>void Clp_chgObjCoefficients(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 137</i>
	 */
	void Clp_chgObjCoefficients(Pointer model, Pointer objIn);
	/**
	 * Original signature : <code>void Clp_dropNames(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 139</i>
	 */
	void Clp_dropNames(Pointer model);

	/**
	 * Original signature : <code>int Clp_numberRows(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 148</i>
	 */
	int Clp_numberRows(Pointer model);
	/**
	 * Original signature : <code>int Clp_numberColumns(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 150</i>
	 */
	int Clp_numberColumns(Pointer model);
	/**
	 * Original signature : <code>double Clp_primalTolerance(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 152</i>
	 */
	double Clp_primalTolerance(Pointer model);
	/**
	 * Original signature : <code>void Clp_setPrimalTolerance(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 153</i>
	 */
	void Clp_setPrimalTolerance(Pointer model, double value);
	/**
	 * Original signature : <code>double Clp_dualTolerance(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 155</i>
	 */
	double Clp_dualTolerance(Pointer model);
	/**
	 * Original signature : <code>void Clp_setDualTolerance(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 156</i>
	 */
	void Clp_setDualTolerance(Pointer model, double value);
	/**
	 * Original signature : <code>double Clp_dualObjectiveLimit(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 158</i>
	 */
	double Clp_dualObjectiveLimit(Pointer model);
	/**
	 * Original signature : <code>void Clp_setDualObjectiveLimit(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 159</i>
	 */
	void Clp_setDualObjectiveLimit(Pointer model, double value);
	/**
	 * Original signature : <code>double Clp_objectiveOffset(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 161</i>
	 */
	double Clp_objectiveOffset(Pointer model);
	/**
	 * Original signature : <code>void Clp_setObjectiveOffset(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 162</i>
	 */
	void Clp_setObjectiveOffset(Pointer model, double value);
	/**
	 * Original signature : <code>void Clp_problemName(Clp_Simplex*, int, char*)</code><br>
	 * <i>native declaration : line 164</i>
	 */
	void Clp_problemName(Pointer model, int maxNumberCharacters, String array);
	/**
	 * Original signature : <code>COINLINKAGE Clp_setProblemName(Clp_Simplex*, int, char*)</code><br>
	 * <i>native declaration : line 166</i>
	 */
	int Clp_setProblemName(Pointer model, int maxNumberCharacters, String array);
	/**
	 * Original signature : <code>int Clp_numberIterations(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 169</i>
	 */
	int Clp_numberIterations(Pointer model);
	/**
	 * Original signature : <code>void Clp_setNumberIterations(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 170</i>
	 */
	void Clp_setNumberIterations(Pointer model, int numberIterations);
	/**
	 * Original signature : <code>int maximumIterations(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 172</i>
	 */
	int maximumIterations(Pointer model);
	/**
	 * Original signature : <code>void Clp_setMaximumIterations(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 173</i>
	 */
	void Clp_setMaximumIterations(Pointer model, int value);
	/**
	 * Original signature : <code>double Clp_maximumSeconds(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 175</i>
	 */
	double Clp_maximumSeconds(Pointer model);
	/**
	 * Original signature : <code>void Clp_setMaximumSeconds(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 176</i>
	 */
	void Clp_setMaximumSeconds(Pointer model, double value);
	/**
	 * Original signature : <code>int Clp_hitMaximumIterations(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 178</i>
	 */
	int Clp_hitMaximumIterations(Pointer model);
	/**
	 * Original signature : <code>int Clp_status(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 186</i>
	 */
	int Clp_status(Pointer model);
	/**
	 * Original signature : <code>void Clp_setProblemStatus(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 188</i>
	 */
	void Clp_setProblemStatus(Pointer model, int problemStatus);
	/**
	 * Original signature : <code>int Clp_secondaryStatus(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 196</i>
	 */
	int Clp_secondaryStatus(Pointer model);
	/**
	 * Original signature : <code>void Clp_setSecondaryStatus(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 197</i>
	 */
	void Clp_setSecondaryStatus(Pointer model, int status);
	/**
	 * Original signature : <code>double Clp_optimizationDirection(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 199</i>
	 */
	double Clp_optimizationDirection(Pointer model);
	/**
	 * Original signature : <code>void Clp_setOptimizationDirection(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 200</i>
	 */
	void Clp_setOptimizationDirection(Pointer model, double value);
	/**
	 * Original signature : <code>double* Clp_primalRowSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 202</i>
	 */
	Pointer Clp_primalRowSolution(Pointer model);
	/**
	 * Original signature : <code>double* Clp_primalColumnSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 204</i>
	 */
	Pointer Clp_primalColumnSolution(Pointer model);
	/**
	 * Original signature : <code>double* Clp_dualRowSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 206</i>
	 */
	Pointer Clp_dualRowSolution(Pointer model);
	/**
	 * Original signature : <code>double* Clp_dualColumnSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 208</i>
	 */
	Pointer Clp_dualColumnSolution(Pointer model);
	/**
	 * Original signature : <code>double* Clp_rowLower(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 210</i>
	 */
	Pointer Clp_rowLower(Pointer model);
	/**
	 * Original signature : <code>double* Clp_rowUpper(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 212</i>
	 */
	Pointer Clp_rowUpper(Pointer model);
	/**
	 * Original signature : <code>double* Clp_objective(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 214</i>
	 */
	Pointer Clp_objective(Pointer model);
	/**
	 * Original signature : <code>double* Clp_columnLower(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 216</i>
	 */
	Pointer Clp_columnLower(Pointer model);
	/**
	 * Original signature : <code>double* Clp_columnUpper(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 218</i>
	 */
	Pointer Clp_columnUpper(Pointer model);
	/**
	 * Original signature : <code>int Clp_getNumElements(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 220</i>
	 */
	int Clp_getNumElements(Pointer model);
	/**
	 * Original signature : <code>CoinBigIndex* Clp_getVectorStarts(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 222</i>
	 */
	Pointer Clp_getVectorStarts(Pointer model);
	/**
	 * Original signature : <code>int* Clp_getIndices(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 224</i>
	 */
	Pointer Clp_getIndices(Pointer model);
	/**
	 * Original signature : <code>int* Clp_getVectorLengths(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 226</i>
	 */
	Pointer Clp_getVectorLengths(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getElements(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 228</i>
	 */
	Pointer Clp_getElements(Pointer model);
	/**
	 * Original signature : <code>double Clp_objectiveValue(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 230</i>
	 */
	double Clp_objectiveValue(Pointer model);
	/**
	 * Original signature : <code>char* Clp_integerInformation(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 232</i>
	 */
	String Clp_integerInformation(Pointer model);
	/**
	 * Original signature : <code>double* Clp_infeasibilityRay(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 239</i>
	 */
	Pointer Clp_infeasibilityRay(Pointer model);
	/**
	 * Original signature : <code>double* Clp_unboundedRay(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 246</i>
	 */
	Pointer Clp_unboundedRay(Pointer model);
	/**
	 * Original signature : <code>void Clp_freeRay(Clp_Simplex*, double*)</code><br>
	 * <i>native declaration : line 248</i>
	 */
	void Clp_freeRay(Pointer model, Pointer ray);
	/**
	 * Original signature : <code>int Clp_statusExists(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 250</i>
	 */
	int Clp_statusExists(Pointer model);
	/**
	 * Original signature : <code>char* Clp_statusArray(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 252</i>
	 */
	String Clp_statusArray(Pointer model);
	/**
	 * Original signature : <code>void Clp_copyinStatus(Clp_Simplex*, const unsigned char*)</code><br>
	 * <i>native declaration : line 254</i>
	 */
	void Clp_copyinStatus(Pointer model, String statusArray);
	/**
	 * Original signature : <code>int Clp_getColumnStatus(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 258</i>
	 */
	int Clp_getColumnStatus(Pointer model, int sequence);
	/**
	 * Original signature : <code>int Clp_getRowStatus(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 260</i>
	 */
	int Clp_getRowStatus(Pointer model, int sequence);
	/**
	 * Original signature : <code>void Clp_setColumnStatus(Clp_Simplex*, int, int)</code><br>
	 * <i>native declaration : line 262</i>
	 */
	void Clp_setColumnStatus(Pointer model, int sequence, int value);
	/**
	 * Original signature : <code>void Clp_setRowStatus(Clp_Simplex*, int, int)</code><br>
	 * <i>native declaration : line 265</i>
	 */
	void Clp_setRowStatus(Pointer model, int sequence, int value);
	/**
	 * Original signature : <code>void Clp_setUserPointer(Clp_Simplex*, void*)</code><br>
	 * <i>native declaration : line 269</i>
	 */
	void Clp_setUserPointer(Pointer model, Pointer pointer);
	/**
	 * Original signature : <code>void* Clp_getUserPointer(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 270</i>
	 */
	Pointer Clp_getUserPointer(Pointer model);
	/**
	 * Original signature : <code>void Clp_setLogLevel(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 288</i>
	 */
	void Clp_setLogLevel(Pointer model, int value);
	/**
	 * Original signature : <code>int Clp_logLevel(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 289</i>
	 */
	int Clp_logLevel(Pointer model);
	/**
	 * Original signature : <code>int Clp_lengthNames(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 291</i>
	 */
	int Clp_lengthNames(Pointer model);
	/**
	 * Original signature : <code>void Clp_rowName(Clp_Simplex*, int, char*)</code><br>
	 * <i>native declaration : line 293</i>
	 */
	void Clp_rowName(Pointer model, int iRow, String name);
	/**
	 * Original signature : <code>void Clp_columnName(Clp_Simplex*, int, char*)</code><br>
	 * <i>native declaration : line 295</i>
	 */
	void Clp_columnName(Pointer model, int iColumn, String name);
	/**
	 * Original signature : <code>int Clp_initialSolve(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 305</i>
	 */
	int Clp_initialSolve(Pointer model);
	/**
	 * Original signature : <code>int Clp_initialSolveWithOptions(Clp_Simplex*, Clp_Solve*)</code><br>
	 * <i>native declaration : line 307</i>
	 */
	int Clp_initialSolveWithOptions(Pointer model, Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>int Clp_initialDualSolve(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 309</i>
	 */
	int Clp_initialDualSolve(Pointer model);
	/**
	 * Original signature : <code>int Clp_initialPrimalSolve(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 311</i>
	 */
	int Clp_initialPrimalSolve(Pointer model);
	/**
	 * Original signature : <code>int Clp_initialBarrierSolve(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 313</i>
	 */
	int Clp_initialBarrierSolve(Pointer model);
	/**
	 * Original signature : <code>int Clp_initialBarrierNoCrossSolve(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 315</i>
	 */
	int Clp_initialBarrierNoCrossSolve(Pointer model);
	/**
	 * Original signature : <code>int Clp_dual(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 317</i>
	 */
	int Clp_dual(Pointer model, int ifValuesPass);
	/**
	 * Original signature : <code>int Clp_primal(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 319</i>
	 */
	int Clp_primal(Pointer model, int ifValuesPass);
	/**
	 * Original signature : <code>void Clp_idiot(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 322</i>
	 */
	void Clp_idiot(Pointer model, int tryhard);
	/**
	 * Original signature : <code>void Clp_scaling(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 325</i>
	 */
	void Clp_scaling(Pointer model, int mode);
	/**
	 * Original signature : <code>int Clp_scalingFlag(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 327</i>
	 */
	int Clp_scalingFlag(Pointer model);
	/**
	 * Original signature : <code>int Clp_crash(Clp_Simplex*, double, int)</code><br>
	 * <i>native declaration : line 342</i>
	 */
	int Clp_crash(Pointer model, double gap, int pivot);
	/**
	 * Original signature : <code>int Clp_primalFeasible(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 349</i>
	 */
	int Clp_primalFeasible(Pointer model);
	/**
	 * Original signature : <code>int Clp_dualFeasible(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 351</i>
	 */
	int Clp_dualFeasible(Pointer model);
	/**
	 * Original signature : <code>double Clp_dualBound(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 353</i>
	 */
	double Clp_dualBound(Pointer model);
	/**
	 * Original signature : <code>void Clp_setDualBound(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 354</i>
	 */
	void Clp_setDualBound(Pointer model, double value);
	/**
	 * Original signature : <code>double Clp_infeasibilityCost(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 356</i>
	 */
	double Clp_infeasibilityCost(Pointer model);
	/**
	 * Original signature : <code>void Clp_setInfeasibilityCost(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 357</i>
	 */
	void Clp_setInfeasibilityCost(Pointer model, double value);
	/**
	 * Original signature : <code>int Clp_perturbation(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 366</i>
	 */
	int Clp_perturbation(Pointer model);
	/**
	 * Original signature : <code>void Clp_setPerturbation(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 367</i>
	 */
	void Clp_setPerturbation(Pointer model, int value);
	/**
	 * Original signature : <code>int Clp_algorithm(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 369</i>
	 */
	int Clp_algorithm(Pointer model);
	/**
	 * Original signature : <code>void Clp_setAlgorithm(Clp_Simplex*, int)</code><br>
	 * <i>native declaration : line 371</i>
	 */
	void Clp_setAlgorithm(Pointer model, int value);
	/**
	 * Original signature : <code>double Clp_sumDualInfeasibilities(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 373</i>
	 */
	double Clp_sumDualInfeasibilities(Pointer model);
	/**
	 * Original signature : <code>int Clp_numberDualInfeasibilities(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 375</i>
	 */
	int Clp_numberDualInfeasibilities(Pointer model);
	/**
	 * Original signature : <code>double Clp_sumPrimalInfeasibilities(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 377</i>
	 */
	double Clp_sumPrimalInfeasibilities(Pointer model);
	/**
	 * Original signature : <code>int Clp_numberPrimalInfeasibilities(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 379</i>
	 */
	int Clp_numberPrimalInfeasibilities(Pointer model);
	/**
	 * Original signature : <code>int Clp_saveModel(Clp_Simplex*, const char*)</code><br>
	 * <i>native declaration : line 386</i>
	 */
	int Clp_saveModel(Pointer model, String fileName);
	/**
	 * Original signature : <code>int Clp_restoreModel(Clp_Simplex*, const char*)</code><br>
	 * <i>native declaration : line 389</i>
	 */
	int Clp_restoreModel(Pointer model, String fileName);
	/**
	 * Original signature : <code>void Clp_checkSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 393</i>
	 */
	void Clp_checkSolution(Pointer model);
	/**
	 * Original signature : <code>int Clp_getNumRows(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 400</i>
	 */
	int Clp_getNumRows(Pointer model);
	/**
	 * Original signature : <code>int Clp_getNumCols(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 402</i>
	 */
	int Clp_getNumCols(Pointer model);
	/**
	 * Original signature : <code>int Clp_getIterationCount(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 404</i>
	 */
	int Clp_getIterationCount(Pointer model);
	/**
	 * Original signature : <code>int Clp_isAbandoned(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 406</i>
	 */
	int Clp_isAbandoned(Pointer model);
	/**
	 * Original signature : <code>int Clp_isProvenOptimal(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 408</i>
	 */
	int Clp_isProvenOptimal(Pointer model);
	/**
	 * Original signature : <code>int Clp_isProvenPrimalInfeasible(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 410</i>
	 */
	int Clp_isProvenPrimalInfeasible(Pointer model);
	/**
	 * Original signature : <code>int Clp_isProvenDualInfeasible(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 412</i>
	 */
	int Clp_isProvenDualInfeasible(Pointer model);
	/**
	 * Original signature : <code>int Clp_isPrimalObjectiveLimitReached(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 414</i>
	 */
	int Clp_isPrimalObjectiveLimitReached(Pointer model);
	/**
	 * Original signature : <code>int Clp_isDualObjectiveLimitReached(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 416</i>
	 */
	int Clp_isDualObjectiveLimitReached(Pointer model);
	/**
	 * Original signature : <code>int Clp_isIterationLimitReached(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 418</i>
	 */
	int Clp_isIterationLimitReached(Pointer model);
	/**
	 * Original signature : <code>double Clp_getObjSense(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 420</i>
	 */
	double Clp_getObjSense(Pointer model);
	/**
	 * Original signature : <code>void Clp_setObjSense(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 422</i>
	 */
	void Clp_setObjSense(Pointer model, double objsen);
	/**
	 * Original signature : <code>double* Clp_getRowActivity(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 424</i>
	 */
	Pointer Clp_getRowActivity(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getColSolution(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 426</i>
	 */
	Pointer Clp_getColSolution(Pointer model);
	/**
	 * Original signature : <code>void Clp_setColSolution(Clp_Simplex*, const double*)</code><br>
	 * <i>native declaration : line 427</i>
	 */
	void Clp_setColSolution(Pointer model, Pointer input);
	/**
	 * Original signature : <code>double* Clp_getRowPrice(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 429</i>
	 */
	Pointer Clp_getRowPrice(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getReducedCost(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 431</i>
	 */
	Pointer Clp_getReducedCost(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getRowLower(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 433</i>
	 */
	Pointer Clp_getRowLower(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getRowUpper(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 435</i>
	 */
	Pointer Clp_getRowUpper(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getObjCoefficients(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 437</i>
	 */
	Pointer Clp_getObjCoefficients(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getColLower(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 439</i>
	 */
	Pointer Clp_getColLower(Pointer model);
	/**
	 * Original signature : <code>double* Clp_getColUpper(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 441</i>
	 */
	Pointer Clp_getColUpper(Pointer model);
	/**
	 * Original signature : <code>double Clp_getObjValue(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 443</i>
	 */
	double Clp_getObjValue(Pointer model);
	/**
	 * Original signature : <code>void Clp_printModel(Clp_Simplex*, const char*)</code><br>
	 * <i>native declaration : line 445</i>
	 */
	void Clp_printModel(Pointer model, String prefix);
	/**
	 * Original signature : <code>double Clp_getSmallElementValue(Clp_Simplex*)</code><br>
	 * <i>native declaration : line 448</i>
	 */
	double Clp_getSmallElementValue(Pointer model);
	/**
	 * Original signature : <code>void Clp_setSmallElementValue(Clp_Simplex*, double)</code><br>
	 * <i>native declaration : line 449</i>
	 */
	void Clp_setSmallElementValue(Pointer model, double value);
	/**
	 * Original signature : <code>void ClpSolve_setSpecialOption(Clp_Solve*, int, int, int)</code><br>
	 * <i>native declaration : line 456</i>
	 */
	void ClpSolve_setSpecialOption(Pointer Clp_SolvePtr1, int which, int value, int extraInfo);
	/**
	 * Original signature : <code>int ClpSolve_getSpecialOption(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 457</i>
	 */
	int ClpSolve_getSpecialOption(Pointer Clp_SolvePtr1, int which);
	/**
	 * Original signature : <code>void ClpSolve_setSolveType(Clp_Solve*, int, int)</code><br>
	 * <i>native declaration : line 468</i>
	 */
	void ClpSolve_setSolveType(Pointer Clp_SolvePtr1, int method, int extraInfo);
	/**
	 * Original signature : <code>int ClpSolve_getSolveType(Clp_Solve*)</code><br>
	 * <i>native declaration : line 469</i>
	 */
	int ClpSolve_getSolveType(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setPresolveType(Clp_Solve*, int, int)</code><br>
	 * <i>native declaration : line 477</i>
	 */
	void ClpSolve_setPresolveType(Pointer Clp_SolvePtr1, int amount, int extraInfo);
	/**
	 * Original signature : <code>int ClpSolve_getPresolveType(Clp_Solve*)</code><br>
	 * <i>native declaration : line 478</i>
	 */
	int ClpSolve_getPresolveType(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>int ClpSolve_getPresolvePasses(Clp_Solve*)</code><br>
	 * <i>native declaration : line 480</i>
	 */
	int ClpSolve_getPresolvePasses(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>int ClpSolve_getExtraInfo(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 481</i>
	 */
	int ClpSolve_getExtraInfo(Pointer Clp_SolvePtr1, int which);
	/**
	 * Original signature : <code>void ClpSolve_setInfeasibleReturn(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 482</i>
	 */
	void ClpSolve_setInfeasibleReturn(Pointer Clp_SolvePtr1, int trueFalse);
	/**
	 * Original signature : <code>int ClpSolve_infeasibleReturn(Clp_Solve*)</code><br>
	 * <i>native declaration : line 483</i>
	 */
	int ClpSolve_infeasibleReturn(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>int ClpSolve_doDual(Clp_Solve*)</code><br>
	 * <i>native declaration : line 485</i>
	 */
	int ClpSolve_doDual(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoDual(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 486</i>
	 */
	void ClpSolve_setDoDual(Pointer Clp_SolvePtr1, int doDual);
	/**
	 * Original signature : <code>int ClpSolve_doSingleton(Clp_Solve*)</code><br>
	 * <i>native declaration : line 488</i>
	 */
	int ClpSolve_doSingleton(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoSingleton(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 489</i>
	 */
	void ClpSolve_setDoSingleton(Pointer Clp_SolvePtr1, int doSingleton);
	/**
	 * Original signature : <code>int ClpSolve_doDoubleton(Clp_Solve*)</code><br>
	 * <i>native declaration : line 491</i>
	 */
	int ClpSolve_doDoubleton(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoDoubleton(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 492</i>
	 */
	void ClpSolve_setDoDoubleton(Pointer Clp_SolvePtr1, int doDoubleton);
	/**
	 * Original signature : <code>int ClpSolve_doTripleton(Clp_Solve*)</code><br>
	 * <i>native declaration : line 494</i>
	 */
	int ClpSolve_doTripleton(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoTripleton(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 495</i>
	 */
	void ClpSolve_setDoTripleton(Pointer Clp_SolvePtr1, int doTripleton);
	/**
	 * Original signature : <code>int ClpSolve_doTighten(Clp_Solve*)</code><br>
	 * <i>native declaration : line 497</i>
	 */
	int ClpSolve_doTighten(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoTighten(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 498</i>
	 */
	void ClpSolve_setDoTighten(Pointer Clp_SolvePtr1, int doTighten);
	/**
	 * Original signature : <code>int ClpSolve_doForcing(Clp_Solve*)</code><br>
	 * <i>native declaration : line 500</i>
	 */
	int ClpSolve_doForcing(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoForcing(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 501</i>
	 */
	void ClpSolve_setDoForcing(Pointer Clp_SolvePtr1, int doForcing);
	/**
	 * Original signature : <code>int ClpSolve_doImpliedFree(Clp_Solve*)</code><br>
	 * <i>native declaration : line 503</i>
	 */
	int ClpSolve_doImpliedFree(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoImpliedFree(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 504</i>
	 */
	void ClpSolve_setDoImpliedFree(Pointer Clp_SolvePtr1, int doImpliedFree);
	/**
	 * Original signature : <code>int ClpSolve_doDupcol(Clp_Solve*)</code><br>
	 * <i>native declaration : line 506</i>
	 */
	int ClpSolve_doDupcol(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoDupcol(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 507</i>
	 */
	void ClpSolve_setDoDupcol(Pointer Clp_SolvePtr1, int doDupcol);
	/**
	 * Original signature : <code>int ClpSolve_doDuprow(Clp_Solve*)</code><br>
	 * <i>native declaration : line 509</i>
	 */
	int ClpSolve_doDuprow(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoDuprow(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 510</i>
	 */
	void ClpSolve_setDoDuprow(Pointer Clp_SolvePtr1, int doDuprow);
	/**
	 * Original signature : <code>int ClpSolve_doSingletonColumn(Clp_Solve*)</code><br>
	 * <i>native declaration : line 512</i>
	 */
	int ClpSolve_doSingletonColumn(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setDoSingletonColumn(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 513</i>
	 */
	void ClpSolve_setDoSingletonColumn(Pointer Clp_SolvePtr1, int doSingleton);
	/**
	 * Original signature : <code>int ClpSolve_presolveActions(Clp_Solve*)</code><br>
	 * <i>native declaration : line 515</i>
	 */
	int ClpSolve_presolveActions(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setPresolveActions(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 516</i>
	 */
	void ClpSolve_setPresolveActions(Pointer Clp_SolvePtr1, int action);
	/**
	 * Original signature : <code>int ClpSolve_substitution(Clp_Solve*)</code><br>
	 * <i>native declaration : line 518</i>
	 */
	int ClpSolve_substitution(Pointer Clp_SolvePtr1);
	/**
	 * Original signature : <code>void ClpSolve_setSubstitution(Clp_Solve*, int)</code><br>
	 * <i>native declaration : line 519</i>
	 */
	void ClpSolve_setSubstitution(Pointer Clp_SolvePtr1, int value);


}
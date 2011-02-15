//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford)
//	
//------------------------------------------------------------------------------
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

package explicit;

import java.util.*;
import java.util.Map.Entry;

/**
 * Interface for classes that provide (read-only) access to an explicit-state MDP.
 */
public interface MDP extends Model
{
	/**
	 * Get the number of transitions from choice i of state s.
	 */
	public double getNumTransitions(int s, int i);

	/**
	 * Get an iterator over the transitions from choice i of state s.
	 */
	public Iterator<Entry<Integer,Double>> getTransitionsIterator(int s, int i);

	/**
	 * Get the transition reward (if any) for choice i of state s.
	 */
	public double getTransitionReward(int s, int i);

	/**
	 * Perform a single step of precomputation algorithm Prob0, i.e., for states i in {@code subset},
	 * set bit i of {@code result} iff, for all/some choices,
	 * there is a transition to a state in {@code u}.
	 * Quantification over choices is determined by {@code forall}.
	 * @param subset Only compute for these states
	 * @param u Set of states {@code u}
	 * @param forall For-all or there-exists (true=for-all, false=there-exists)
	 * @param result Store results here
	 */
	public void prob0step(BitSet subset, BitSet u, boolean forall, BitSet result);
	
	/**
	 * Perform a single step of precomputation algorithm Prob1, i.e., for states i in {@code subset},
	 * set bit i of {@code result} iff, for all/some choices,
	 * there is a transition to a state in {@code v} and all transitions go to states in {@code u}.
	 * Quantification over choices is determined by {@code forall}.
	 * @param subset Only compute for these states
	 * @param u Set of states {@code u}
	 * @param v Set of states {@code v}
	 * @param forall For-all or there-exists (true=for-all, false=there-exists)
	 * @param result Store results here
	 */
	public void prob1step(BitSet subset, BitSet u, BitSet v, boolean forall, BitSet result);
	
	/**
	 * Do a matrix-vector multiplication followed by min/max, i.e. one step of value iteration,
	 * i.e. for all s: result[s] = min/max_k { sum_j P_k(s,j)*vect[j] }
	 * @param vect Vector to multiply by
	 * @param min Min or max for (true=min, false=max)
	 * @param result Vector to store result in
	 * @param subset Only do multiplication for these rows (ignored if null)
	 * @param complement If true, {@code subset} is taken to be its complement (ignored if {@code subset} is null)
	 */
	public void mvMultMinMax(double vect[], boolean min, double result[], BitSet subset, boolean complement);

	/**
	 * Do a single row of matrix-vector multiplication followed by min/max,
	 * i.e. return min/max_k { sum_j P_k(s,j)*vect[j] }
	 * @param s Row index
	 * @param vect Vector to multiply by
	 * @param min Min or max for (true=min, false=max)
	 */
	public double mvMultMinMaxSingle(int s, double vect[], boolean min);

	/**
	 * Determine which choices result in min/max after a single row of matrix-vector multiplication.
	 * @param s Row index
	 * @param vect Vector to multiply by
	 * @param min Min or max (true=min, false=max)
	 * @param val Min or max value to match
	 */
	public List<Integer> mvMultMinMaxSingleChoices(int s, double vect[], boolean min, double val);

	/**
	 * Do a single row of matrix-vector multiplication for a specific choice.
	 * @param s State (row) index
	 * @param k Choice index
	 * @param vect Vector to multiply by
	 */
	public double mvMultSingle(int s, int k, double vect[]);
	
	/**
	 * Do a Gauss-Seidel-style matrix-vector multiplication followed by min/max.
	 * i.e. for all s: vect[s] = min/max_k { (sum_{j!=s} P_k(s,j)*vect[j]) / P_k(s,s) }
	 * and store new values directly in {@code vect} as computed.
	 * The maximum (absolute/relative) difference between old/new
	 * elements of {@code vect} is also returned.
	 * @param vect Vector to multiply by (and store the result in)
	 * @param min Min or max for (true=min, false=max)
	 * @param subset Only do multiplication for these rows (ignored if null)
	 * @param complement If true, {@code subset} is taken to be its complement (ignored if {@code subset} is null)
	 * @param absolute If true, compute absolute, rather than relative, difference
	 * @return The maximum difference between old/new elements of {@code vect}
	 */
	public double mvMultGSMinMax(double vect[], boolean min, BitSet subset, boolean complement, boolean absolute);

	/**
	 * Do a single row of Jacobi-style matrix-vector multiplication followed by min/max.
	 * i.e. return min/max_k { (sum_{j!=s} P_k(s,j)*vect[j]) / P_k(s,s) }
	 * @param s Row index
	 * @param vect Vector to multiply by
	 * @param min Min or max for (true=min, false=max)
	 */
	public double mvMultJacMinMaxSingle(int s, double vect[], boolean min);

	/**
	 * Do a single row of Jacobi-style matrix-vector multiplication for a specific choice.
	 * i.e. return min/max_k { (sum_{j!=s} P_k(s,j)*vect[j]) / P_k(s,s) }
	 * @param s Row index
	 * @param k Choice index
	 * @param vect Vector to multiply by
	 */
	public double mvMultJacSingle(int s, int k, double vect[]);

	/**
	 * Do a matrix-vector multiplication and sum of action reward followed by min/max, i.e. one step of value iteration.
	 * @param vect Vector to multiply by
	 * @param min Min or max for (true=min, false=max)
	 * @param result Vector to store result in
	 * @param subset Only do multiplication for these rows (ignored if null)
	 * @param complement If true, {@code subset} is taken to be its complement (ignored if {@code subset} is null)
	 */
	public void mvMultRewMinMax(double vect[], boolean min, double result[], BitSet subset, boolean complement);

	/**
	 * Do a single row of matrix-vector multiplication and sum of action reward followed by min/max.
	 * @param s Row index
	 * @param vect Vector to multiply by
	 * @param min Min or max for (true=min, false=max)
	 */
	public double mvMultRewMinMaxSingle(int s, double vect[], boolean min);

	/**
	 * Determine which choices result in min/max after a single row of matrix-vector multiplication and sum of action reward.
	 * @param s Row index
	 * @param vect Vector to multiply by
	 * @param min Min or max (true=min, false=max)
	 * @param val Min or max value to match
	 */
	public List<Integer> mvMultRewMinMaxSingleChoices(int s, double vect[], boolean min, double val);
}

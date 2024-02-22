//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <d.a.parker@cs.bham.ac.uk> (University of Birmingham/Oxford)
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

package parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import explicit.EquilibriumResult;
import parser.EvaluateContext;
import parser.visitor.ASTVisitor;
import parser.visitor.DeepCopy;
import prism.PrismException;
import prism.PrismLangException;

/**
 * Class to represent ATL &lt;&lt;.&gt;&gt; and [[.]] operators, i.e. quantification over strategies
 * ("there exists a strategy" or "for all strategies").
 */
public class ExpressionStrategy extends Expression
{
	/** "There exists a strategy" (true) or "for all strategies" (false) */
	protected boolean thereExists = false;
	
	/** Coalition info (for game models) */
	/** Actually a non-empty list of coalitions; contains a single empty one by default */
	/** So, to avoid confusion, there are only set, not add, methods for coalitions */
	protected List<Coalition> coalitions = Collections.singletonList(new Coalition());
	
	/** Child expression(s) */
	protected ArrayList<Expression> operands = new ArrayList<Expression>();
	
	/** Is there just a single operand (P/R operator)? If not, the operand list will be parenthesised. **/
	protected boolean singleOperand = false;

	// Options

	/** Optional specification of options, appended in {...} */
	protected ArrayList<Expression> optionsSpec = null;

	/** Equilibrium type */
	protected EquilibriumType equilibriumType;

	public enum EquilibriumType { NASH, CORRELATED };

	/** Equilibrium optimality criterion */
	protected EquilibriumCriterion equilibriumCriterion;

	public enum EquilibriumCriterion { SOCIAL, FAIR };

	// Constructors

	public ExpressionStrategy()
	{
	}

	public ExpressionStrategy(boolean thereExists)
	{
		this.thereExists = thereExists;
	}

	public ExpressionStrategy(boolean thereExists, Expression expression)
	{
		this.thereExists = thereExists;
		operands.add(expression);
		singleOperand = true;
	}

	// Set methods

	public void setThereExists(boolean thereExists)
	{
		this.thereExists = thereExists;
	}
	
	/**
	 * Set the (single) coalition, specified as a player list
	 */
	public void setCoalition(List<String> players)
	{
		this.coalitions.clear();
		this.coalitions.add(new Coalition(players));
	}
	
	/**
	 * Set the (single) coalition, specified as a player list
	 */
	public void setCoalition(Coalition coalition)
	{
		this.coalitions.clear();
		this.coalitions.add(new Coalition(coalition));
	}
	
	/**
	 * Set the list of coalitions, specified as lists of player names/indices
	 */
	public void setCoalitionsFromPlayerLists(List<List<String>> coalitions)
	{
		this.coalitions = new ArrayList<>();
		for (List<String> players : coalitions) {
			this.coalitions.add(new Coalition(players));
		}
	}

	/**
	 * Set the list of coalitions, copied from an existing list of Coalition objects 
	 */
	public void setCoalitions(List<Coalition> coalitions)
	{
		this.coalitions = new ArrayList<>();
		for (Coalition coalition : coalitions) {
			this.coalitions.add(new Coalition(coalition));
		}
	}

	public void setSingleOperand(Expression expression)
	{
		operands.clear();
		operands.add(expression);
		singleOperand = true;
	}

	public void addOperand(Expression e)
	{
		operands.add(e);
	}

	public void setOperand(int i, Expression e)
	{
		operands.set(i, e);
	}

	/**
	 * Process options (specified in {...}) as a list of expressions.
	 */
	public void processOptions(ArrayList<Expression> optionsSpec) throws PrismLangException
	{
		// Store for toString
		this.optionsSpec = optionsSpec;
		if (optionsSpec == null) {
			return;
		}
		for (Expression option : optionsSpec) {
			if (option instanceof ExpressionIdent &&
					((ExpressionIdent) option).getName().equals("nash")) {
				equilibriumType = EquilibriumType.NASH;
			}
			else if (option instanceof ExpressionIdent &&
					((ExpressionIdent) option).getName().equals("correlated") || ((ExpressionIdent) option).getName().equals("corr")) {
				equilibriumType = EquilibriumType.CORRELATED;
			}
			else if (option instanceof ExpressionIdent &&
					((ExpressionIdent) option).getName().equals("social")) {
				equilibriumCriterion = EquilibriumCriterion.SOCIAL;
			}
			else if (option instanceof ExpressionIdent &&
					((ExpressionIdent) option).getName().equals("fair")) {
				equilibriumCriterion = EquilibriumCriterion.FAIR;
			} else {
				throw new PrismLangException("Unknown option \"" + option + "\" for " + getOperatorString() + " operator");
			}
		}
	}

	// Get methods

	public boolean isThereExists()
	{
		return thereExists;
	}

	/**
	 * Get a string ""&lt;&lt;&gt;&gt;"" or "[[]]" indicating type of quantification.
	 */
	public String getOperatorString()
	{
		return thereExists ? "<<>>" : "[[]]";
	}

	/**
	 * Get the (or the first, if there are several) coalition 
	 */
	public Coalition getCoalition()
	{
		return coalitions.get(0);
	}
	
	/**
	 * Get the number of coalitions in the list
	 * NB: <<>> means a single list of an empty coalition
	 */
	public int getNumCoalitions()
	{
		return coalitions.size();
	}
	
	/**
	 * Get the {@code i}th coalition
	 */
	public Coalition getCoalition(int i) 
	{
		return coalitions.get(i);
	}
	
	/**
	 * Get the list of all coalitions 
	 */
	public List<Coalition> getCoalitions() 
	{
		return coalitions;
	}

	/**
	 * Check if the (or the first, if there are several) coalition is "*" (all players)
	 */
	public boolean coalitionIsAllPlayers()
	{
		return coalitions.get(0).isAllPlayers();
	}
	
	/**
	 * Get the (or the first, if there are several) coalition's players
	 */
	public List<String> getCoalitionPlayers()
	{
		return coalitions.get(0).getPlayers();
	}
	
	public boolean hasSingleOperand()
	{
		return singleOperand;
	}
	
	public int getNumOperands()
	{
		return operands.size();
	}

	public Expression getOperand(int i)
	{
		return operands.get(i);
	}

	public List<Expression> getOperands()
	{
		return operands;
	}

	/**
	 * Get the equilibrium type (e.g., Nash, correlated), if specified.
	 */
	public EquilibriumType getEquilibriumType()
	{
		return equilibriumType;
	}

	/**
	 * Get the equilibrium optimality criterion (e.g., social welfare/cost, fair), if specified.
	 */
	public EquilibriumCriterion getEquilibriumCriterion()
	{
		return equilibriumCriterion;
	}

	// Methods required for Expression:

	@Override
	public boolean isConstant()
	{
		return false;
	}

	@Override
	public boolean isProposition()
	{
		return false;
	}

	@Override
	public Object evaluate(EvaluateContext ec) throws PrismLangException
	{
		throw new PrismLangException("Cannot evaluate a " + getOperatorString() + " operator without a model");
	}

	/*@Override
	public String getResultName()
	{
		return expression.getResultName();
	}*/

	@Override
	public boolean returnsSingleValue()
	{
		return false;
	}

	// Methods required for ASTElement:

	@Override
	public Object accept(ASTVisitor v) throws PrismLangException
	{
		return v.visit(this);
	}

	@Override
	public ExpressionStrategy deepCopy(DeepCopy copier) throws PrismLangException
	{
		copier.copyAll(operands);
		copier.copyAll(optionsSpec);

		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ExpressionStrategy clone()
	{
		ExpressionStrategy clone = (ExpressionStrategy) super.clone();

		clone.setCoalitions(coalitions); // NB: setCoalitions copies anyway
		clone.operands  = (ArrayList<Expression>) operands.clone();
		clone.optionsSpec = (optionsSpec == null) ? null : (ArrayList<Expression>) optionsSpec.clone();
		clone.equilibriumType = equilibriumType;
		clone.equilibriumCriterion = equilibriumCriterion;

		return clone;
	}

	// Standard methods

	@Override
	public String toString()
	{
		String s = "";
		s += (thereExists ? "<<" : "[[");
		s += coalitions.stream().map(Coalition::toString).collect(Collectors.joining(":"));
		s += (thereExists ? ">>" : "]]");
		if (optionsSpec != null) {
			s += "{" + optionsSpec.stream().map(Expression::toString).collect(Collectors.joining(",")) + "}";
		}
		if (singleOperand) {
			s += operands.get(0);
		} else {
			s += "(";
			boolean first = true;
			for (Expression operand : operands) {
				if (!first)
					s += ", ";
				else
					first = false;
				s = s + operand;
			}
			s += ")";
		}
		return s;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coalitions == null) ? 0 : coalitions.hashCode());
		result = prime * result + ((operands == null) ? 0 : operands.hashCode());
		result = prime * result + (singleOperand ? 1231 : 1237);
		result = prime * result + (thereExists ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionStrategy other = (ExpressionStrategy) obj;
		if (coalitions == null) {
			if (other.coalitions != null)
				return false;
		} else if (!coalitions.equals(other.coalitions))
			return false;
		if (operands == null) {
			if (other.operands != null)
				return false;
		} else if (!operands.equals(other.operands))
			return false;
		if (singleOperand != other.singleOperand)
			return false;
		if (thereExists != other.thereExists)
			return false;
		return true;
	}
}

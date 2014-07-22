// ==============================================================================
//	
// Copyright (c) 2002-
// Authors:
// * Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford)
//	
// ------------------------------------------------------------------------------
//	
// This file is part of PRISM.
//	
// PRISM is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//	
// PRISM is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//	
// You should have received a copy of the GNU General Public License
// along with PRISM; if not, write to the Free Software Foundation,
// Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//	
// ==============================================================================

package explicit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import prism.ModelType;
import prism.PrismException;

/**
 * Simple explicit-state representation of a (turn-based) stochastic multi-player game (SMG).
 * States can be labelled arbitrarily with player 1..n, player 0 has a special
 * purpose of scheduling the moves of other players
 */
public class SMG extends STPGExplicit implements STPG
{
	// List of players which form a coalition
	protected List<Integer> coalition;

	// player-integer mapping
	protected Map<String, Integer> players;

	public SMG()
	{
		super();
		stateOwners = new ArrayList<Integer>(numStates);
	}

	public SMG(int n)
	{
		super(n);
		stateOwners = new ArrayList<Integer>(numStates);
	}

	/**
	 * Construct an SMG from an existing one and a state index permutation, i.e.
	 * in which state indexsetPlayer i becomes index permut[i].
	 */
	public SMG(SMG smg, int permut[], Map<String, Integer> players)
	{
		super(smg, permut);
		this.players = players;
		stateOwners = new ArrayList<Integer>(numStates);
		// Create blank array of correct size
		for (int i = 0; i < numStates; i++) {
			stateOwners.add(0);
		}
		// Copy permuted player info
		for (int i = 0; i < numStates; i++) {
			stateOwners.set(permut[i], smg.stateOwners.get(i));
		}
		coalition = new ArrayList<Integer>();

	}

	/**
	 * Copy constructor
	 */
	public SMG(SMG smg)
	{
		super(smg);
		this.players = new HashMap<String, Integer>(smg.players);
		stateOwners = new ArrayList<Integer>(smg.stateOwners);
		coalition = new ArrayList<Integer>(smg.coalition);
	}

	/**
	 * Returns the list of states that belong to the scheduler
	 * 
	 * @return the list of states that belong to the scheduler
	 */
	public Set<Integer> getSchedulerStates()
	{
		Set<Integer> ret = new HashSet<Integer>();
		return ret;
	}

	/**
	 * Adds one state, assigned to player 0
	 */
	@Override
	public int addState()
	{
		return addState(0);
	}

	/**
	 * Adds specified number of states all assigned to player 0
	 */
	@Override
	public void addStates(int numToAdd)
	{
		for (int i = 0; i < numToAdd; i++)
			stateOwners.add(0);
	}

	/**
	 * Adds state assigned to the specified player
	 * 
	 * @param player
	 *            state owner
	 * @return state id
	 */
	public int addState(int player)
	{
		super.addStates(1);
		stateOwners.add(player);
		return numStates - 1;
	}

	/**
	 * Adds the number of states the same as number of Integer in the list, each
	 * assigned to the corresponding player
	 * 
	 * @param players
	 *            list of players (to which corresponding state belongs)
	 */
	public void addStates(List<Integer> players)
	{
		super.addStates(players.size());
		stateOwners.addAll(players);
	}

	/**
	 * labels the given state with the given player
	 * 
	 * @param s
	 *            state
	 * @param player
	 *            player
	 */
	public void setPlayer(int s, int player)
	{
		if (s < stateOwners.size())
			stateOwners.set(s, player);
	}

	/**
	 * Sets the coalition (representing Player 1)
	 * @param coalition
	 */
	public void setCoalition(List<String> coalition) throws PrismException
	{
		this.coalition.clear();
		for (String player : coalition) {
			if (players.containsKey(player)) { // get the number of the player
				this.coalition.add(players.get(player));
			} else { // try parsing an integer
				try {
					this.coalition.add(Integer.parseInt(player));
				} catch (NumberFormatException e) {
					throw new PrismException("Player " + player + " is not present in the model");
				}
			}
		}
	}

	/**
	 * Sets the coalition (representing Player 1)
	 * 
	 * @param coalition
	 * @throws PrismException
	 */
	public void setCoalitionInts(List<Integer> coalition) throws PrismException
	{
		this.coalition = coalition;
	}

	/**
	 * Makes a half-deep (up to one reference level) copy of itself
	 */
	public SMG clone()
	{
		SMG smg = new SMG();
		smg.copyFrom(this);
		smg.actions = new ArrayList<List<Object>>(this.actions);
		smg.allowDupes = this.allowDupes;
		smg.maxNumDistrs = this.maxNumDistrs;
		smg.maxNumDistrsOk = this.maxNumDistrsOk;
		smg.numDistrs = this.numDistrs;
		smg.numTransitions = this.numTransitions;
		smg.stateOwners = new ArrayList<Integer>(this.stateOwners);
		smg.trans = new ArrayList<List<Distribution>>(this.trans);
		return smg;
	}

	// Accessors (for Model)

	@Override
	public ModelType getModelType()
	{
		return ModelType.SMG;
	}

	// Accessors (for STPG)

	/**
	 * Get the player that owns state {@code s}.
	 */
	@Override
	public int getPlayer(int s)
	{
		return coalition.contains(stateOwners.get(s)) ? 1 : 2;
	}

	public List<Integer> getCoalition()
	{
		return this.coalition;
	}

	public Map<String, Integer> getPlayerMapping()
	{
		return this.players;
	}

        public void setPlayerMapping(Map<String, Integer> pl)
	{
		this.players = pl;
	}

	// Standard methods

	@Override
	public String toString()
	{
		int i, j, n;
		Object o;
		String s = "";
		s = "[ ";
		for (i = 0; i < numStates; i++) {
			if (i > 0)
				s += ", ";
			s += i + "(P-" + stateOwners.get(i) + " " + statesList.get(i) + "): ";
			s += "[";
			n = getNumChoices(i);
			for (j = 0; j < n; j++) {
				if (j > 0)
					s += ",";
				o = getAction(i, j);
				if (o != null)
					s += o + ":";
				s += trans.get(i).get(j);
			}
			s += "]";
		}
		s += " ]\n";
		return s;
	}

	public List<Integer> getStateLabels()
	{
		return stateOwners;
	}

	public void setStateLabels(List<Integer> list)
	{
		stateOwners = list;
	}

}

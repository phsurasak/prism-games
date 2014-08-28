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
import java.util.List;
import java.util.Map;

import prism.ModelType;
import prism.PrismException;

/**
 * Simple explicit-state representation of a (turn-based) stochastic multi-player game (SMG).
 * States can be labelled arbitrarily with player 1..n, player 0 has a special
 * purpose of scheduling the moves of other players
 */
public class SMG extends STPGExplicit implements STPG
{
	// NB: We re-use the existing stateOwners list in the superclass to assign states to players

	// Mapping from player names to (integer) indices
	protected Map<String, Integer> players;

	// Optionally, a list of players which form a coalition.
	// When set, this SMG effectively reduces to 2 players,
	// i.e. an STPG in which the coalition corresponds to player 1.
	// This is why the class implements the STPG interface.
	protected List<Integer> coalition;

	// Constructors

	/**
	 * Constructor: empty SMG.
	 */
	public SMG()
	{
		super();
		players = new HashMap<String, Integer>();
		coalition = null;
	}

	/**
	 * Constructor: new SMG with fixed number of states.
	 */
	public SMG(int numStates)
	{
		super(numStates);
		players = new HashMap<String, Integer>();
		coalition = null;
	}

	/**
	 * Construct an SMG from an existing one and a state index permutation,
	 * i.e. in which state index i becomes index permut[i].
	 * Player and coalition info is also copied across.
	 */
	public SMG(SMG smg, int permut[])
	{
		super(smg, permut);
		players = new HashMap<String, Integer>(smg.players);
		coalition = smg.coalition == null ? null : new ArrayList<Integer>(smg.coalition);

	}

	/**
	 * Copy constructor
	 */
	public SMG(SMG smg)
	{
		super(smg);
		players = new HashMap<String, Integer>(smg.players);
		coalition = smg.coalition == null ? null : new ArrayList<Integer>(smg.coalition);
	}

	// Mutators

	/**
	 * Add a new (player 0) state and return its index.
	 */
	@Override
	public int addState()
	{
		return addState(0);
	}

	/**
	 * Add multiple new (player 0) states.
	 */
	@Override
	public void addStates(int numToAdd)
	{
		for (int i = 0; i < numToAdd; i++)
			stateOwners.add(0);
	}

	/**
	 * Set the mapping from player names to (integer) indices.
	 */
	public void setPlayerMapping(Map<String, Integer> pl)
	{
		this.players = pl;
	}

	/**
	 * Set a coalition of players for this SMG
	 * (which effectively makes it an STPG with player 1 representing the coalition).
	 * Pass null to remove any coalition info from this SMG.
	 * @param playerNames List of names of players making up the coalition 
	 */
	public void setCoalition(List<String> playerNames) throws PrismException
	{
		if (playerNames == null) {
			coalition = null;
			return;
		}
			
		coalition = new ArrayList<Integer>();
		for (String playerName : playerNames) {
			// Look up index of each player and add to coalition
			if (players.containsKey(playerName)) {
				coalition.add(players.get(playerName));
			}
			// Failing that, try parsing it as an integer
			else {
				try {
					coalition.add(Integer.parseInt(playerName));
				} catch (NumberFormatException e) {
					throw new PrismException("Player " + playerName + " is not present in the model");
				}
			}
		}
	}

	/**
	 * Set a coalition of players for this SMG
	 * (which effectively makes it an STPG with player 1 representing the coalition).
	 * The list if indices is copied, not stored directly.
	 * Pass null to remove any coalition info from this SMG.
	 * @param coalition List of indices of players making up the coalition 
	 */
	public void setCoalitionInts(List<Integer> coalition) throws PrismException
	{
		if (coalition == null) {
			this.coalition = null;
		} else {
			this.coalition = new ArrayList<Integer>(coalition);
		}
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

	// Accessors (for STPG/SMG)

	@Override
	public int getPlayer(int s)
	{
		if (coalition == null)
			return stateOwners.get(s);
		else
			return coalition.contains(stateOwners.get(s)) ? 1 : 2;
	}

	/**
	 * Get the mapping from player names to (integer) indices.
	 */
	public Map<String, Integer> getPlayerMapping()
	{
		return this.players;
	}

	/**
	 * Get the current coalition (may be null).
	 */
	public List<Integer> getCoalition()
	{
		return coalition;
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
}

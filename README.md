# PRISM-games

This is PRISM-games, an extension of the PRISM model checker
for verification and strategy synthesis for stochastic multi-player games.


## Installation

Up-to-date installation instructions can be found here:

http://www.prismmodelchecker.org/games/installation.php


## Documentation

Included in this release is a manual for the version of PRISM
on which PRISM-games is based.

Documentation specifically for PRISM-games can be found here:

  http://www.prismmodelchecker.org/games/


## Licensing

PRISM-games is distributed under the GNU General Public License (GPL), version 2.
A copy of this license can be found in the file `COPYING.txt`.
For more information, see:

  http://www.gnu.org/licenses/

PRISM-games uses the CUDD (Colorado University Decision Diagram) library of Fabio Somenzi,
which is freely available. For more information about this library, see:

  http://vlsi.colorado.edu/~fabio/CUDD/

PRISM-games also uses various other libraries (mainly to be found in the lib directory).
For details of those, and for links to source where we distribute only binaries, see:

http://www.prismmodelchecker.org/other-downloads.php


## Acknowledgements

PRISM was created and is still actively maintained by:

 * Dave Parker (University of Oxford)
 * Gethin Norman (University of Glasgow)
 * Marta Kwiatkowska (University of Oxford) 

Development of the tool is currently led from Oxford by Dave Parker.

The following have made a wide range of contributions to
PRISM covering many different aspects of the tool
(in approximately reverse chronological order):

 * Steffen Märcker (Technische Universität Dresden)
 * Joachim Klein (formerly Technische Universität Dresden)
 * Vojtech Forejt (formerly University of Oxford)

The following have worked specifically worked on PRISM-games
(in approximately reverse chronological order):

* Gabriel Santos: concurrent stochastic games and equilibria
* Clemens Wiltsche: multi-objective and compositional techniques
* Mateusz Ujma: turn-based stochastic games
* Aistis Simaitis: turn-based stochastic games

We also gratefully acknowledge contributions to the PRISM code-base from
(in approximately reverse chronological order):

 * Max Kurze: Language parser code improvements
 * Ludwig Pauly: Reward import/export
 * Alberto Puggelli: First version of interval DTMC/MDP code
 * Xueyi Zou: Partially observable Markov decision processes (POMDPs)
 * Chris Novakovic: Build infrastructure and explicit engine improvements
 * Clemens Wiltsche: Multi-objective and compositional synthesis for stochastic games
 * Ernst Moritz Hahn: Parametric model checking, fast adaptive uniformisation + various other features
 * Frits Dannenberg: Fast adaptive uniformisation
 * Hongyang Qu: Multi-objective model checking
 * Mateusz Ujma: Bug fixes and GUI improvements
 * Christian von Essen: Symbolic/explicit-state model checking
 * Vincent Nimal: Approximate (simulation-based) model checking techniques
 * Mark Kattenbelt: Wide range of enhancements/additions, especially in the GUI
 * Carlos Bederian (working with Pedro D'Argenio): LTL model checking for MDPs
 * Gethin Norman: Precomputation algorithms, abstraction
 * Alistair John Strachan: Port to 64-bit architectures
 * Alistair John Strachan, Mike Arthur and Zak Cohen: Integration of JFreeChart into PRISM
 * Charles Harley and Sebastian Vermehren: GUI enhancements
 * Rashid Mehmood: Improvements to low-level data structures and numerical solution algorithms
 * Stephen Gilmore: Support for the stochastic process algebra PEPA
 * Paolo Ballarini & Kenneth Chan: Port to Mac OS X
 * Andrew Hinton: Original versions of the GUI, Windows port and simulator
 * Joachim Meyer-Kayser: Original implementation of the "Fox-Glynn" algorithm 

For more details see:

  http://www.prismmodelchecker.org/people.php


## Contact

If you have problems or questions regarding PRISM, please use the help forum provided. See:

  http://www.prismmodelchecker.org/support.php

Other comments and feedback about any aspect of PRISM are also very welcome. Please contact:

  Dave Parker  
  (david.parker@cs.ox.ac.uk)  
  Department of Computer Science  
  University of Oxford  
  Oxford  
  OX1 3QG
  UK

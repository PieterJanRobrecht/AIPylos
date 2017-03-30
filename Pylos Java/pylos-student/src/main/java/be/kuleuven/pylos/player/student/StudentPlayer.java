package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;
/**
 * Created by Jan on 20/02/2015.
 */
public class StudentPlayer extends PylosPlayer {

	@Override
	public void doMove(PylosGameIF game, PylosBoard board) {
		// Met observer kan je de tegenstander in de gaten houden ofzo
		/* board methods
			* 	PylosLocation[] allLocations = board.getLocations();
			* 	PylosSphere[] allSpheres = board.getSpheres();
			* 	PylosSphere[] mySpheres = board.getSpheres(this);
			* 	PylosSphere myReserveSphere = board.getReserve(this); */

		/* game methods
			* game.moveSphere(myReserveSphere, allLocations[0]); */

	}

	@Override
	public void doRemove(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere); */

	}

	@Override
	public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere);
			* game.pass() */

	}
}

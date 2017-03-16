package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

test matthijs

public class StudentPlayer extends PylosPlayer {

	@Override
	public void doMove(PylosGameIF game, PylosBoard board) {
		/* board methods
			* 	PylosLocation[] allLocations = board.getLocations();
			* 	PylosSphere[] allSpheres = board.getSpheres();
			* 	PylosSphere[] mySpheres = board.getSpheres(this);
			* 	PylosSphere myReserveSphere = board.getReserve(this); */

		/* game methods
			* game.moveSphere(myReserveSphere, allLocations[0]); */
		PylosLocation[] allLocations = board.getLocations();
		PylosSphere myRePylosSphere = board.getReserve(this);
		for (PylosLocation allLocation : allLocations) {
			if (allLocation.isUsable()) {
				game.moveSphere(myRePylosSphere, allLocation);
				break;
			}
		}
	}

	@Override
	public void doRemove(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere); */
		PylosSphere[] mySpheres = board.getSpheres(this);
		for (PylosSphere mySphere : mySpheres) {
			if (mySphere.canRemove()) {
				game.removeSphere(mySphere);
				break;
			}
		}
	}

	@Override
	public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* game methods
			* game.removeSphere(mySphere);
			* game.pass() */
		game.pass();
	}
}

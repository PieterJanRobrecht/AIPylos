package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jan on 20/02/2015.
 */
public class StudentPlayer extends PylosPlayer {
	PylosLocation lastPylosLocation = null;

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
		PylosSphere sphereToRemove = lastPylosLocation.getSphere();
		game.removeSphere(sphereToRemove);
	}

	@Override
	public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* collect all removable spheres */
		ArrayList<PylosSphere> removableSpheres = new ArrayList<>();
		for (PylosSphere ps : board.getSpheres(this)) {
			if (ps.canRemove()) {
				removableSpheres.add(ps);
			}
		}
		Collections.shuffle(removableSpheres, getRandom());

		/* if remove a sphere, remove the sphere with minimum in square
		 * otherwise, pass */
		if (!removableSpheres.isEmpty()) {
			PylosSphere sphere = Collections.min(removableSpheres, new Comparator<PylosSphere>() {
				@Override
				public int compare(PylosSphere o1, PylosSphere o2) {
					return Integer.compare(o1.getLocation().getMaxInSquare(StudentPlayer.this), o2.getLocation().getMaxInSquare(StudentPlayer.this));
				}
			});
			game.removeSphere(sphere);
		} else {
			game.pass();
		}
	}
}

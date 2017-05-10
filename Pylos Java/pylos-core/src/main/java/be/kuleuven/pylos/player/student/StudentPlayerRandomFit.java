package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGameIF;
import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ine on 5/05/2015.
 */
public class StudentPlayerRandomFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
		/* add a reserve sphere to a feasible random location */
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
		/* removeSphere a random sphere */
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
		/* always pass */
        game.pass();
    }
}

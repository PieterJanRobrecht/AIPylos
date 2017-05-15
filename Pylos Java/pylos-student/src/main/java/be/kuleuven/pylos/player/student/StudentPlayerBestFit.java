package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;
import be.kuleuven.pylos.player.PylosPlayerObserver;
import be.kuleuven.pylos.player.codes.PylosPlayerBestFit;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Ine on 25/02/2015.
 */
public class StudentPlayerBestFit extends PylosPlayer {

    //	public static Random random = new Random(218);
    private PylosLocation lastPylosLocation;

    private int[] input = new int[5];
    private int[] boardOrReserve = new int[2];
    private int[] output = new int[5];
    private int[] choseSphere = new int[2];

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        getObserver().shoutBegin(board, this);

        resetInput();
        /* collect all possible locations */
        ArrayList<PylosLocation> allUsableLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (bl.isUsable()) {
                allUsableLocations.add(bl);
            }
        }

        Collections.shuffle(allUsableLocations, getRandom());

		/* get the location with maximum in square of this color */
        PylosLocation toMaxThis = getMaxInSquare(allUsableLocations, this);

		/* get the location with maximum in square of the other color */
        PylosLocation toMaxOther = getMaxInSquare(allUsableLocations, this.OTHER);

		/* decide what to do */
        PylosSphere sphere = null;
        PylosLocation toLocation = null;

        sortZ(allUsableLocations, this);
        List<PylosLocation> prunedForZ = getAboveZero(allUsableLocations);
        if (prunedForZ.size() != 0) {
            PylosLocation bl = prunedForZ.get(0);
            sphere = getMovableSphereOrReserve(bl, board);
            toLocation = bl;

            input[0] = 1;
            output[0] = 1;
        }
//        if (toLocation == null) {
        Collections.shuffle(allUsableLocations, getRandom());
        if (toMaxOther.getMaxInSquare(this.OTHER) == 2 || toMaxOther.getMaxInSquare(this.OTHER) == 3) {
            // we should sabotage this square
            if (toLocation == null) {
                sphere = getMovableSphereOrReserve(toMaxOther, board);
                toLocation = toMaxOther;
                output[1] = 1;
            }
            input[1] = 1;
        }  if (toMaxThis.getMaxInSquare(this) == 2 || toMaxThis.getMaxInSquare(this) == 3) {
            // we should create this square
            if (toLocation == null) {
                sphere = getMovableSphereOrReserve(toMaxThis, board);
                toLocation = toMaxThis;
                output[2] = 1;
            }
            input[2] = 1;
        }  {
            // try to move a used sphere
            // prefer higher locations, than max in square
            // than pick a sphere with minimum in square and which does not enable the other player to create a square
            sortZorMaxInSquare(allUsableLocations, this);
            for (int i = 0; i < allUsableLocations.size(); i++) {
                PylosLocation bl = allUsableLocations.get(i);
                PylosSphere usedSphere = getMovableSphereMinInSquare(bl, board);
                if (usedSphere != null && usedSphere.getLocation().getMaxInSquare(this.OTHER) < 3) {
                    if (toLocation == null) {
                        sphere = usedSphere;
                        toLocation = bl;
                        output[3] = 1;

                        boardOrReserve[0] = 1;
                        boardOrReserve[1] = 1;

                        choseSphere[0] = 1;
                        choseSphere[1] = 0;
                    }
                    input[3] = 1;
                    break;
                }
            }
            if (toLocation == null) {
                // we couldn't move a used sphere, add a reserve sphere
                // put it on the highest location and the max in square on the same level
                toLocation = getMaxZorMaxInSquare(allUsableLocations, this);
                sphere = board.getReserve(this);

                boardOrReserve[0] = 0;
                boardOrReserve[1] = 1;

                choseSphere[0] = 0;
                choseSphere[1] = 1;

                output[4] = 1;
            }
            input[4] = 1;
//            }
        }
//        getObserver().shoutAI(Arrays.toString(input) +";"+Arrays.toString(output)+ ";" + Arrays.toString(boardOrReserve) + ";" + Arrays.toString(choseSphere));

        getObserver().shoutEnd(board, this);
        game.moveSphere(sphere, toLocation);
        lastPylosLocation = toLocation;
    }

    private void resetInput() {
        for (int i = 0; i < input.length; i++) {
            input[i] = 0;
            output[i] = 0;
        }
        for (int i = 0; i < boardOrReserve.length; i++) {
            boardOrReserve[i] = 0;
            choseSphere[i] = 0;
        }
    }

    private List<PylosLocation> getAboveZero(ArrayList<PylosLocation> allUsableLocations) {
        List<PylosLocation> help = new ArrayList<>();
        for (PylosLocation loc : allUsableLocations) {
            if (loc.Z != 0) {
                help.add(loc);
            }
        }
        return help;
    }

    private PylosLocation getMaxInSquare(ArrayList<PylosLocation> locations, PylosPlayer player) {
        return Collections.max(locations, new Comparator<PylosLocation>() {
            @Override
            public int compare(PylosLocation o1, PylosLocation o2) {
                return Integer.compare(o1.getMaxInSquare(player), o2.getMaxInSquare(player));
            }
        });
    }

    private PylosLocation getMaxZorMaxInSquare(ArrayList<PylosLocation> locations, PylosPlayer player) {
        return Collections.max(locations, new Comparator<PylosLocation>() {
            @Override
            public int compare(PylosLocation o1, PylosLocation o2) {
                int compZ = Integer.compare(o1.Z, o2.Z);
                if (compZ != 0) return compZ;
                return Integer.compare(o1.getMaxInSquare(player), o2.getMaxInSquare(player));
            }
        });
    }

    private void sortZorMaxInSquare(ArrayList<PylosLocation> locations, PylosPlayer player) {
        Collections.sort(locations, new Comparator<PylosLocation>() {
            @Override
            public int compare(PylosLocation o1, PylosLocation o2) {
                int compZ = -Integer.compare(o1.Z, o2.Z);
                if (compZ != 0) return compZ;
                return -Integer.compare(o1.getMaxInSquare(player), o2.getMaxInSquare(player));
            }
        });
    }

    private void sortZ(ArrayList<PylosLocation> locations, PylosPlayer player) {
        Collections.sort(locations, new Comparator<PylosLocation>() {
            @Override
            public int compare(PylosLocation o1, PylosLocation o2) {
                int compZ = -Integer.compare(o1.Z, o2.Z);
                return compZ;
            }
        });
    }

    private PylosSphere getMovableSphereMinInSquare(PylosLocation toLocation, PylosBoard board) {
        ArrayList<PylosSphere> movableSpheres = new ArrayList<>();
        for (PylosSphere sphere : board.getSpheres(this)) {
            if (!sphere.isReserve() && sphere.canMoveTo(toLocation)) {
                movableSpheres.add(sphere);
            }
        }
        if (!movableSpheres.isEmpty()) {
            /* pick the one with the minimum in square */
            PylosSphere sphere = Collections.min(movableSpheres, new Comparator<PylosSphere>() {
                @Override
                public int compare(PylosSphere o1, PylosSphere o2) {
                    return Integer.compare(o1.getLocation().getMaxInSquare(StudentPlayerBestFit.this), o2.getLocation().getMaxInSquare(StudentPlayerBestFit.this));
                }
            });
            return sphere;
        } else {
            return null;
        }
    }

    private PylosSphere getMovableSphereOrReserve(PylosLocation toLocation, PylosBoard board) {
        PylosSphere usedSphere = getMovableSphereMinInSquare(toLocation, board);
        if (usedSphere != null) {
            boardOrReserve[0] = 1;
            boardOrReserve[1] = 1;

            choseSphere[0] = 1;
            choseSphere[1] = 0;
            return usedSphere;
        } else {
            boardOrReserve[0] = 0;
            boardOrReserve[1] = 1;

            choseSphere[0] = 0;
            choseSphere[1] = 1;
            return board.getReserve(this);
        }
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* removeSphere a random sphere from the square */
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
                    return Integer.compare(o1.getLocation().getMaxInSquare(StudentPlayerBestFit.this), o2.getLocation().getMaxInSquare(StudentPlayerBestFit.this));
                }
            });
            game.removeSphere(sphere);
        } else {
            game.pass();
        }
    }
}

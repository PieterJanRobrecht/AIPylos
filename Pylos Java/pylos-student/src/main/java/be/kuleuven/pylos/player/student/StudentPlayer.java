package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGameIF;
import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;
import be.kuleuven.pylos.player.PylosPlayer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.File;
import java.util.*;
import static org.encog.persist.EncogDirectoryPersistence.*;

/**
 * Created by Jan on 20/02/2015.
 */
public class StudentPlayer extends PylosPlayer {
    PylosLocation lastPylosLocation = null;


    PylosLocation[] locations;
    PylosSphere[][] spheres;

    BasicNetwork networkLocation;
    BasicNetwork networkSphere;

    public StudentPlayer(String file1, String file2) {
        loadNet(file1,file2);
    }

    private void loadNet(String file1, String file2) {
        networkLocation = (BasicNetwork) loadObject(new File(file1));
        networkSphere = (BasicNetwork) loadObject(new File(file2));
    }

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        List<double[][]> data = readBoard(game, board);
        System.out.println("Read the board: " + Arrays.toString(data.get(0)[0]));
        List<double[]> answerLocation = computerLocation(data.get(0)[0]);
        int locationIndex = getBiggest(answerLocation.get(0));
        List<double[]> answersSphere = computeSphere(data.get(1)[locationIndex]);
        System.out.println("Ready to make a decision");
        int sphereIndex = getBiggest(answersSphere.get(0));
        System.out.println("Deciding to pick location: " + locationIndex + " and sphere: " + sphereIndex);
        game.moveSphere(spheres[locationIndex][sphereIndex], locations[locationIndex]);
        lastPylosLocation = locations[locationIndex];
    }

    private List<double[]> computerLocation(double[] doubles) {
        List<double[]> answers = new ArrayList<>();

        MLData mlData = new BasicMLData(doubles);
        answers.add(networkLocation.compute(mlData).getData());
        System.out.println("The answer for the location was: " + Arrays.toString(answers.get(0)));
        return answers;
    }

    private int getBiggest(double[] doubles) {
        int index = 0;
        double largest = 0;
        for (int i = 0; i < doubles.length; i++) {
            if (doubles[i] > largest) {
                largest = doubles[i];
                index = i;
            }
        }
        return index;
    }

    private List<double[]> computeSphere(double[] data) {
        List<double[]> answers = new ArrayList<>();

        MLData mlData = new BasicMLData(data);
        answers.add(networkSphere.compute(mlData).getData());
        System.out.println("The answer for the sphere was: " + Arrays.toString(answers.get(0)));
        return answers;
    }

    private List<double[][]> readBoard(PylosGameIF game, PylosBoard board) {
        locations = new PylosLocation[5];
        spheres = new PylosSphere[5][2];

        List<double[][]> data = new ArrayList<>();
        double[][] input = new double[1][5];
        double[][] boardOrReserve = new double[5][2];

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
            getMovableSphereOrReserve(bl, board, boardOrReserve[0], spheres[0]);
            input[0][0] = 1;
            locations[0] = bl;
        }
        Collections.shuffle(allUsableLocations, getRandom());
        if (toMaxOther.getMaxInSquare(this.OTHER) == 2 || toMaxOther.getMaxInSquare(this.OTHER) == 3) {

            getMovableSphereOrReserve(toMaxOther, board, boardOrReserve[1], spheres[1]);

            input[0][1] = 1;
            locations[1] = toMaxOther;
        }
        if (toMaxThis.getMaxInSquare(this) == 2 || toMaxThis.getMaxInSquare(this) == 3) {

            getMovableSphereOrReserve(toMaxThis, board, boardOrReserve[2], spheres[2]);
            input[0][2] = 1;
            locations[2] = toMaxThis;
        }
        sortZorMaxInSquare(allUsableLocations, this);
        for (PylosLocation bl : allUsableLocations) {
            PylosSphere usedSphere = getMovableSphereMinInSquare(bl, board);
            if (usedSphere != null && usedSphere.getLocation().getMaxInSquare(this.OTHER) < 3) {
                input[0][3] = 1;
                locations[3] = bl;

                boardOrReserve[3][0] = 1;
                boardOrReserve[3][1] = 1;
                spheres[3][0] = usedSphere;
                spheres[3][1] = board.getReserve(this);
                break;
            }
        }
        input[0][4] = 1;
        locations[4] = getMaxZorMaxInSquare(allUsableLocations, this);

        getMovableSphereOrReserve(getMaxZorMaxInSquare(allUsableLocations, this), board, boardOrReserve[4], spheres[4]);

        data.add(input);
        data.add(boardOrReserve);
        return data;
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
                    return Integer.compare(o1.getLocation().getMaxInSquare(StudentPlayer.this), o2.getLocation().getMaxInSquare(StudentPlayer.this));
                }
            });
            return sphere;
        } else {
            return null;
        }
    }

    private void getMovableSphereOrReserve(PylosLocation toLocation, PylosBoard board, double[] boardOrReserve, PylosSphere[] sphere) {
        PylosSphere usedSphere = getMovableSphereMinInSquare(toLocation, board);
        if (usedSphere != null) {
            boardOrReserve[0] = 1;
            boardOrReserve[1] = 1;
            sphere[0]=usedSphere;
        } else {
            boardOrReserve[0] = 0;
            boardOrReserve[1] = 1;
        }
        sphere[1] = board.getReserve(this);
    }
}

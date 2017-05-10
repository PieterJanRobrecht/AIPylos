package be.kuleuven.pylos.player;

import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;

import java.io.*;

/**
 * Created by Jan on 5/03/2015.
 */
public interface PylosPlayerObserver {

	PylosPlayerObserver NONE = new PylosPlayerObserver() {
		@Override
		public void shout(String str) {

		}

		@Override
		public void shoutAI(String str) {

		}

		@Override
		public void shoutGood(String str) {

		}

		@Override
		public void shoutBad(String str) {

		}

		@Override
		public void checkingMoveSphere(PylosSphere pylosSphere, PylosLocation toLocation) {

		}

		@Override
		public void checkingRemoveSphere(PylosSphere pylosSphere) {

		}

		@Override
		public void checkingPass() {

		}
	};

	PylosPlayerObserver CONSOLE_PLAYER_OBSERVER = new PylosPlayerObserver() {
		FileOutputStream outputFile;
		FileOutputStream outputFileAI;
		PrintStream printOutput;
		PrintStream printOutputAI;

		{
			try {
				outputFile = new FileOutputStream("log.txt");
				printOutput = new PrintStream(outputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}


		{
			try {
				outputFileAI = new FileOutputStream("AI.txt");
				printOutputAI = new PrintStream(outputFileAI);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void shout(String str) {
			printOutput.println(str);
		}

		@Override
		public void shoutAI(String str) {
			printOutputAI.println(str);
		}

		@Override
		public void shoutGood(String str) {

		}

		@Override
		public void shoutBad(String str) {

		}

		@Override
		public void checkingMoveSphere(PylosSphere pylosSphere, PylosLocation toLocation) {

		}

		@Override
		public void checkingRemoveSphere(PylosSphere pylosSphere) {

		}

		@Override
		public void checkingPass() {

		}
	};

	void shout(String str);
	void shoutAI(String str);
	void shoutGood(String str);
	void shoutBad(String str);
	void checkingMoveSphere(PylosSphere pylosSphere, PylosLocation toLocation);
	void checkingRemoveSphere(PylosSphere pylosSphere);
	void checkingPass();

}

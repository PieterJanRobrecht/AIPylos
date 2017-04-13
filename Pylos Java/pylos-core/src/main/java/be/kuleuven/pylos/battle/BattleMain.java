package be.kuleuven.pylos.battle;

import be.kuleuven.pylos.player.codes.PylosPlayerBestFit;
import be.kuleuven.pylos.player.codes.PylosPlayerMiniMax;

/**
 * Created by Jan on 23/02/2015.
 */
public class BattleMain {

	public static void main(String[] args){
		Battle.play(new PylosPlayerBestFit(), new PylosPlayerBestFit(), 2);
	}

}

package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.HashMap;

public abstract class DiceState {

    /**
     * ダイス状態のセットを取得する
     * 状態名を String　値を Integer　のMapで取得
     * @return ダイス状態のセット
     */
    abstract HashMap<String, Integer> getStateSet ();

    /**
     * ダイス状態をセットする
     * @param gameInfo
     * @param boardSurface
     * @return ダイス状態が変化した場合trueを，変化しない場合はfalseを返す
     */
    abstract boolean setDiceState (GameInfo gameInfo, BoardSurface boardSurface);

    /**
     * 2つのダイスのステートを比べ，違いがあればtrue 全て同じであればfalseを返す
     * @param diceState1
     * @param diceState2
     * @return
     */
    public static boolean isDiceStateChange(DiceState diceState1, DiceState diceState2) {
        HashMap<String, Integer> dice1StateSet = diceState1.getStateSet();
        HashMap<String, Integer> dice2StateSet = diceState2.getStateSet();

        for (String key :
                dice1StateSet.keySet()) {
            if (dice2StateSet.containsKey(key)) {
                if (dice1StateSet.get(key) != dice2StateSet.get(key)) {
                    return true;
                }
            } else {
                // dice1とdice2のステート情報が違う場合，この分岐に入る．
                Log.warn("種類の違うダイスを比べています．");
                return false;
            }
        }
        return false;
    }
}

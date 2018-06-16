package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WerewolfDiceState extends DiceState {

    GameInfo gameInfo;
    // ----- 状態s -----
    private int day = 0;            // 日付
    private int seerCONum = 0;   // 占い師COの数
    private int mediumCO = 0;       // 霊能者COの数

    // ----- -----
    /* Q値 */
    private Map<String, Integer> qMap = new HashMap<>();
    int q[][][][];
    /* 遷移状態 */
    private List<Map<String, Integer>> route = new ArrayList<>();

    public WerewolfDiceState() {

    }

    @Override
    HashMap<String, Integer> getStateSet() {
        return null;
    }

    @Override
    boolean setDiceState(GameInfo gameInfo, BoardSurface boardSurface) {
        return false;
    }

    /**
     * Q値の初期化
     */
    private void initQ(){
        for (int i = 0; i < Utility.gameMaxDay(gameInfo.getAgentList().size()); i++) {

        }
    }
}

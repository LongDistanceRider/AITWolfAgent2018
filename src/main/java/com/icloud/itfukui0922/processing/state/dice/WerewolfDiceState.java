package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WerewolfDiceState extends DiceState {

    // ----- 状態s -----
    private int day = 0;            // 日付
    private int seerCONum = 0;   // 占い師COの数
    private int mediumCO = 0;       // 霊能者CO(boolean)
    private int discoveryWolf = 0;  // 黒判定(boolean)
    // ----- -----
    /* Q値 */
    private Map<String, Integer> qMap = new HashMap<>();
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
}

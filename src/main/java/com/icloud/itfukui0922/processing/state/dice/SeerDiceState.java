package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.dice.BoardSurface;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeerDiceState extends DiceState{

    // ----- 状態s -----
    private int day = 0;            // 日付
    private int oppositionCO = 0;   // 対抗CO(boolean)
    private int mediumCO = 0;       // 霊能者CO(boolean)
    private int discoveryWolf = 0;  // 黒判定(boolean)
    // ----- -----
    /* Q値 */
    private Map<String, Integer> qMap = new HashMap<>();
    /* 繊維状態 */
    private List<Map<String, Integer>> route = new ArrayList<>();

    public int getDay() {
        return day;
    }

    public int getOppositionCO() {
        return oppositionCO;
    }

    public int getMediumCO() {
        return mediumCO;
    }

    public int getDiscoveryWolf() {
        return discoveryWolf;
    }

    public SeerDiceState(GameInfo gameInfo, BoardSurface boardSurface) {
        day = gameInfo.getDay();
        oppositionCO =  boardSurface.comingoutRoleAgentList(Role.SEER).size() > 0 ? 1 : 0;
        mediumCO = boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0 ? 1 : 0;
        Map.Entry<Agent, Species> divIdenMap = boardSurface.getMyInformation().peekDivIdenMap();
        if (divIdenMap != null) {
            discoveryWolf = divIdenMap.getValue() == Species.WEREWOLF ? 1 : 0;
        } else {
            discoveryWolf = 0;  // 占い情報がない場合は黒出ししていないとする．
        }
    }

    @Override
    public boolean setDiceState (GameInfo gameInfo, BoardSurface boardSurface) {
        int tmpDay = this.day;
        int tmpOppositionCO = this.oppositionCO;
        int tmpMediumCO = this.mediumCO;
        int tmpDiscoveryWolf = this.discoveryWolf;
        day = gameInfo.getDay();
        oppositionCO =  boardSurface.comingoutRoleAgentList(Role.SEER).size() > 0 ? 1 : 0;
        mediumCO = boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0 ? 1 : 0;
        discoveryWolf = boardSurface.getMyInformation().peekDivIdenMap().getValue() == Species.WEREWOLF ? 1 : 0;
        if (tmpDay != day||
                tmpOppositionCO != oppositionCO ||
                tmpMediumCO != mediumCO ||
                tmpDiscoveryWolf != discoveryWolf) {
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, Integer> getStateSet () {
        HashMap<String, Integer> map = new HashMap<String, Integer>() {
            {
                put("day", day);
                put("oppositionCO", oppositionCO);
                put("mediumCO", mediumCO);
                put("discoveryWolf", discoveryWolf);
            }
        };
        return map;
    }
}

package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import java.util.*;

public class WerewolfDice extends Dice  {
    // 状態S
    private int day = 0;
    private int seerCONum = 0;
    private int mediumCO = 0;

    /* Q値 */
    private double q[][][][];

    /* 辿ってきた状態 */
    private List<Map<String, Integer>> route;

    /**
     * コンストラクタ
     */
    public WerewolfDice(DiceState diceState) {
        super(diceState);
    }

    /**
     * 状態のセット
     * @return
     */
    public boolean setDiceState (GameInfo gameInfo, BoardSurface boardSurface) {
        int day = gameInfo.getDay();
        int seerCONum = boardSurface.comingoutRoleAgentList(Role.SEER).size();
        int mediumCO = 0;
        if (boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0) {
            mediumCO = 1;
        }
        boolean isStateChenge = false;  // 状態が変化したか
        if (this.day != day ||
                this.seerCONum != seerCONum ||
                this.mediumCO != mediumCO) {
            isStateChenge = true;
        }

        this.day = day;
        this.seerCONum = seerCONum;
        this.mediumCO = mediumCO;
        return isStateChenge;
    }

    /**
     * ダイスを振る
     * eGreedy法を用いて局所解を回避
     * @return COするtrueしないfalse
     */
    public int shakeTheDice() {
        int doCO = 0;

        Random rand = new Random();
        int randNum = rand.nextInt(100+1);

        if (randNum > EPSILON * 100.0) {
            // εの確率　Q値が最大となるようなaを選択
            Log.trace("day" + day + "seerCONum" + seerCONum + " med" + mediumCO);
            if (q[day][seerCONum][mediumCO][0] < q[day][seerCONum][mediumCO][1]) {
                routeRecord(1);
                doCO = 1;
            } else {
                routeRecord(0);
            }
        } else {
            // (1-ε)の確率　ランダムにaを選択
            int randomInt = rand.nextInt(2);
            if (randomInt > 0) {
                routeRecord(1);
                doCO = 1;
            } else {
                routeRecord(0);
            }
        }
        return doCO;
    }

    /**
     * Qテーブルの更新
     * @param gameInfo
     * @param boardSurface
     */
    public void updateQTable(GameInfo gameInfo, BoardSurface boardSurface) {
        int reward = reward();
        double total_reward_t = 0.0;
        Collections.reverse(route);
        for (Map<String, Integer> map:
                route){
            total_reward_t = GAMMA * total_reward_t;    // 時間割引率
            // Q値を取得
            q[map.get("day")][map.get("seerCONum")][map.get("mediumCO")][map.get("action")] =
                    q[map.get("day")][map.get("seerCONum")][map.get("mediumCO")][map.get("action")] +
                            ALPHA * (reward + total_reward_t - q[map.get("day")][map.get("seerCONum")][map.get("mediumCO")][map.get("action")]);
            // ステップtより先でもらえた報酬の合計を更新
            total_reward_t += reward;
        }
    }

    /**
     * 報酬の計算
     * @return
     */
    protected int reward() {
        // 報酬の計算
//        int reward = 0;
//        for (Agent agent :
//                gameInfo.getAliveAgentList()) {
//            RoleState role = gameInfo.getRoleMap().get(agent);
//            if (role == RoleState.WEREWOLF) {    // 狼が生存していたら，狼勝利
//                reward += 100;
//                break;
//            }
//            if (agent.equals(gameInfo.getAgent())) {    // 生存
//                reward += 50;
//            }
//        }
//
//        for (Agent agent :
//                boardSurface.getExecutedAgentList()) {
//            if (agent.equals(gameInfo.getAgent())) {    // 吊られた
//                reward -= 200;
//            }
//        }
//
//        for (Agent agent :
//                boardSurface.getDivinedAgentList()) {
//            if (agent.equals(gameInfo.getAgent())) {    // 占われた
//                reward -= 100;
//            }
//        }
//        return reward;
        return 0;
    }

    /**
     * ルート保管
     * @param action
     */
    protected void routeRecord(int action) {
        route.add(new HashMap<String, Integer>() {{
            put("day", day);
            put("seerCONum", seerCONum);
            put("mediumCO", mediumCO);
            put("action", action);
        }});
    }

    @Override
    protected void initQ() {
        return;
    }

    /**
     * Q値初期化
     * @param maxGameDay
     */
//    private void initQ () {
//
//        Random rand = new Random();
//        for (int i = 0; i < maxGameDay; i++) {
//            for (int j = 0; j < playerNum; j++) {
//                for (int k = 0; k < 2; k++) {
//                    for (int a = 0; a < 2; a++) {
//                        int randNum = rand.nextInt(INIT_Q_MAX + 1);
//                        // 経験則に応じて加点をする
//                        int experiment = 0;
//                        // 1日目，2日目COは加点
//                        if (i == 0) {
//                            experiment += 100;  // 1日目
//                        } else if (i == 1) {
//                            experiment += 50;   // 2日目
//                        }
//                        // 占い師が1人しかCOしていないなら加点2人なら減点
//                        if (j == 1) {
//                            experiment += 100;
//                        } else if (j == 2) {
//                            experiment -= 50;
//                        }
//                        // 霊能者CO出た時は加点
//                        if (k == 1) {
//                            experiment += 100;
//                        }
//                        // 基本的にはCOするように加点
//                        if (a == 1) {
//                            experiment += 100;
//                        }
//                        q[i][j][k][a] = randNum + experiment;
//                    }
//                }
//            }
//        }
//    }
//

}

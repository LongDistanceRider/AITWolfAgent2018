package com.icloud.itfukui0922.processing.state.dice;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

import java.util.*;

/**
 *
 */
public class SeerDice extends Dice {

    // 状態S
    private int day = 0;            // 日付
    private int oppositionCO = 0;   // 対抗CO(boolean)
    private int mediumCO = 0;       // 占い師CO(boolean)
    private int discoveryWolf = 0;  // 黒判定(boolean)
    /* Q値 */
    private double q[][][][][];
    /* 辿ってきた状態 */
    private List<Map<String, Integer>> route;

    /**
     * コンストラクタ
     * @param maxGameDay　ゲーム最大日数
     */
    public SeerDice(int maxGameDay) {
        q = new double[maxGameDay][2][2][2][2]; // Q値定義
        initQ(maxGameDay);    // Q値初期化
        route = new ArrayList<>();  // routeの初期化
    }

    /**
     * 状態のセット
     * @param day
     * @param oppositionCO
     * @param mediumCO
     * @param discoveryWolf
     * @return 状態が変化した場合trueを返す
     */
    public boolean setDiceState (int day, boolean oppositionCO, boolean mediumCO, boolean discoveryWolf) {
        boolean isStateChenge = false;  // 状態が変化したか
        if (this.day == day ||
                this.oppositionCO == convertInteger(oppositionCO) ||
                this.mediumCO == convertInteger(mediumCO) ||
                this.discoveryWolf == convertInteger(discoveryWolf)) {
            isStateChenge = true;
        }

        this.day = day;
        this.oppositionCO = convertInteger(oppositionCO);
        this.mediumCO = convertInteger(mediumCO);
        this.discoveryWolf = convertInteger(discoveryWolf);
        return isStateChenge;
    }

    /**
     * ダイスを振る
     * eGreedy法を用いて局所解を回避
     * @return COするtrueしないfalse
     */
    public boolean shakeTheDice() {
        boolean doCO = false;

        Random rand = new Random();
        int randNum = rand.nextInt(100+1);

        if (randNum > EPSILON * 100.0) {
            // εの確率　Q値が最大となるようなaを選択
            Log.trace("day" + day + "opp" + oppositionCO + " med" + mediumCO + "dis" + discoveryWolf);
            if (q[day][oppositionCO][mediumCO][discoveryWolf][0] < q[day][oppositionCO][mediumCO][discoveryWolf][1]) {
                routeRecord(1);
                doCO = true;
            } else {
                routeRecord(0);
            }
        } else {
            // (1-ε)の確率　ランダムにaを選択
            int randomInt = rand.nextInt(2);
            if (randomInt > 0) {
                routeRecord(1);
                doCO = true;
            } else {
                routeRecord(0);
            }
        }
        return doCO;
    }

    /**
     * Qテーブルの更新
     */
    public void updateQTable(GameInfo gameInfo, BoardSurface boardSurface) {
        /* 報酬適用計画
         *
         *　モンテカルロ法を持ちいる
         */
        int reward = reward(gameInfo, boardSurface);
        double total_reward_t = 0.0;    // 現時点を含めたその先で得られた報酬
        Collections.reverse(route); // ルートを逆順に
        for (Map<String, Integer> map:
                route){
            total_reward_t = GAMMA * total_reward_t;    // 時間割引率
            // Q値を取得
            q[map.get("day")][map.get("oppositionCO")][map.get("mediumCO")][map.get("discoveryWolf")][map.get("action")] =
                    q[map.get("day")][map.get("oppositionCO")][map.get("mediumCO")][map.get("discoveryWolf")][map.get("action")] +
                            ALPHA * (reward + total_reward_t - q[map.get("day")][map.get("oppositionCO")][map.get("mediumCO")][map.get("discoveryWolf")][map.get("action")]);
            // ステップtより先でもらえた報酬の合計を更新
            total_reward_t += reward;
        }
    }
    /**
     * 報酬の計算
     * @param gameInfo
     * @param boardSurface
     * @return
     */
    private int reward(GameInfo gameInfo, BoardSurface boardSurface) {
        // 報酬の計算
        int reward = 0;
        for (Agent agent :
                gameInfo.getAliveAgentList()) {
            Role role = gameInfo.getRoleMap().get(agent);
            if (role == Role.WEREWOLF) {    // 狼が生存していたら，狼勝利
                reward -= 100;
                break;
            }
            if (agent.equals(gameInfo.getAgent())) {    // 生存
                reward += 50;
            }
        }
        for (Agent agent :
                boardSurface.getExecutedAgentList()) {
            if (agent.equals(gameInfo.getAgent())) {    // 吊られた
                reward -= 200;
            }
        }
        for (Agent agent :
                boardSurface.getBiteAgentList()) {
            if (agent.equals(gameInfo.getAgent())) {    // 噛まれた
                reward -= 100;
            }
        }
        return reward;
    }

    /**
     * ルート保管（辿ってきた状態を保管）
     * @param action
     */
    private void routeRecord(int action) {
        route.add(new HashMap<String, Integer>() {{
            put("day", day);
            put("oppositionCO", oppositionCO);
            put("mediumCO", mediumCO);
            put("discoveryWolf", discoveryWolf);
            put("action", action);
        }});
    }
    /**
     * booleanをintに変換
     * @param boo
     * @return
     */
    private int convertInteger (boolean boo) {
        if (boo) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Q値初期化
     * @param maxGameDay
     */
    private void initQ (int maxGameDay) {
        Random rand = new Random();
        for (int i = 0; i < maxGameDay; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    for (int l = 0; l < 2; l++) {
                        for (int a = 0; a < 2; a++) {
                            int randNum = rand.nextInt(INIT_Q_MAX + 1);
                            // 経験則に応じて加点をする
                            int experiment = 0;
                            // 1日目，2日目COは加点
                            if (i == 0) {
                                experiment += 100;  // 1日目
                            } else if (i == 1) {
                                experiment += 50;   // 2日目
                            }
                            // 対抗COでた時は加点
                            if (j == 1) {
                                experiment += 100;
                            }
                            // 霊能者CO出た時は加点
                            if (k == 1) {
                                experiment += 100;
                            }
                            // 黒出しした時は超加点
                            if (l == 1) {
                                experiment += 1000;
                            }
                            // 基本的にはCOするように加点
                            if (a == 1) {
                                experiment += 100;
                            }
                            q[i][j][k][l][a] = randNum + experiment;
                        }
                    }
                }
            }
        }
    }
}
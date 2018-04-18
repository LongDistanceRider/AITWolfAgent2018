package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.ComingoutContentBuilder;
import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.ContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;

import java.util.*;

public class SeerState implements RoleState{

    /* Qダイス */
    private double qDice[][][][][];
    /* Qランダム値の最大値 */
    private static final int INIT_Q_MAX = 30;
    /* 学習ルート */
    private List<Map<String, Integer>> route;
    /* ε-greedy法のε */
    private static final double EPSILON = 0.3;
    /* 学習率 */
    private static final double ALPHA = 0.1;
    /* 割引率 */
    private static final double GAMMA = 0.9;
    // 対抗CO 霊能者CO 黒出しをチェック
    int oppositionCO = 0;
    int mediumCO = 0;
    int discoveryWolf = 0;

    /**
     * コンストラクタ
     * @param maxGameDay
     */
    public SeerState(int maxGameDay) {
        // Qダイス初期化　最大ゲーム日時 boolean対抗CO boolean霊能者CO boolean黒出し ACTIONカミングアウトするか
        this.qDice = new double[maxGameDay][2][2][2][2];
        initQ(maxGameDay);
        // 学習ルート初期化
        route = new ArrayList<>();
    }

    @Override
    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
            // 占い結果の取り込み
            Judge divination = gameInfo.getDivineResult();
            if (divination != null) {
                boardSurface.putDivIdenMap(divination.getTarget(), divination.getResult());
            } else {
                System.err.println("占い結果取得失敗");
            }
        }
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface, int day) {
        // COするかしないかをダイスで決める
        boolean doCO = false;
        if (FlagManagement.getInstance().isComingOut()) {
            doCO = eGreedy(boardSurface, day);
        }

        LinkedList<String> talkQueue = new LinkedList<>();
        // ----- coming out -----
        if (doCO) {
            String comingOutSeerString = UtilState.coming_out(boardSurface.getMyInformation().getAgent(), Role.SEER);   // すでにCOして入ればnull返却
            if (comingOutSeerString != null) {
                talkQueue.add(comingOutSeerString);
            }
        }
        // ----- 占い結果報告 -----
        if (FlagManagement.getInstance().isComingOut()) {  // COしていれば結果報告をする
            String divinedResultString = UtilState.divinedResult(boardSurface);
            if (divinedResultString != null) {
                talkQueue.add(divinedResultString);
            }
        }
        return talkQueue;
    }

    /**
     * Qダイスの初期化
     * 経験則を入れて初期学習を助ける（が，強化学習の意味がない気がする）
     * 数値は適当のため調整必須
     */
    private void initQ(int maxGameDay) {
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
                            qDice[i][j][k][l][a] = randNum + experiment;
                        }
                    }
                }
            }
        }
    }

    /**
     * eGreedy法で行動を選択
     * COするかしないか
     *
     * 状況が変化しない場合は学習ルート保管をせず，返却値も前回と同様のものを返却するようにする（COすると返却した場合，以降ダイスを振らないため，実質falseを返す）
     */
    private boolean eGreedy(BoardSurface boardSurface, int day) {
        boolean doCO = false;
        // 状況変化チェックのため，一時変数
        int tmpOppositionCO = oppositionCO;
        int tmpMediumCO = mediumCO;
        int tmpDiscoveryWolfCO = discoveryWolf;

        if (boardSurface.comingoutRoleAgentList(Role.SEER).size() > 0) {
            oppositionCO = 1;   // 対抗CO
        }
        if (boardSurface.comingoutRoleAgentList(Role.MEDIUM).size() > 0) {
            mediumCO = 1;   // 霊能者CO
        }
        if (boardSurface.getMyInformation().peekDivIdenMap().getValue() == Species.WEREWOLF) {
            discoveryWolf = 1;  // 黒出し
        }

        //状況変化チェック



        Random rand = new Random();
        int randNum = rand.nextInt(100+1);

        if (randNum > EPSILON * 100.0) {   // 逆じゃね？
            // εの確率　Q値が最大となるようなaを選択
            if (qDice[day][oppositionCO][mediumCO][discoveryWolf][0] < qDice[day][oppositionCO][mediumCO][discoveryWolf][1]) {
                route.add(returnRouteMap(day, oppositionCO, mediumCO, discoveryWolf, 1));   // 学習ルート保管
                return true;    // COする
            } else {
                route.add(returnRouteMap(day, oppositionCO, mediumCO, discoveryWolf, 0));   // 学習ルート保管
                return false;   // COしない
            }
        } else {
            // (1-ε)の確率　ランダムにaを選択
            int randomInt = rand.nextInt(2);
            if (randomInt > 0) {
                route.add(returnRouteMap(day, oppositionCO, mediumCO, discoveryWolf, 1));   // 学習ルート保管
                return true;
            } else {
                route.add(returnRouteMap(day, oppositionCO, mediumCO, discoveryWolf, 0));    // 学習ルート保管
                return false;
            }
        }
    }

    /**
     * １ゲーム終了後に呼び出される予定
     *
     * 報酬の計算
     * 報酬内容
     * 勝敗
     * 吊られた
     * 噛まれた
     * 生き残った
     */
    public void finish (GameInfo gameInfo, BoardSurface boardSurface) {
        // 報酬の計算
        int reward = reward(gameInfo, boardSurface);
        // 報酬の適用

        /* 報酬適用計画
         *
         *　モンテカルロ法を持ちいる
         */
        // TODO モンテカルロ
        // 学習ルートを逆順に
        Collections.reverse(route);
        for (Map<String, Integer> map :
                route) {
            // 状態s'で行った時にQ値が最大となるような行動を調べる
            int maxA = 0;
            for (int i = 0; i < 2; i++) {
                if (qDice[map.get("day")][map.get("oppositionCO")][map.get("mediumCO)")][map.get("discoveryWolf")][maxA] <
                        qDice[map.get("day")][map.get("oppositionCO")][map.get("mediumCO)")][map.get("discoveryWolf")][i]) {
                    maxA = i;
                }
            }
            // Q値の更新
            qDice[map.get("day")][map.get("oppositionCO")][map.get("mediumCO)")][map.get("discoveryWolf")][map.get("action")] =
                    (1.0 - ALPHA) * qDice[map.get("day")][map.get("oppositionCO")][map.get("mediumCO)")][map.get("discoveryWolf")][map.get("action")] +
                            ALPHA * (reward + GAMMA * qDice[map.get("day")][map.get("oppositionCO")][map.get("mediumCO)")][map.get("discoveryWolf")][maxA]);

        }
//        int maxA = 0;
//        for (int i = 0; i < 4; i++) {
//            if (q[sdPosX][sdPosY][maxA] < q[sdPosX][sdPosY][i]) {
//                maxA = i;
//            }
//        }
//        // Q値の更新
//        q[sPosX][sPosY][a] = (1.0 - ALPHA) * q[sPosX][sPosY][a] + ALPHA * (r + GAMMA * q[sdPosX][sdPosY][maxA]);


    }

    /**
     * 学習ルート用のマップ作成
     * @param day
     * @param oppositionCO
     * @param mediumCO
     * @param discoveryWolf
     * @param action
     * @return
     */
    private Map<String, Integer> returnRouteMap (int day, int oppositionCO, int mediumCO, int discoveryWolf, int action) {
        return new HashMap<String, Integer>() {{
            put("day", day);
            put("oppositionCO", oppositionCO);
            put("mediumCO", mediumCO);
            put("discoveryWolf", discoveryWolf);
            put("action", action);
        }};
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
}

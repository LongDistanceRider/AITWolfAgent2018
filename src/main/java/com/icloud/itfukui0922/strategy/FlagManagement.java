package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;

import java.util.HashMap;
import java.util.Map;

public class FlagManagement {

    /* 0日目の挨拶 */
    private boolean isGreeting = false;
    /* coming outしたか */
    private boolean isComingOut = false;
    /* 占い（霊能・かたり）結果報告したか */
    private boolean isResultReport = false;
    /* finishフラグ管理（finishが2回呼ばれるため） */
    private boolean isFinish = false;
    /* あるエージェントに対して投票先発言をしたか */
    private Map<Agent, Boolean> isVoteUtteranceMap = new HashMap<>();

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isResultReport() {
        return isResultReport;
    }

    public void setResultReport(boolean resultReport) {
        isResultReport = resultReport;
    }

    public boolean isComingOut() {
        return isComingOut;
    }

    public void setComingOut(boolean comingOut) {
        isComingOut = comingOut;
    }

    public boolean isGreeting() {
        return isGreeting;
    }

    public void setGreeting(boolean greeting) {
        this.isGreeting = greeting;
    }

    public void putVoteUtteranceMap (Agent agent, Boolean boo) {
        isVoteUtteranceMap.put(agent, boo);
    }

    /**
     * 自然言語処理部門用対戦か，プロトコル部門対戦か
     * @return 自然言語処理部門対戦ならtrueをセット
     */
    public boolean isNLSwitch() {
        return true;
    }

    /**
     * あるエージェントに対して投票先発言をしたか
     * @param agent 投票対象
     * @return isVoteUtteranceMapにある場合はその値を，ない場合はfalseを返す
     */
    public boolean getVoteUtteranceMap (Agent agent) {
        return isVoteUtteranceMap.getOrDefault(agent, false);
    }

    /* Singleton処理 */
    /**
     * コンストラクタ
     */
    private static FlagManagement flagManagement = new FlagManagement();
    private FlagManagement() {}
    public static FlagManagement getInstance() {
        return flagManagement;
    }

    /**
     * 日にちが変わるときにリセットするフィールドの処理
     */
    public void dayReset() {
        isResultReport = false;
        isVoteUtteranceMap.clear();
    }
}

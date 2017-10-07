/**
 *  Q学習を扱うクラス
 *  編集中
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Qlearning implements Serializable{

    /* Q値MAP （このMAPに対して.get()しないこと 値が欲しい時にはretValue()を使う) TODO Q値MAP取得方法については要検討*/
    Map<SAKeypair, Double> qValueMap = new HashMap<>();
    /* 最後に参照したSAKeypair（報酬が直接与えられるS） TODO これは自動化できないものか*/
    SAKeypair lastSAKeypair;
    /* 割引率ガンマ */
    double gamma = 0.9;
    /* 学習係数アルファ */
    double alpha = 0.1;
    /* 報酬r */
    double reward = 1000;

    /* Singleton TODO Singletonしたほうがいいと思ったが，不必要かもしれない*/
    private static Qlearning qlearning = new Qlearning();
    private Qlearning(){}
    public static Qlearning getInstance() {
        return qlearning;
    }

    /**
     * Q値を更新します（これは間違っている．更新されない）
     *
     * @param saKeypair 更新するQ(s, a)
     * @param toReward ゲームに勝利する（ゴールに到達する）時にtrue, ゲームに負けた時はfalse
     * @return 新しいQ値
     */
    public double updateQvalue(SAKeypair saKeypair, boolean toReward) {
        BoardSurface state = saKeypair.getState();  // 更新するs
        double preQvalue = retQvalue(saKeypair);    // 更新前Q値
        double maxQvalue = retQvalue(saKeypair);    // 状態sにおいて取りうる行動aのうち最大ものも．つまりQ(s, a_1), Q(s, a_2), ... , Q(s, a_n)のうちの最大値

        if (saKeypair.equals(lastSAKeypair)) {  // 直接
            if (toReward) {
                return preQvalue + alpha * (reward = preQvalue);
            } else {
                return preQvalue;
            }
        } else {
            // 状態sにおいて取りうる行動aのうちQ値が最大のものを調べる
            for (Action action : Action.values()) {
                SAKeypair tmpsaKeypair = new SAKeypair(state, action);
                if (maxQvalue > retQvalue(tmpsaKeypair)) {
                    maxQvalue = retQvalue(tmpsaKeypair);
                }
            }
            return preQvalue + alpha * (gamma * maxQvalue - preQvalue);
        }
    }
    /**
     * Q値を返します
     * Q値を返したことがある場合（存在する場合）はその値を
     * 返したことがない場合は初期化処理として0から99の間のdouble型を返します
     *
     * @param saKeypair
     * @return
     */
    public double retQvalue (SAKeypair saKeypair) {
        if(qValueMap.containsKey(saKeypair)) {
            this.lastSAKeypair = saKeypair;
            return qValueMap.get(saKeypair);
        } else {
            double ret = Math.random() * 100;
            qValueMap.put(saKeypair, ret);
            this.lastSAKeypair = saKeypair;
            return ret;
        }
    }

}

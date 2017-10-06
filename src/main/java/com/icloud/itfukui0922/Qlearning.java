/**
 *
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Qlearning implements Serializable{

    /* Q値MAP （このMAPに対して.get()しないこと 値が欲しい時にはretValue()を使う)*/
    Map<SAKeypair, Double> qValueMap = new HashMap<>();
    /* 最後に参照したSAKeypair（報酬が直接与えられるS） */
    SAKeypair lastSAKeypair;
    /* 割引率ガンマ */
    double gamma = 0.9;
    /* 学習係数アルファ */
    double alpha = 0.1;
    /* 報酬r */
    double reward = 1000;

    /* Singleton */
    private static Qlearning qlearning = new Qlearning();
    private Qlearning(){}
    public static Qlearning getInstance() {
        return qlearning;
    }

    /**
     * Q値を更新します
     */
    public double updateQvalue(SAKeypair saKeypair, boolean toReward) {
        BoardSurface state = saKeypair.getState();
        double preQvalue = retQvalue(saKeypair);
        double upQvalue;
        double maxQvalue = retQvalue(saKeypair);

        if (saKeypair.equals(lastSAKeypair)) {
            if (toReward) {
                upQvalue = preQvalue + alpha * (reward = preQvalue);
            } else {
                upQvalue = preQvalue;
            }
        } else {
            // 状態sにおいて取りうる行動aのうちQ値が最大のものを調べる
            for (Action action: Action.values()) {
                SAKeypair tmpsaKeypair = new SAKeypair(state, action);
                if (maxQvalue > retQvalue(tmpsaKeypair)) {
                    maxQvalue = retQvalue(tmpsaKeypair);
                }
            }
            upQvalue = preQvalue + alpha * (gamma * maxQvalue - preQvalue);
        }
        return upQvalue;
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

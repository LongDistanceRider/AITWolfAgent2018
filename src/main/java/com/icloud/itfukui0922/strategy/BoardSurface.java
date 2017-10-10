/**
 * ゲームの盤面整理
 * このクラスはQ学習における状態sになる
 * 状態集合SはAITWolfクラスで管理する
 */
package com.icloud.itfukui0922.strategy;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardSurface {

    /* プレイヤ情報 */

    /* 自分のプレイヤ情報番号 */
    int myPlayerInfomation = 0;

    /* 占い結果 */
    List<Judge> divinationList = new ArrayList<>();

    public List<Judge> getDivinationList() {
        return divinationList;
    }
}

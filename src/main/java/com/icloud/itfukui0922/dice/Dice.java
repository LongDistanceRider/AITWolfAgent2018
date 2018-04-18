package com.icloud.itfukui0922.dice;

import com.icloud.itfukui0922.processing.state.RoleState;
import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameSetting;

import java.util.List;
import java.util.Map;

/**
 * 削除予定クラス
 * 意志決定のためのサイコロを管理
 * 全ての意思決定内容毎のサイコロを用意し，DiceMatrixにて管理されている確率を用いて傾きのあるランダム数値を返す
 */
public class Dice {

    /* ゲーム設定 */
    GameSetting gameSetting;
    /* 自身の役職 */
    RoleState myRole;

    /* ε-greedy法のε */
    private static final double EPSILON = 0.3;
    /* 学習率 */
    private static final double ALPHA = 0.1;
    /* 割引率 */
    private static final double GAMMA = 0.9;
    /* Qの初期値の最大値（乱数の最大値） */
    private static final int INIT_Q_MAX = 30;


    /* ----- サイコロ準備　----- */
    private double q[][][];
    int day;    // ゲーム内日時


    /* ----- 状況考慮b ----- */
    List<Agent> divinationResultList;   // 占い結果
    Map<Agent, Role> comingoutRoleMap;  // COした役職

    /* ----- 何を判断したいか（行動a） ----- */
//    private static final int

    /**
     * コンストラクタ
     *
     * @param gameSetting
     */
    public Dice(GameSetting gameSetting, RoleState roleState) {
        this.gameSetting = gameSetting;
        this.myRole = roleState;
    }

    /**
     * 状態の初期化
     */
    private void init() {

    }

    /**
     * COするかどうか（毎日サイコロを振る）
     * <p>
     * 状態s:
     * 役職
     * 日付
     * 狼発見
     * 村人発見
     * <p>
     * 評価r:
     * 勝った
     * 負けた
     * 釣られた
     * 噛まれた
     * 生き残った
     */

//    public boolean doComingout(Role role, int day, boolean discoveryWerewolf, boolean discoveryHuman) {
//        double probability = 0.5;   // COする確率
//        // 役職によってCOする確率を変動させる
////        switch (myRole) {
////            case SEER:
////                probability *= 1.9;
////        }
//    }

    /**
     * 襲撃先or投票先or護衛先or占い対象をどうするか
     * <p>
     * 状態s:
     * 役職
     * 日付
     * 黒出しエージェント
     * 占い師COエージェント
     * 霊能者COエージェント
     * 狩人COエージェント
     * 評価r:
     * 勝った
     * 負けた
     * 襲撃成功したか
     * 護衛成功したか
     * 黒出したか
     */

}

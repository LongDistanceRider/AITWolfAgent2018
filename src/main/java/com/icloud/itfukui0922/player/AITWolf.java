/**
 * AITWolfエージェント　メイン部分
 *
 * 可能な限り行数を減らしたい＝クラスの分化
 */
package com.icloud.itfukui0922.player;

import com.icloud.itfukui0922.Action;
import com.icloud.itfukui0922.ProtocolProcessing;
import com.icloud.itfukui0922.RoleSpecificProcessing;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AITWolf implements Player {

    /* 自然言語処理部門対応スイッチ */
    boolean NLSwitch = false;   // 自然言語処理部門に参加する場合はtrue，プロトコル部門はfalse
    /* トークリストをどこまで読み込んだか */
    int talkListHead;
    /* ゲーム情報 */
    GameInfo gameInfo;
    /* ゲーム設定情報 */
    GameSetting gameSetting;
    /* 役職固有の処理クラス */
    RoleSpecificProcessing roleSpecificProcessing;
    /* 盤面クラス */
    BoardSurface boardSurface;
    /* Actionリスト */
    List<Action> actionList = new ArrayList<>();
    LinkedList<String> talkQueue = new LinkedList<>();
    /* Switchクラス */
    FlagManagement flagManagement;

    @Override
    public String getName() {
        return "AITWolf";
    }   // プレイヤ名を返す

    @Override
    public void update(GameInfo gameInfo) {
        this.gameInfo = gameInfo;   // ゲーム情報更新

        // 発言内容取得
        for (int i = talkListHead; i < gameInfo.getTalkList().size(); i++) {
            Talk talk = gameInfo.getTalkList().get(i);  // 新規Talkを取得
            boardSurface.addTalkList(talk);     // 盤面クラスへtalkを保管
            // TODO talkに対する処理をここに書く
            if (NLSwitch) {
                // TODO 自然言語処理をここに書く
                // 自然言語をプロトコル言語に変換したあと，共通処理で処理する
            } else {
                // TODO プロトコル部門のみの処理をここに書く

            }
            // TODO NLPとプロトコル共通処理をここに書く
            // Talk内容を読み取り，BoardSurfaceへ保管する
            ProtocolProcessing.updateTalkInfo(talk, boardSurface);
        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        // ----- フィールド初期化処理 -----
        this.gameInfo = gameInfo;   // ゲーム情報の初期化
        this.gameSetting = gameSetting; // ゲーム設定の初期化
        this.roleSpecificProcessing = new RoleSpecificProcessing(); // 役職固有のクラスの初期化
        this.boardSurface = new BoardSurface(gameInfo); // 盤面クラスの初期化
        this.flagManagement = new FlagManagement(); // フラグ管理クラスの初期化
    }

    @Override
    public void dayStart() {
        // ----- 特定日時に実行させる処理 -----
        switch (gameInfo.getDay()) {
            case 0: // 0日目
                if (NLSwitch) {
                    // 挨拶を返す
                    if (!flagManagement.isGreeting()) {
                        talkQueue.add("0日目の挨拶です．");
                        flagManagement.setGreeting(true);
                    }
                    return;
                }
                break;

            case 1: // 1日目
                roleSpecificProcessing.setMyRole(gameInfo.getRole());   // 自分自身の役職を役職固有の処理にセット
                boardSurface.getMyInformation().setMyRole(gameInfo.getRole());   // プレイヤ情報に保管
                break;
            case 2: // 2日目
                break;
            default:

        }
        // ----- 各役職ごとの処理 -----
        roleSpecificProcessing.dayStart(gameInfo, boardSurface);

    }

    @Override
    public String talk() {
        // ----- 各役職ごとの処理 -----
        LinkedList<String> roleTalkQueue = roleSpecificProcessing.talk(boardSurface);
        if (NLSwitch) {
            // TODO 自然言語処理に関する処理をここに書く
        } else {
            // TODO プロトコル部門の処理に関する処理をここに書く
        }
        if (!roleTalkQueue.isEmpty()) {
            return roleTalkQueue.poll();
        }
        // NNによるActionダイス実行
        // ランダム関数を用いた仮クラスで行う
        //DeepLearningTmp.decisionMaking(boardSurfaceStack);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OVER";
    }

    @Override
    public String whisper() {
        // このメソッドは利用されない
        return null;
    }

    @Override
    public Agent vote() {
        // 生存プレイヤー内（自分自身を除く）からランダムに投票
        return ramdomElementSelect(aliveAgentListRemoveMe());
    }

    @Override
    public Agent attack() {
        // 生存プレイヤー内（自分自身を除く）からランダムにアタック
        return ramdomElementSelect(aliveAgentListRemoveMe());
    }

    @Override
    public Agent divine() {
        // 生存プレイヤー内（自分自身を除く）からランダムに占う
        return ramdomElementSelect(aliveAgentListRemoveMe());
    }

    @Override
    public Agent guard() {
        // ◯-◯進行をチェック

        // 戦略：1-1なら
        // 占い師がCOしていれば占い師を護衛する

        return null;
    }

    @Override
    public void finish() {
        // TODO メモリ，フィールドの初期化
    }

    /**
     * リストから要素を一つランダムに返す
     *
     * @param list
     * @param <T>
     * @return
     */
    private <T> T ramdomElementSelect(List<T> list) {
        if (list.isEmpty()) return null;
        else return list.get((int) (Math.random() * list.size()));
    }

    /**
     * 生存プレイヤーリストから自分自身を除いたリストを返す
     * @return 生存プレイヤーリスト（自分自身を除く）
     */
    private List<Agent> aliveAgentListRemoveMe() {
        List<Agent> aliveAgentList = gameInfo.getAliveAgentList();
        aliveAgentList.remove(gameInfo.getAgent());
        return aliveAgentList;
    }

}

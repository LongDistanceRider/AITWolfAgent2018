/**
 * AITWolfエージェント　メイン部分
 *
 * 可能な限り行数を減らしたい＝クラスの分化
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.PlayerInformation;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.ArrayList;
import java.util.List;

public class AITWolf implements Player {

    /* 自然言語処理部門対応スイッチ */
    private boolean NLSwitch = false;   // 自然言語処理部門に参加する場合はtrue，プロトコル部門はfalse
    /* ゲーム情報 */
    private GameInfo gameInfo;
    /* ゲーム設定情報 */
    private GameSetting gameSetting;
    /* プレイヤ情報リスト（自分自身も含む） */
    private List<PlayerInformation> playerInformationList = new ArrayList<>();
    /* 役職固有の処理クラス */
    private RoleSpecificProcessing roleSpecificProcessing = new RoleSpecificProcessing();
    /* トークリストをどこまで読み込んだか */
    private int talkListHead;
    /* BoardSurfaceリスト */
    List<BoardSurface> boardSurfacesList = new ArrayList<>();
    /* Actionリスト */
    List<Action> actionList = new ArrayList<>();

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
            // TODO talkに対する処理をここに書く
            if (NLSwitch) {
                // TODO 自然言語処理をここに書く
            } else {
                // TODO プロトコル部門のみの処理をここに書く
                // Talk内容を読み取り，BoardSurfaceへ保管する


            }
            // TODO NLPとプロトコル共通処理をここに書く

        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        // ----- フィールド初期化処理 -----
        this.gameInfo = gameInfo;   // ゲーム情報の初期化
        this.gameSetting = gameSetting; // ゲーム設定の初期化
        this.playerInformationList.clear(); // リスト初期化
        boardSurfacesList.add(new BoardSurface());  // 盤面リストへ初期状態sを入れる
        // -----  -----
    }

    @Override
    public void dayStart() {
        // ----- 特定日時に実行させる処理 -----
        switch (gameInfo.getDay()) {
            case 0: // 0日目
                break;
            case 1: // 1日目
                roleSpecificProcessing.setMyRole(gameInfo.getRole());
                break;
            case 2: // 2日目
                break;
            default:

        }
        // ----- 各役職ごとの処理 -----

    }

    @Override
    public String talk() {
        // ----- 各役職ごとの処理 -----
        
        if (NLSwitch) {
            // TODO 自然言語処理に関する処理をここに書く
        } else {
            // TODO プロトコル部門の処理に関する処理をここに書く

        }
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
        // このメソッドは利用されない
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

package com.icloud.itfukui0922;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.List;

public class AITWolf implements Player {

    /* ゲーム情報 */
    private GameInfo gameInfo;
    /* ゲーム設定情報 */
    private GameSetting gameSetting;
    /* トークリストをどこまで読み込んだか */
    private int talkListHead;

    @Override
    public String getName() {
        return "AITWolf";
    }

    @Override
    public void update(GameInfo gameInfo) {
        this.gameInfo = gameInfo;

        // 発言内容取得
        for (int i = talkListHead; i < gameInfo.getTalkList().size(); i++) {
            Talk talk = gameInfo.getTalkList().get(i);  // 新規Talkを取得
            // TODO talkに対する処理をここに書く

        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        this.gameInfo = gameInfo;
        this.gameSetting = gameSetting;
    }

    @Override
    public void dayStart() {

    }

    @Override
    public String talk() {
        return null;
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

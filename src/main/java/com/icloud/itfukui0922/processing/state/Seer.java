package com.icloud.itfukui0922.processing.state;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.processing.state.dice.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import org.aiwolf.client.lib.*;
import org.aiwolf.common.data.*;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.ui.TalkListRender;

import java.util.*;

public class Seer extends RoleState {

    /**
     * コンストラクタ
     * @param gameInfo  ゲーム情報
     */
    public Seer(GameInfo gameInfo) {
        super(gameInfo);
    }

    @Override
    public void update(GameInfo gameInfo) {
        super.gameInfo = gameInfo;
    }

    @Override
    public void dayStart(BoardSurface boardSurface) {
        // ----- 占い結果の取り込み -----
        if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
            // 占い結果の取り込み
            Judge divination = gameInfo.getDivineResult();
            if (divination != null) {
                boardSurface.putDivIdenMap(divination.getTarget(), divination.getResult());
                Log.debug("占い結果 target: " + divination.getTarget() + " result: " + divination.getResult());

                if (divination.getResult().equals(Species.WEREWOLF)) {  // 黒出ししたら，boardSurfaceに登録
                    boardSurface.getPlayerInformation(divination.getTarget()).setConvictionRole(Role.WEREWOLF);   // 確信した役職に人狼を加える
                }
            } else {
                Log.warn("占い結果の取得に失敗");
            }
        }
    }

    @Override
    public LinkedList<String> talk(BoardSurface boardSurface) {
        FlagManagement flagManagement = FlagManagement.getInstance();
        LinkedList<String> talkQueue = new LinkedList<>();
        // 占い師はRCOする
        if (!flagManagement.isComingOut()) {
            flagManagement.setComingOut(true);    // フラグセット
            ContentBuilder builder = new ComingoutContentBuilder(gameInfo.getAgent(), Role.SEER);  // CO発言生成
            String comingOutSeerString = new Content(builder).getText();    //CO発言
            talkQueue.add(comingOutSeerString); // 発言キューに追加
        }

        // ----- 占い結果報告 -----
        if (!flagManagement.isResultReport()) {  // 結果報告をしていない
            flagManagement.setResultReport(true); // 結果報告のフラグセット

            Map.Entry<Agent, Species> divinationResult = boardSurface.peekDivIdenMap(); // 占い結果取得
            ContentBuilder builder = new DivinedResultContentBuilder(divinationResult.getKey(), divinationResult.getValue());   // 占い結果発言生成
            String divinedResultString = new Content(builder).getText();  // 占い結果報告
            talkQueue.add(divinedResultString); // 発言キューに追加
            Log.trace("占い結果報告 target: " + divinationResult.getKey() + " result: " + divinationResult.getValue());
            // ----- 占い結果で黒が出た場合に投票先発言をする -----
            if (divinationResult.getValue().equals(Species.WEREWOLF)) {
                if (!flagManagement.getVoteUtteranceMap(divinationResult.getKey())) {   // target に投票発言をまだしていないなら
                    flagManagement.putVoteUtteranceMap(divinationResult.getKey(), true);   // フラグセット
                    ContentBuilder vBuilder = new VoteContentBuilder(divinationResult.getKey());    // 投票発言生成
                    String voteString = new Content(vBuilder).getText();    // 投票発言
                    talkQueue.add(voteString); // 発言キューに追加
                    Log.trace("投票先発言 target: " + divinationResult.getKey());
                }
            }
        }

        // ----- 偽占い師に対して投票発言とDISAGREE発言 -----
        List<Agent> oppositionAgentList = boardSurface.getOppositionAgentList();    // 対抗エージェントの取得
        if (!oppositionAgentList.isEmpty()) {   // 対抗エージェントがいるなら
            for (Agent agent :
                    oppositionAgentList) {
                ContentBuilder builder = new VoteContentBuilder(agent);
                String voteString = new Content(builder).getText(); // 投票発言
                talkQueue.add(voteString);
                Log.trace("投票先発言 target: " + agent);

                List<Talk> talkList = boardSurface.getTalkList();   // talkリストを取得
                for (Talk talk :
                        talkList) {
                    Content content = new Content(talk.getText());  // talkを分解
                    if (content.getTopic().equals(Topic.COMINGOUT) && content.getRole().equals(Role.SEER)) {   // 占い師CO発言を取り出す
                        ContentBuilder dissbuilder = new DisagreeContentBuilder(TalkType.TALK, talk.getDay(), talk.getIdx());
                        String disagreeString = new Content(dissbuilder).getText();    // 非同意発言
                        talkQueue.add(disagreeString);
                        Log.trace("DISAGREE発言 target: " + talk.getAgent() + " ID: " + talk.getIdx());
                    }
                }
            }
        }

        return talkQueue;
    }

    @Override
    public void finish(BoardSurface boardSurface) {

    }
}

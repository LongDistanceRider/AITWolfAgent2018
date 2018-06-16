package com.icloud.itfukui0922.player;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.processing.pro.ProtocolProcessing;
import com.icloud.itfukui0922.processing.state.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.*;

public class AITWolfPro implements Player{

    /* トークリストをどこまで読み込んだか */
    private int talkListHead;
    /* ゲーム情報 */
    private GameInfo gameInfo;
    /* ゲーム設定情報 */
    private GameSetting gameSetting;
    /* 発言リスト */
    private LinkedList<String> talkQueue = new LinkedList<>();
    /* 自分自身の役職 */
    private RoleState roleState = null;

    @Override
    public String getName() {
        return "AITWolfPro";
    }

    @Override
    public void update(GameInfo gameInfo) {
        BoardSurface boardSurface = BoardSurface.getInstance();
        FlagManagement flagManagement = FlagManagement.getInstance();
        this.gameInfo = gameInfo;

        for (int i = talkListHead; i < gameInfo.getTalkList().size(); talkListHead++) {
            Talk talk = gameInfo.getTalkList().get(i);
            boardSurface.addTalkList(talk); //  ログ上の全てのログが入る（自分自身の発言も）
            if (talk.getAgent().equals(gameInfo.getAgent())) {  // 自分自身の発言はスキップ
                continue;
            }
            ProtocolProcessing.updateTalkInfo(talk, boardSurface);
        }
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {
        Log.debug("initialize実行");
        this.gameInfo = gameInfo;
        this.gameSetting = gameSetting;
        BoardSurface.initialize(gameInfo);
        FlagManagement.getInstance().setFinish(false); // finishフラグのリセット
    }

    @Override
    public void dayStart() {
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        Log.info("\t\t" + gameInfo.getDay() + "day start");
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        BoardSurface boardSurface = BoardSurface.getInstance();
        FlagManagement flagManagement = FlagManagement.getInstance();

        // ----- 特定日時に実行させる処理 -----
        switch (gameInfo.getDay()) {
            case 1: // 1日目
                roleSet();  // 役職セット
                boardSurface.getMyInformation().setMyRole(gameInfo.getRole());   // プレイヤ情報に保管
                // ----- 各役職ごとの処理 -----
                roleState.dayStart(gameInfo, boardSurface);
                break;
            case 2: // 2日目
                // 被投票者
                Agent executedAgent = gameInfo.getExecutedAgent();
                boardSurface.getExecutedAgentList().add(executedAgent);
                Log.info("追放者 : " + executedAgent);
                // 被噛み　null の場合はGJ発生
                Agent attackedAgent = null;
                for (Agent agent :
                        gameInfo.getLastDeadAgentList()) {  // ここには前日の追放者と被害者の2つのAgentが入ってくると思っているが正しいか？
                    if (!agent.equals(executedAgent)) {
                        attackedAgent = agent;
                    }
                }
                if (attackedAgent != null) {
                    boardSurface.getBiteAgentList().add(attackedAgent);
                    Log.info("被害者 : " + executedAgent);
                } else {
                    // TODO GJ発生時の処理を書くこと
                    Log.info("被害者 : なし（GJ発生）");
                }
                // ----- 各役職ごとの処理 -----
                roleState.dayStart(gameInfo, boardSurface);
                break;
            default:

        }

        // ----- 日をまたぐごとに初期化するフラグ -----
        flagManagement.dayReset();

    }

    @Override
    public String talk() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        FlagManagement flagManagement = FlagManagement.getInstance();
        // ----- 各役職ごとの処理 -----
        LinkedList<String> roleTalkQueue = roleState.talk(gameInfo, boardSurface); // nullが入ってくることがある
        if (roleTalkQueue != null) {
            talkQueue.addAll(roleTalkQueue);
        }

        if (talkQueue != null &&!talkQueue.isEmpty()) { // nullチェックいらないかも
            return talkQueue.poll();
        }
        return "Over";
    }

    @Override
    public String whisper() {
        return null;
    }

    @Override
    public Agent vote() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        // 役職COしたプレイヤの中から追放先を決定する
        // 人狼COしたプレイヤの中から追放する
        List<Agent> werewolfList = boardSurface.comingoutRoleAgentList(Role.WEREWOLF);
        if (!werewolfList.isEmpty()) {
            Agent voteAgent = Utility.randomElementSelect(werewolfList);
            Log.info("投票先: " + voteAgent);
            return voteAgent;
        }

        // 霊能者COしたプレイヤから追放する
        List<Agent> mediumList = boardSurface.comingoutRoleAgentList(Role.MEDIUM);

        if (!mediumList.isEmpty()) {
            int tmpTurn = 0;    // 発言順番をつけるため，一時変数を用意
            int changeCount = 0;   // 発言順番に差があったか
            Agent tmpAgent = null;
            for (Agent agent :
                    mediumList) {
                List<Talk> talkDayAgentList = Utility.searchTalk(boardSurface.getTalkList(), gameInfo.getDay(), agent);
                List<Talk> talkCO = Utility.searchTalk(talkDayAgentList, Topic.COMINGOUT); // 霊能者COしたプレイヤの霊能COTalkが手に入るはずだが，複数COしていると2つ入ってくる
                int turn = talkCO.get(talkCO.size() - 1).getTurn();    // 最後にCOしたもののみを取ってくる
                if (turn > tmpTurn) {
                    tmpTurn = turn;
                    tmpAgent = agent;
                    changeCount += 1;   // 霊能者COがいる場合，ここは1度は呼ばれる
                }
            }
            // change が2以上の時，ターンに差があったとして，tmpAgent(ターンが遅い人->後出しCO)に投票する
            if (changeCount >= 2) {
                Log.info("投票先: " + tmpAgent);
                return tmpAgent;
            }

            Agent voteAgent = Utility.randomElementSelect(mediumList);
            Log.info("投票先: " + voteAgent);
            return voteAgent;
        }

        // 占い師COしたプレイヤから追放する
        List<Agent> seerList = boardSurface.comingoutRoleAgentList(Role.SEER);
        if (!seerList.isEmpty()) {
            Agent voteAgent = Utility.randomElementSelect(seerList);
            Log.info("投票先: " + voteAgent);
            return voteAgent;
        }

        // 狩人COしたプレイヤから追放する
        List<Agent> bodyguardList = boardSurface.comingoutRoleAgentList(Role.BODYGUARD);
        if (!bodyguardList.isEmpty()) {
            Agent voteAgent = Utility.randomElementSelect(bodyguardList);
            Log.info("投票先: " + voteAgent);
            return voteAgent;
        }

        // 生存プレイヤー内（自分自身を除く）からランダムに投票
        Agent voteAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
        Log.info("投票先 : " + voteAgent);
        return voteAgent;
    }

    @Override
    public Agent attack() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        Agent attackAgent;
        // とりあえず占い師COした人物を対象に，いない場合は霊能者，次に狩人の順で対象を決定する
        List<org.aiwolf.common.data.Role> checkRole = new ArrayList<>(Arrays.asList(org.aiwolf.common.data.Role.SEER, org.aiwolf.common.data.Role.BODYGUARD, org.aiwolf.common.data.Role.BODYGUARD));
        List<Agent> coming_outAgentList;    // 初期化してないけどいいのかな
        for (org.aiwolf.common.data.Role role :
                checkRole) {
            coming_outAgentList = boardSurface.comingoutRoleAgentList(role);
            if(!coming_outAgentList.isEmpty()) {
                attackAgent = Utility.randomElementSelect(coming_outAgentList);    // リストが空でなければリストからランダムに返す
                Log.info("襲撃先 : " + attackAgent);
                return attackAgent;
            }
        }
        attackAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 誰もいなければランダムに返す
        Log.info("襲撃先 : " + attackAgent);
        return attackAgent;
    }

    @Override
    public Agent divine() {
        // 生存プレイヤー内（自分自身を除く）からランダムに占う
        Agent divineAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
        Log.info("占い先 : " + divineAgent);
        return divineAgent;
    }

    @Override
    public Agent guard() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        Agent guardAgent;
        // ◯-◯進行をチェック
        // 占い師1ならその占い師を，2以上なら霊能者を護衛（霊能者が二人の時は適当に選ぶ），霊能者が不在なら適当に選ぶ
        List<Agent> comingoutSeerAgentList = boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.SEER);
        List<Agent> comingoutMediumAgentList = boardSurface.comingoutRoleAgentList(org.aiwolf.common.data.Role.MEDIUM);

        int numberOfSeer = comingoutSeerAgentList.size();
        int numberOfMedium = comingoutMediumAgentList.size();

        if (numberOfSeer == 1) {
            guardAgent = comingoutSeerAgentList.get(0);    // 占い師護衛
            Log.info("護衛先 : " + guardAgent);
            return guardAgent;
        } else if (numberOfSeer > 1 && numberOfMedium == 1) {
            guardAgent = comingoutMediumAgentList.get(0);  // 霊能者護衛
            Log.info("護衛先 : " + guardAgent);
            return guardAgent;
        }
        guardAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));   // 適当なプレイヤーを返す
        Log.info("護衛先 : " + guardAgent);
        return guardAgent;
    }

    @Override
    public void finish() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        FlagManagement flagManagement = FlagManagement.getInstance();
        if (flagManagement.isFinish()) {  // finishが2回目に呼び出されるとき，処理をしない
            return;
        }
        flagManagement.setFinish(true);
        Log.debug("finish実行");
        // TODO メモリ，フィールドの初期化


        // 役職finish()時の処理
        roleState.finish(gameInfo, boardSurface);

        // 参加プレイヤのリザルト出力
        Map<Agent, Role> agentMap = gameInfo.getRoleMap();
        for (Agent agent:
                agentMap.keySet()){
            Log.info(agent + " RoleState : " + agentMap.get(agent));
        }
        // ----- ログ出力停止 -----
        // 勝敗結果をログ出力
        if (Utility.isVillageSideWin(gameInfo)) {
            Log.info("勝敗結果: 村人陣営 勝利");
        } else {
            Log.info("勝敗結果: 人狼陣営 勝利");
        }

        Log.endLog();
    }
    /**
     * 役職セット
     */
    private void roleSet() {
        BoardSurface boardSurface = BoardSurface.getInstance();
        org.aiwolf.common.data.Role role = gameInfo.getRole();
        Log.info("自分の役職 : " + role);
        switch (role) {
            case SEER:
                roleState = new Seer(gameInfo, boardSurface);
                break;
            case MEDIUM:
                roleState = new Medium(gameInfo, boardSurface);
                break;
            case BODYGUARD:
                roleState = new Bodyguard(gameInfo, boardSurface);
                break;
            case POSSESSED:
                roleState = new Possessed(gameInfo, boardSurface);
                break;
            case WEREWOLF:
                roleState = new Werewolf(gameInfo, boardSurface);
                break;
            default:
                roleState = new Villager(gameInfo, boardSurface);
        }
    }
}

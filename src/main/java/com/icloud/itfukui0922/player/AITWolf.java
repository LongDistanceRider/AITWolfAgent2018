package com.icloud.itfukui0922.player;

import com.icloud.itfukui0922.log.Log;
import com.icloud.itfukui0922.log.LogLevel;
import com.icloud.itfukui0922.processing.NaturalLanguageProcessing;
import com.icloud.itfukui0922.processing.ProtocolProcessing;
import com.icloud.itfukui0922.processing.state.*;
import com.icloud.itfukui0922.strategy.BoardSurface;
import com.icloud.itfukui0922.strategy.FlagManagement;
import com.icloud.itfukui0922.util.Utility;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

import java.util.*;

import static com.icloud.itfukui0922.strategy.FlagManagement.*;

/**
 * AITWolfエージェント　メイン部分
 *
 * @author fukurou
 * @version 1.0
 *
 * 更新内容
 *  
 *  1.0 バージョン管理開始
 */

public class AITWolf implements Player {

    /* トークリストをどこまで読み込んだか */
    private int talkListHead;
    /* ゲーム情報 */
    private GameInfo gameInfo;
    /* ゲーム設定情報 */
    private GameSetting gameSetting;
    /* 盤面クラス */
    private BoardSurface boardSurface;
    /* 発言リスト */
    private LinkedList<String> talkQueue = new LinkedList<>();
    /* 自分自身の役職 */
    private Role roleState = null;

    /**
     * コンストラクタ
     * @param consoleLevel コンソール出力レベル
     * @param writeLevel ファイル出力レベル
     */
    public AITWolf(LogLevel consoleLevel, LogLevel writeLevel, boolean NLSwitch) {
        // ----- ログ出力開始 -----
        Log.init(consoleLevel, writeLevel);   // コンソール出力レベル, ファイル出力レベル
        // ----- 自然言語処理実行か -----
        FlagManagement.getInstance().setNLSwitch(NLSwitch);
    }

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
            boardSurface.addTalkList(talk);     // 盤面クラスへtalkを保管（自分自身の発言が入る）
            Log.info("発言内容 : " + talk.getAgent() + " > " + talk.getText()); // 「発言者 > 発言内容」をログ出力
            if (talk.getAgent().equals(gameInfo.getAgent())) {  // 自分自身の発言は処理をしないため，スキップ
                continue;
            }

            List<String> textList = new ArrayList<>();  // 処理をする文を保管（プロトコルの場合は1つ，自然言語処理の場合は2文受け取る場合があるため，2つ以上はいる）
            if (FlagManagement.getInstance().isNLSwitch()) {    // 自然言語処理をするか
                // ----- NLP処理 -----
                textList.addAll(NaturalLanguageProcessing.convertPro(gameInfo, boardSurface, talk));    // プロトコル文を返却
            } else {
                // ----- プロトコル部門のみの処理 -----
                textList.add(talk.getText());   // 文をそのまま追加
            }
            // NLPとプロトコルの共通処理
            // Talk内容を読み取り，BoardSurfaceへ保管する
            for (String text :
                    textList) {
                ProtocolProcessing.updateTalkInfo(talk, text, boardSurface);    // 処理する文を順々にいれていく
            }
        }
        // talkListHeadの更新
        talkListHead = gameInfo.getTalkList().size();
    }

    @Override
    public void initialize(GameInfo gameInfo, GameSetting gameSetting) {

        Log.debug("initialize実行");
        // ----- フィールド初期化処理 -----
        this.gameInfo = gameInfo;   // ゲーム情報の初期化
        this.gameSetting = gameSetting; // ゲーム設定の初期化
        this.boardSurface = new BoardSurface(gameInfo); // 盤面クラスの初期化
        getInstance().setFinish(false);  // フィニッシュフラグをリセット
    }

    @Override
    public void dayStart() {
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        Log.info("\t\t" + gameInfo.getDay() + "day start");
        Log.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
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
        getInstance().dayReset();
    }

    @Override
    public String talk() {
        // ----- 0日目挨拶 -----
        if (gameInfo.getDay() == 0) {
            if (getInstance().isGreeting()) {
                return "Over";
            } else {
                getInstance().setGreeting(true);
                return "こんにちは";
            }
        }
        // ----- 各役職ごとの処理 -----
        LinkedList<String> roleTalkQueue = roleState.talk(gameInfo, boardSurface); // nullが入ってくることがある
        if (roleTalkQueue != null) {
            talkQueue.addAll(roleTalkQueue);
        }

        if (FlagManagement.getInstance().isNLSwitch()) {
            // TODO 自然言語処理に関する処理をここに書く
        } else {
            // TODO プロトコル部門の処理に関する処理をここに書く
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
        // 生存プレイヤー内（自分自身を除く）からランダムに投票
        Agent voteAgent = Utility.randomElementSelect(Utility.aliveAgentListRemoveMe(gameInfo));
        Log.info("投票先 : " + voteAgent);
        return voteAgent;
    }

    @Override
    public Agent attack() {
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
        if (getInstance().isFinish()) {  // finishが2回目に呼び出されるとき，処理をしない
            return;
        }
        getInstance().setFinish(true);
        Log.debug("finish実行");
        // TODO メモリ，フィールドの初期化


        // 役職finish()時の処理
        roleState.finish(gameInfo, boardSurface);

        // 参加プレイヤのリザルト出力
        Map<Agent, org.aiwolf.common.data.Role> agentMap = gameInfo.getRoleMap();
        for (Agent agent:
                agentMap.keySet()){
            Log.info(agent + " Role : " + agentMap.get(agent));
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

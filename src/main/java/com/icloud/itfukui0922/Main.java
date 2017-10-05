import com.icloud.itfukui0922.AITWolf;
import com.icloud.itfukui0922.Dammy;
import com.icloud.itfukui0922.LocalHostStarter;
import org.aiwolf.common.data.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ----- ローカルホストで対戦開始 -----
        // 参加プレイヤーリスト作成
        List<Player> participantsPlayerList = new ArrayList<Player>() {
            {
                add(new AITWolf());
                add(new Dammy());
                add(new Dammy());
                add(new Dammy());
                add(new Dammy());
                x1
            }
        };

        // 対戦回数を指定
        int gameNum = 1;

        LocalHostStarter localHostStarter = new LocalHostStarter(participantsPlayerList, gameNum);  // インスタンス生成
        localHostStarter.start();   // ローカルホストで対戦開始
    }
}

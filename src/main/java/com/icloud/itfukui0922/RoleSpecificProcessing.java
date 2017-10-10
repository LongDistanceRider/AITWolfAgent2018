/**
 * 役職固有の処理
 */
package com.icloud.itfukui0922;

import com.icloud.itfukui0922.strategy.BoardSurface;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.net.GameInfo;

public class RoleSpecificProcessing {
    /* 自分自身の役職 */
    Role myRole;

    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }

    public void dayStart(GameInfo gameInfo, BoardSurface boardSurface) {
        switch (myRole) {
            case MEDIUM:    // 占い師の処理
                if (0 < gameInfo.getDay()) {    // 0日目は占い結果が取得できないため，回避
                    // 占い結果の取り込み
                    Judge divination = gameInfo.getDivineResult();
                    if (divination != null) {
                        boardSurface.getDivinationList().add(divination);
                    } else {
                        System.err.println("占い結果取得失敗");
                    }
                }
                break;
            default:
        }
    }
}

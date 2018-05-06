package com.icloud.itfukui0922.strategy;

public class FlagManagement {

    /* NLスイッチ */
    private boolean NLSwitch = true;
    /* 0日目の挨拶 */
    private boolean isGreeting = false;
    /* coming outしたか */
    private boolean isComingOut = false;
    /* 占い（霊能・かたり）結果報告したか */
    private boolean isResultReport = false;
    /* finishフラグ管理（finishが2回呼ばれるため） */
    private boolean isFinish = false;

    public boolean isFinish() {
        return isFinish;
    }

    public boolean isNLSwitch() {
        return NLSwitch;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isResultReport() {
        return isResultReport;
    }

    public void setResultReport(boolean resultReport) {
        isResultReport = resultReport;
    }

    public boolean isComingOut() {
        return isComingOut;
    }

    public void setComingOut(boolean comingOut) {
        isComingOut = comingOut;
    }

    public boolean isGreeting() {
        return isGreeting;
    }

    public void setGreeting(boolean greeting) {
        this.isGreeting = greeting;
    }

    /* Singleton処理 */
    /**
     * コンストラクタ
     */
    private static FlagManagement flagManagement = new FlagManagement();
    private FlagManagement() {}
    public static FlagManagement getInstance() {
        return flagManagement;
    }
}

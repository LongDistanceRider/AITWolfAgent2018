package com.icloud.itfukui0922.processing.nl;

import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

public class TransNL {

    public static String translateNL (Role role) {
        switch (role) {
            case SEER:
                return "占い師";
            case POSSESSED:
                return "裏切り者";
            case MEDIUM:
                return "霊能者";
            case BODYGUARD:
                return "狩人";
            case WEREWOLF:
                return "人狼";
            case VILLAGER:
                return "村人";
        }
        return "";
    }

    public static String translateNL (Species species) {
        switch (species) {
            case HUMAN:
                return "人間";
            case WEREWOLF:
                return "人狼";
        }
        return "";
    }
}

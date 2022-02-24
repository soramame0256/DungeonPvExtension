package com.github.soramame0256.dungeonpvextension.utils;

public class ArrayUtilities {
    public static boolean isContain(Object[] alpha, Object beta){
        for (Object o : alpha) {
            if (o.equals(beta)){
                return true;
            }
        }
        return false;
    }
    public static boolean isStringContainsInList(String[] alpha, String beta){
        for (String o : alpha) {
            if (o.contains(beta)){
                return true;
            }
        }
        return false;
    }
}

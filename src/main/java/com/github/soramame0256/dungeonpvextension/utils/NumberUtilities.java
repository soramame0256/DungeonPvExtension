package com.github.soramame0256.dungeonpvextension.utils;

public class NumberUtilities {
    public static String commaSeparate(Integer i){
        return commaSeparate(i.longValue());
    }
    public static String commaSeparate(Double d) {
        String inputString = d.toString();
        if (d.compareTo(Math.floor(d)) == 0) {
            return commaSeparate(d.longValue());
        }
        if (inputString.contains(".")) {
            if (inputString.contains("E")) {
                return commaSeparate(((Double) Math.floor(d)).longValue());
            }else{
                return commaSeparate(((Double) Math.floor(d)).longValue()) + "." + inputString.split("\\.")[1];
            }
        }else{
            return commaSeparate(d.longValue());
        }
    }
    public static String commaSeparate(Long l){
        String str = l.toString();
        StringBuilder ret = new StringBuilder();
        int ln = 0;
        StringBuilder strb = new StringBuilder(str);
        for (String s : strb.reverse().toString().split("")) {
            ln++;
            if (ln<4){
                ret.insert(0, s);
            }else{
                ret.insert(0, ",");
                ret.insert(0, s);
                ln = 1;
            }
        }
        return ret.toString().replace("-,","-");
    }
    public static String commaSeparate(Float f){
        return commaSeparate(f.doubleValue());
    }
    public static String commaSeparate(Short s){
        return commaSeparate(s.doubleValue());
    }
    public static String toTime(Long l){
        long hour = l/3600;
        long min = (l%3600)/60;
        long sec = l%60;
        return (hour==0?"":hour+"h")+(min==0?"":min+"m")+(sec==0?"":sec+"s");
    }
    public static Boolean canParse(String s){
        double ignored;
        try{
            ignored = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

}

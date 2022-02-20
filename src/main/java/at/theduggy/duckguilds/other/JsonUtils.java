package at.theduggy.duckguilds.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class JsonUtils {


    public static String toPrettyJsonString(String stringToWrap){
        ArrayList<Integer> doubleDots = new ArrayList<>();
        char[] chars = stringToWrap.toCharArray();
        for (int i=0; i!= chars.length;i++){
            if (chars[i]==':'){
                doubleDots.add(i);
            }
        }
        Collections.sort(doubleDots);
        return parseToPrettyString( doubleDots, stringToWrap);
    }

    private static String parseToPrettyString(ArrayList<Integer> doubleDots, String stringToWrap){
        char[] chars =  stringToWrap.toCharArray();
        StringBuilder wrapped = new StringBuilder();
        int tabPos = 1;
        for (int i=0;i!=chars.length;i++){
            if (chars[i]=='{'){
                if (i==0){
                    wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                }else {
                    tabPos = tabPos + 1;
                    wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                }
            }else if (chars[i]=='}'){
                if (i==chars.length-1){
                    wrapped.append("\n" + chars[i]);
                }else {
                    tabPos=tabPos-1;
                    wrapped.append("\n" + "\t".repeat(tabPos) + chars[i]);
                }
            }else if (chars[i]==','){
                if (getDoubleDotAfter(i, doubleDots)<getDoubleDotBehind(i, doubleDots)){
                    wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                }else if (chars[i-1]==']'||chars[i-1]=='}'){
                    wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                }else if (getDoubleDotBehind(i, doubleDots)<getDoubleDotAfter(i, doubleDots)){
                    if (chars[getDoubleDotBehind(i, doubleDots) + 1]=='['||chars[getDoubleDotBehind(i, doubleDots) + 1]=='{'){
                        wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                    }else {
                        wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                    }
                }else {
                    wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
                }
            }else if (chars[i]=='['){
                tabPos = tabPos + 1;
                wrapped.append(chars[i] + "\n" + "\t".repeat(tabPos));
            }else if (chars[i]==']'){
                tabPos=tabPos-1;
                wrapped.append("\n" + "\t".repeat(tabPos) + chars[i]);
            }else {
                wrapped.append(chars[i]);
            }
        }
        return wrapped.toString();
    }



    private static Integer getDoubleDotBehind(int pos, ArrayList<Integer> doubleDots){
        ArrayList<Integer> smaller = new ArrayList<>();
        HashMap<Integer, Integer> index = new HashMap<>();
        for (int i:doubleDots){
            if (i<pos){
                smaller.add(i);
            }
        }
        ArrayList<Integer> ranges = new ArrayList<>();
        for (int i:smaller){
            ranges.add(pos-i);
            index.put(pos-i, i);
        }
        if (ranges.size()>0) {
            return index.get(Collections.min(ranges));
        }else {
            return null;
        }
    }

    private static Integer getDoubleDotAfter(int pos, ArrayList<Integer> doubleDots){
        ArrayList<Integer> smaller = new ArrayList<>();
        HashMap<Integer, Integer> index = new HashMap<>();
        for (int i:doubleDots){
            if (i>pos){
                smaller.add(i);
            }
        }
        ArrayList<Integer> ranges = new ArrayList<>();
        for (int i:smaller){
            ranges.add(pos-i);
            index.put(pos-i, i);
        }
        if (ranges.size()>0) {
            return index.get(Collections.min(ranges));
        }else {
            return null;
        }
    }
}

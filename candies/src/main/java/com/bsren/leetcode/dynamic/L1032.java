package com.bsren.leetcode.dynamic;


class StreamChecker {

    String[] words;

    StringBuilder sb = new StringBuilder();
    public StreamChecker(String[] words) {
        this.words = words;
    }

    public boolean query(char letter) {
        sb.append(letter);
        for (String word : words) {
            if(sb.length()<word.length()){
                continue;
            }
            if(sb.substring(sb.length()-word.length()).equals(word)){
                return true;
            }
        }
        return false;
    }
}

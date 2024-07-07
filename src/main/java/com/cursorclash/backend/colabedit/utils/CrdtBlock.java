package com.cursorclash.backend.colabedit.utils;

class CharInfo {
    char c;
    boolean isBold;
    boolean isItalic;
    boolean isUnderline;
    boolean isStrikethrough;
    boolean isHighlight;
    CharInfo(char c) {
        this.c = c;
        this.isBold = false;
        this.isItalic = false;
        this.isUnderline = false;
        this.isStrikethrough = false;
        this.isHighlight = false;
    }

    public String toString() {
        String str = "";
        if (this.isBold) {
            str += "<b>";
        }
        if (this.isItalic) {
            str += "<i>";
        }
        if (this.isUnderline) {
            str += "<u>";
        }
        if (this.isStrikethrough) {
            str += "<s>";
        }
        if (this.isHighlight) {
            str += "<mark>";
        }
        str += this.c;
        if (this.isHighlight) {
            str += "</mark>";
        }
        if (this.isStrikethrough) {
            str += "</s>";
        }
        if (this.isUnderline) {
            str += "</u>";
        }
        if (this.isItalic) {
            str += "</i>";
        }
        if (this.isBold) {
            str += "</b>";
        }
        return str;
    }
}
public class CrdtBlock {
    double index;
    // pointer to the CharInfo object
    CharInfo charInfo;
    int userId;
    // timestamp
    String timestamp;


    CrdtBlock(double index, char c, int userId) {
        this.index = index;
        this.charInfo = new CharInfo(c);
        this.userId = userId;
        // set it to the current time
        this.timestamp = java.time.LocalTime.now().toString();
    }

    // override the toString method
    public String toString() {
        return "idx: " + this.index + ", c: " + this.charInfo + ", userId: " + this.userId + ", timestamp: " + this.timestamp;
    }
}

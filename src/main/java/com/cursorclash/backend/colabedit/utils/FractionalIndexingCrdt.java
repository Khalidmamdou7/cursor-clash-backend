package com.cursorclash.backend.colabedit.utils;


import java.util.TreeMap;

public interface FractionalIndexingCrdt {

    public String getDocument();
    public CrdtBlock insertBetween(double after, double before, int userId, char c);
    public void insert(double index, int userId, char c);
    public String toString();

}

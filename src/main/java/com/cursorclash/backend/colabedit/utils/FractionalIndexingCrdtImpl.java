package com.cursorclash.backend.colabedit.utils;

import java.util.TreeMap;

public class FractionalIndexingCrdtImpl implements FractionalIndexingCrdt {

    TreeMap<Double, CrdtBlock> crdt;

    public FractionalIndexingCrdtImpl() {
        crdt = new TreeMap<>();
        crdt.put(0.0, new CrdtBlock(0.0, '|', 0));
        crdt.put(1.0, new CrdtBlock(1.0, '|', 1));
    }

    @Override
    public String getDocument() {
        String doc = "";
        // skip the first and last blocks
        for (CrdtBlock block : crdt.values()) {
            if (block.index == 0.0 || block.index == 1.0) {
                continue;
            }
            doc += block.charInfo.toString();
        }
        return doc;
    }

    @Override
    public CrdtBlock insertBetween(double after, double before, int userId, char c) {
        if (after >= before || after < 0.0 || before > 1.0) {
            System.out.println("Invalid indices");
            return null;
        }
        double index = (after + before) / 2;
        double EPS = 1.0 / (Math.pow(10, 12));
        // add a value at the end of the index indicating the user to avoid conflicts
        index += userId * EPS;
        // add random jitter at the end of the index to avoid conflicts
        index += Math.random() * (EPS/100);
        CrdtBlock newBlock = new CrdtBlock(index, c, userId);
        crdt.put(index, newBlock);
        System.out.println("User " + userId + " inserted " + c + " between " + after + " and " + before);
        System.out.println("CRDT state After Insertion: ");
        System.out.println(this);

        return newBlock;
    }

    @Override
    public void insert(double index, int userId, char c) {
        // get to the block at the offset
        if (index <= 0.0 || index >= 1.0) {
            System.out.println("Invalid index");
            return;
        }
        CrdtBlock newBlock = new CrdtBlock(index, c, userId);
        crdt.put(index, newBlock);

        System.out.println("User " + userId + " inserted " + c + " at index " + index);
        System.out.println("CRDT state After Insertion: ");
        System.out.println(this);
    }

    @Override
    public String toString() {
        String str = "";
        for (CrdtBlock block : crdt.values()) {
            str += block.toString() + "\n";
        }
        return str;
    }
}

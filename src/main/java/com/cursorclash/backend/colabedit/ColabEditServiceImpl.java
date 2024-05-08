package com.cursorclash.backend.colabedit;

import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdt;
import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdtImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.TreeMap;

@Service
public class ColabEditServiceImpl implements ColabEditService {
    private LinkedHashMap<String, FractionalIndexingCrdt> crdts = new LinkedHashMap<>();

    @Override
    public boolean insertChar(char c, double index, int userId, String documentId) {
        var crdt = getCrdt(documentId);
        crdt.insert(index, userId, c);
        return true;
    }

    @Override
    public boolean insertCharBetween(char c, double after, double before, int userId, String documentId) {
        var crdt = getCrdt(documentId);
        crdt.insertBetween(after, before, userId, c);
        return true;
    }

    @Override
    public boolean deleteChar(double index, int userId, String documentId) {
        return false;
    }

    @Override
    public String getDocument(String documentId) {
        var crdt = getCrdt(documentId);
        return crdt.getDocument();
    }

    private FractionalIndexingCrdt getCrdt(String documentId) {
        if (!crdts.containsKey(documentId)) {
            System.out.println("no crdt is found for docID: " + documentId);
            var crdt = new FractionalIndexingCrdtImpl();
            crdts.put(documentId, crdt);
            System.out.println("Crdt have been created for docID: " + documentId);
            System.out.println(crdt);
            return crdt;
        }
        return crdts.get(documentId);


    }
}

package com.cursorclash.backend.colabedit;

public interface ColabEditService {
    public boolean insertChar(char c, double index, int userId, String documentId);
    public boolean insertCharBetween(char c, double after, double before, int userId, String documentId);
    public boolean deleteChar(double index, int userId, String documentId);
    public String getDocument(String documentId);
}

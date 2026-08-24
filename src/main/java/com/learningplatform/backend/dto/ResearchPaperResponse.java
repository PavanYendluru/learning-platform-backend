package com.learningplatform.backend.dto;

public class ResearchPaperResponse {
    private String title;
    private String authors;
    private String doi;
    private String journal;
    private String url;
    private String abstractText;

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
}
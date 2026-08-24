package com.learningplatform.backend.client;

import com.learningplatform.backend.dto.ResearchPaperResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CrossrefClient {

    private static final String BASE_URL = "https://api.crossref.org/works";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ResearchPaperResponse> search(String query, int rows) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("query", query)
                .queryParam("rows", rows)
                .queryParam("select", "DOI,title,author,published,container-title,URL,abstract")
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<ResearchPaperResponse> results = new ArrayList<>();

        if (response != null && response.get("message") instanceof Map<?, ?> message) {
            List<?> items = (List<?>) message.get("items");
            if (items != null) {
                for (Object item : items) {
                    Map<String, Object> work = (Map<String, Object>) item;
                    ResearchPaperResponse paper = new ResearchPaperResponse();

                    paper.setDoi((String) work.get("DOI"));
                    paper.setUrl((String) work.get("URL"));

                    if (work.get("title") instanceof List<?> titles && !titles.isEmpty()) {
                        paper.setTitle((String) titles.get(0));
                    }

                    if (work.get("container-title") instanceof List<?> journals && !journals.isEmpty()) {
                        paper.setJournal((String) journals.get(0));
                    }

                    if (work.get("abstract") != null) {
                        paper.setAbstractText(work.get("abstract").toString());
                    }

                    // Authors
                    if (work.get("author") instanceof List<?> authors) {
                        List<String> authorNames = new ArrayList<>();
                        for (Object a : authors) {
                            Map<String, Object> author = (Map<String, Object>) a;
                            String name = (author.get("given") != null ? author.get("given") + " " : "")
                                    + (author.get("family") != null ? author.get("family") : "");
                            authorNames.add(name.trim());
                        }
                        paper.setAuthors(String.join(", ", authorNames));
                    }

                    results.add(paper);
                }
            }
        }
        return results;
    }
}
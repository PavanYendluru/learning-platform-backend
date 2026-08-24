package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.client.CrossrefClient;
import com.learningplatform.backend.dto.ResearchPaperResponse;
import com.learningplatform.backend.exception.BadRequestException;
import com.learningplatform.backend.service.ResearchPaperService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResearchPaperServiceImpl implements ResearchPaperService {

    private final CrossrefClient crossrefClient;

    public ResearchPaperServiceImpl(CrossrefClient crossrefClient) {
        this.crossrefClient = crossrefClient;
    }

    @Override
    public List<ResearchPaperResponse> search(String query, int rows) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Search query is required");
        }
        if (rows < 1 || rows > 20) {
            rows = 10;
        }
        return crossrefClient.search(query.trim(), rows);
    }
}
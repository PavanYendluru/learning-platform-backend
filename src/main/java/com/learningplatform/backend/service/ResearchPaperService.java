package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.ResearchPaperResponse;
import java.util.List;

public interface ResearchPaperService {
    List<ResearchPaperResponse> search(String query, int rows);
}
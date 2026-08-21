package com.learningplatform.backend.enums;

/**
 * Supported types of learning resources.
 * We only store metadata + external links (never the actual content).
 */
public enum ResourceType {
    VIDEO,
    BOOK,
    DOCUMENTATION,
    RESEARCH_PAPER,
    ARTICLE,
    COURSE,
    GITHUB,
    PRACTICE,
    PROJECT,
    TECHNOLOGY_UPDATE
}
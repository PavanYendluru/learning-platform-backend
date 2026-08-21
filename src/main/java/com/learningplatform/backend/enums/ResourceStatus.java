package com.learningplatform.backend.enums;

/**
 * Lifecycle status of a Resource.
 * Only PUBLISHED resources are visible to normal users.
 */
public enum ResourceStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED,
    ARCHIVED
}
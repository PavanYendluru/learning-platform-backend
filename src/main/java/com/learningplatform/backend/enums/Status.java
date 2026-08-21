package com.learningplatform.backend.enums;

/**
 * Common status used by Domain (and later by Topic, Resource, etc.).
 * Using an enum prevents invalid values from being stored in the database.
 */
public enum Status {
    ACTIVE,
    INACTIVE
}
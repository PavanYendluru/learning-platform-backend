package com.learningplatform.backend.enums;

public enum TeachingStyle {
    GENERAL("Balanced technical explanation with practical clarity"),
    BEGINNER("Beginner-friendly explanation using simple analogies and no overwhelming jargon"),
    STEP_BY_STEP("Detailed step-by-step breakdown with clear stages"),
    PRACTICAL("Practical real-world code example and usage scenario"),
    INTERVIEW("Technical interview preparation with expected questions, edge cases, and best answers"),
    CODE_DEBUG("Debugging assistant: identify potential bugs, explain the root cause, and offer clean fixes"),
    SUMMARY("Concise high-level topic summary with key takeaways and bullet points");

    private final String description;

    TeachingStyle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

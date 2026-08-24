package com.learningplatform.backend.controller;

import com.learningplatform.backend.dto.ApiResponse;
import com.learningplatform.backend.dto.BookmarkResponse;
import com.learningplatform.backend.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(@PathVariable Long resourceId) {
        BookmarkResponse response = bookmarkService.addBookmark(resourceId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource bookmarked successfully", response));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(@PathVariable Long resourceId) {
        bookmarkService.removeBookmark(resourceId);
        return ResponseEntity.ok(ApiResponse.success("Bookmark removed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getMyBookmarks() {
        return ResponseEntity.ok(ApiResponse.success("Bookmarks retrieved successfully",
                bookmarkService.getMyBookmarks()));
    }
}
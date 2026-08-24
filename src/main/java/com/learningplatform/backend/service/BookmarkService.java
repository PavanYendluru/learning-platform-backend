package com.learningplatform.backend.service;

import com.learningplatform.backend.dto.BookmarkResponse;
import java.util.List;

public interface BookmarkService {
    BookmarkResponse addBookmark(Long resourceId);
    void removeBookmark(Long resourceId);
    List<BookmarkResponse> getMyBookmarks();
}
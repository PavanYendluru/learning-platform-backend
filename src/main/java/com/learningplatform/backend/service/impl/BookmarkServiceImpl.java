package com.learningplatform.backend.service.impl;

import com.learningplatform.backend.dto.BookmarkResponse;
import com.learningplatform.backend.entity.Bookmark;
import com.learningplatform.backend.entity.Resource;
import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.exception.ConflictException;
import com.learningplatform.backend.exception.ResourceNotFoundException;
import com.learningplatform.backend.repository.BookmarkRepository;
import com.learningplatform.backend.repository.ResourceRepository;
import com.learningplatform.backend.service.BookmarkService;
import com.learningplatform.backend.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ResourceRepository resourceRepository;
    private final CurrentUserService currentUserService;

    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository,
                               ResourceRepository resourceRepository,
                               CurrentUserService currentUserService) {
        this.bookmarkRepository = bookmarkRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public BookmarkResponse addBookmark(Long resourceId) {
        User user = currentUserService.getCurrentUser();
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        if (bookmarkRepository.existsByUserIdAndResourceId(user.getId(), resourceId)) {
            throw new ConflictException("Resource is already bookmarked");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setResource(resource);
        Bookmark saved = bookmarkRepository.save(bookmark);
        return mapToResponse(saved);
    }

    @Override
    public void removeBookmark(Long resourceId) {
        User user = currentUserService.getCurrentUser();
        if (!bookmarkRepository.existsByUserIdAndResourceId(user.getId(), resourceId)) {
            throw new ResourceNotFoundException(resourceId);
        }
        bookmarkRepository.deleteByUserIdAndResourceId(user.getId(), resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookmarkResponse> getMyBookmarks() {
        User user = currentUserService.getCurrentUser();
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BookmarkResponse mapToResponse(Bookmark bookmark) {
        BookmarkResponse r = new BookmarkResponse();
        r.setId(bookmark.getId());
        r.setResourceId(bookmark.getResource().getId());
        r.setResourceTitle(bookmark.getResource().getTitle());
        r.setResourceType(bookmark.getResource().getResourceType().name());
        r.setExternalUrl(bookmark.getResource().getExternalUrl());
        r.setCreatedAt(bookmark.getCreatedAt());

        if (bookmark.getResource().getTopic() != null) {
            r.setTopicId(bookmark.getResource().getTopic().getId());
            r.setTopicName(bookmark.getResource().getTopic().getName());
            if (bookmark.getResource().getTopic().getDomain() != null) {
                r.setDomainId(bookmark.getResource().getTopic().getDomain().getId());
                r.setDomainName(bookmark.getResource().getTopic().getDomain().getName());
            }
        }
        return r;
    }
}

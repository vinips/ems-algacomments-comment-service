package com.algawors.algacomments.comment.service.api.controller;

import com.algawors.algacomments.comment.service.api.client.ModerationClient;
import com.algawors.algacomments.comment.service.api.exception.ModerationClientUnprocessableEntityException;
import com.algawors.algacomments.comment.service.api.model.CommentInput;
import com.algawors.algacomments.comment.service.api.model.CommentOutput;
import com.algawors.algacomments.comment.service.api.model.ModerationInput;
import com.algawors.algacomments.comment.service.api.model.ModerationOutput;
import com.algawors.algacomments.comment.service.common.IdGenerator;
import com.algawors.algacomments.comment.service.domain.model.Comment;
import com.algawors.algacomments.comment.service.domain.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentRepository commentRepository;

    private final ModerationClient moderationClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentOutput create(@RequestBody CommentInput commentInput) {

        UUID commentId = IdGenerator.generateTimeBasedUUID();

        ModerationInput moderationInput = ModerationInput.builder()
                .commentId(commentId)
                .text(commentInput.getText())
                .build();

        ModerationOutput moderationOutput = moderationClient.approve(moderationInput);

        if(moderationOutput.isApproved()) {
            Comment comment = Comment.builder()
                    .id(commentId)
                    .text(commentInput.getText())
                    .author(commentInput.getAuthor())
                    .createdAt(OffsetDateTime.now())
                    .build();

            commentRepository.saveAndFlush(comment);

            log.info(comment.toString());
        } else {
            throw new ModerationClientUnprocessableEntityException(moderationOutput.getReason());
        }

        return CommentOutput.builder()
                .id(commentId)
                .text(commentInput.getText())
                .author(commentInput.getAuthor())
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @GetMapping("{commentId}")
    public CommentOutput findOne(@PathVariable UUID commentId){
        Comment comment = findOrFail(commentId);

        return CommentOutput.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @GetMapping
    public Page<CommentOutput> findAll(@PageableDefault Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);

        return comments.map(this::convertToOutputModel);
    }

    private Comment findOrFail(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private CommentOutput convertToOutputModel(Comment comment) {
        return CommentOutput.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

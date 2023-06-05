package com.socaly.commentVote;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.socaly.comment.Comment;
import com.socaly.email.CommentUpVoteEmail;
import com.socaly.email.EmailService;
import com.socaly.user.User;
import com.socaly.util.VoteType;
import com.socaly.comment.CommentNotFoundException;
import com.socaly.comment.CommentRepository;
import com.socaly.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentVoteService {
    private final CommentVoteRepository commentVoteRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final EmailService emailService;

    @Transactional
    public void vote(CommentVoteDto commentVoteDto) {
        Comment comment = commentRepository.findById(commentVoteDto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException(commentVoteDto.getCommentId().toString()));
        Optional<CommentVote> voteByCommentAndUser = commentVoteRepository.findTopByCommentAndUserOrderByIdDesc(comment, authService.getCurrentUser());

        if (voteByCommentAndUser.isPresent() && voteByCommentAndUser.get().getVoteType().equals(commentVoteDto.getVoteType())) {
            if (VoteType.UPVOTE.equals(commentVoteDto.getVoteType())) {
                comment.setVoteCount(comment.getVoteCount() - 1);
            } else {
                comment.setVoteCount(comment.getVoteCount() + 1);
            }
            commentVoteRepository.deleteById(voteByCommentAndUser.get().getId());

        } else if (voteByCommentAndUser.isPresent()) {
            if (VoteType.UPVOTE.equals(commentVoteDto.getVoteType())) {
                comment.setVoteCount(comment.getVoteCount() + 2);
            } else {
                comment.setVoteCount(comment.getVoteCount() - 2);
            }
            commentVoteRepository.deleteById(voteByCommentAndUser.get().getId());
            commentVoteRepository.save(mapToCommentVote(commentVoteDto, comment));

        } else {
            if (VoteType.UPVOTE.equals(commentVoteDto.getVoteType())) {
                comment.setVoteCount(comment.getVoteCount() + 1);
            } else {
                comment.setVoteCount(comment.getVoteCount() - 1);
            }
            commentVoteRepository.save(mapToCommentVote(commentVoteDto, comment));
        }
    }

    private void sendCommentUpVoteEmail(Comment comment) {
        User currentUser = authService.getCurrentUser();

        int postPoints = comment.getPost().getVoteCount();
        String postPointsText;

        int commentCount = commentRepository.findByPost(comment.getPost()).size();
        String commentCountText;

        int commentPoints = comment.getVoteCount();
        String commentPointsText;

        int commentReplyCount = commentRepository.findByParentCommentId(comment.getId()).size();
        String commentReplyCountText;

        if (postPoints == 1 || postPoints == -1) {
            postPointsText = "1 point";
        } else {
            postPointsText = comment.getPost().getVoteCount() + " points";
        }

        if (commentCount == 1) {
            commentCountText = "1 comment";
        } else {
            commentCountText = commentCount + " comments";
        }

        if (commentPoints == 1 || commentPoints == -1) {
            commentPointsText = "1 point";
        } else {
            commentPointsText = commentPoints + " points";
        }

        if (commentReplyCount == 1) {
            commentReplyCountText = "1 comment";
        } else {
            commentReplyCountText = commentReplyCount + " comments";
        }

        emailService.sendCommentUpVoteEmail(new CommentUpVoteEmail(
                currentUser.getUsername() + " upvoted your comment on " + comment.getPost().getPostName()
                        + " in s\\" + comment.getPost().getCommunity().getName(),
                comment.getUser().getEmail(),
                comment.getUser().getUsername(),
                comment.getUser().getProfileImage().getImageUrl(),
                comment.getPost().getCommunity().getName(),
                comment.getPost().getUser().getUsername(),
                TimeAgo.using(comment.getPost().getCreatedDate().toEpochMilli()),
                comment.getPost().getPostName(),
                postPointsText,
                commentCountText,
                TimeAgo.using(comment.getCreationDate().toEpochMilli()),
                comment.getText(),
                commentPointsText,
                commentReplyCountText,
                currentUser.getUsername(),
                currentUser.getProfileImage().getImageUrl()
        ));
    }

    private CommentVote mapToCommentVote(CommentVoteDto commentVoteDto, Comment comment) {
        return CommentVote.builder()
                .voteType(commentVoteDto.getVoteType())
                .comment(comment)
                .user(authService.getCurrentUser())
                .build();
    }
}
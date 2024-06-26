package com.socaly.comment;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.socaly.email.CommentReplyEmail;
import com.socaly.email.PostCommentEmail;
import com.socaly.post.Post;
import com.socaly.user.User;
import com.socaly.post.PostNotFoundException;
import com.socaly.post.PostRepository;
import com.socaly.user.UserRepository;
import com.socaly.auth.AuthService;
import com.socaly.email.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final EmailService emailService;

    void create(final CommentRequest commentRequest) {
        final Post post = findPostById(commentRequest.getPostId());
        final User user = authService.getCurrentUser();
        final Comment comment = commentMapper.mapToComment(commentRequest, post, user);
        commentRepository.save(comment);

        if (shouldSendPostCommentEmail(post, comment)) {
            sendPostCommentEmail(post, comment);
        } else if (comment.getParentCommentId() != null) {
            Comment parentComment = findCommentById(comment.getParentCommentId());

            if (shouldSendCommentReplyEmail(comment, parentComment)) {
                sendCommentReplyEmail(post, parentComment, comment);
            }
        }
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new PostNotFoundException(postId.toString())
        );
    }

    private boolean shouldSendPostCommentEmail(final Post post, final Comment comment) {
        return !post.getUser().getUsername().equals(comment.getUser().getUsername()) &&
                comment.getParentCommentId() == null &&
                post.getUser().getSettings().getPostCommentEmails();
    }

    private Comment findCommentById(final Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                    () -> new CommentNotFoundException(commentId.toString())
            );
    }

    private boolean shouldSendCommentReplyEmail(final Comment comment, final Comment parentComment) {
        return !comment.getUser().getUsername().equals(parentComment.getUser().getUsername()) &&
                parentComment.getUser().getSettings().getCommentReplyEmails();
    }

    private void sendPostCommentEmail(final Post post, final Comment comment) {
        emailService.sendPostCommentEmail(new PostCommentEmail(
                comment.getUser().getUsername() + " commented on your post " + post.getTitle() + " in s\\"
                        + post.getCommunity().getName(),
                post.getUser().getEmail(),
                post.getUser().getUsername(),
                post.getUser().getProfileImage().getImageUrl(),
                post.getCommunity().getName(),
                String.valueOf(post.getId()),
                TimeAgo.using(post.getCreationDate().toEpochMilli()),
                post.getTitle(),
                Post.getPostPointsText(post.getPoints()),
                Post.getPostCommentCountText(commentRepository.findByPost(comment.getPost()).size()),
                String.valueOf(comment.getId()),
                comment.getUser().getUsername(),
                comment.getUser().getProfileImage().getImageUrl(),
                comment.getText()
        ));
    }

    private void sendCommentReplyEmail(final Post post, final Comment comment, final Comment reply) {
        emailService.sendCommentReplyEmail(new CommentReplyEmail(
                reply.getUser().getUsername() + " replied to your comment on post " + post.getTitle()
                        + "in s\\" + post.getCommunity().getName(),
                comment.getUser().getEmail(),
                comment.getUser().getUsername(),
                comment.getUser().getProfileImage().getImageUrl(),
                post.getCommunity().getName(),
                String.valueOf(post.getId()),
                post.getUser().getUsername(),
                TimeAgo.using(post.getCreationDate().toEpochMilli()),
                post.getTitle(),
                Post.getPostPointsText(comment.getPost().getPoints()),
                Post.getPostCommentCountText(commentRepository.findByPost(comment.getPost()).size()),
                TimeAgo.using(comment.getCreationDate().toEpochMilli()),
                comment.getText(),
                Comment.getCommentPointsText(comment.getPoints()),
                Comment.getCommentReplyCountText(commentRepository.findByParentCommentId(comment.getId()).size()),
                String.valueOf(reply.getId()),
                reply.getUser().getUsername(),
                reply.getUser().getProfileImage().getImageUrl(),
                reply.getText()
        ));
    }

    void edit(final Long commentId, final String text) {
        final Comment commentToEdit = findCommentById(commentId);
        final User user = authService.getCurrentUser();

        if (Objects.equals(commentToEdit.getUser().getId(), user.getId())) {
            commentToEdit.setText(text);
            commentToEdit.setEditDate(Instant.now());
            commentRepository.save(commentToEdit);
        }
    }

    CommentResponse get(final Long commentId) {
        final Optional<Comment> comment = commentRepository.findById(commentId);

        if (comment.isPresent()) {
            return commentMapper.mapToCommentResponse(comment.get());
        } else {
            throw new CommentNotFoundException(commentId.toString());
        }
    }

    List<CommentResponse> getAllByPost(final Long postId) {
        final Post post = findPostById(postId);

        return commentRepository.findByPostAndParentCommentIdIsNull(post)
                .stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    List<CommentResponse> getSubComments(final Long commentId) {
        return commentRepository.findByParentCommentId(commentId)
                .stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    List<CommentResponse> getAllByUser(final String username) {
        final User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );

        return commentRepository.findByUser(user)
                .stream()
                .map(commentMapper::mapToCommentResponse)
                .collect(Collectors.toList());
    }
}

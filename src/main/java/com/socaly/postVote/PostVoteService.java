package com.socaly.postVote;

import com.socaly.auth.AuthService;
import com.socaly.post.Post;
import com.socaly.util.VoteType;
import com.socaly.post.PostNotFoundException;
import com.socaly.post.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PostVoteService {
    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(PostVoteDto postVoteDto) {
        Post post = postRepository.findById(postVoteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(postVoteDto.getPostId().toString()));
        Optional<PostVote> voteByPostAndUser = postVoteRepository.findTopByPostAndUserOrderByIdDesc(post, authService.getCurrentUser());

        if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(postVoteDto.getVoteType())) {
            if (VoteType.UPVOTE.equals(postVoteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() - 1);
            } else {
                post.setVoteCount(post.getVoteCount() + 1);
            }

            postVoteRepository.deleteById(voteByPostAndUser.get().getId());

        } else if (voteByPostAndUser.isPresent()) {
            if (VoteType.UPVOTE.equals(postVoteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() + 2);
            } else {
                post.setVoteCount(post.getVoteCount() - 2);
            }

            postVoteRepository.deleteById(voteByPostAndUser.get().getId());
            postVoteRepository.save(mapToVote(postVoteDto, post));

        } else {
            if (VoteType.UPVOTE.equals(postVoteDto.getVoteType())) {
                post.setVoteCount(post.getVoteCount() + 1);
            } else {
                post.setVoteCount(post.getVoteCount() - 1);
            }

            postVoteRepository.save(mapToVote(postVoteDto, post));
        }
    }

    private PostVote mapToVote(PostVoteDto postVoteDto, Post post) {
        return PostVote.builder()
                .voteType(postVoteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}

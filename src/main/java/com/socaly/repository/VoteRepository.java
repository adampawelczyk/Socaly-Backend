package com.socaly.repository;

import com.socaly.entity.Post;
import com.socaly.entity.User;
import com.socaly.entity.Vote;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByIdDesc(Post post, User currentUser);
    void deleteById(@NotNull Long voteId);
}

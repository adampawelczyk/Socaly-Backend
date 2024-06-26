package com.socaly.postVote;

import com.socaly.util.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostVoteDto {
    private VoteType voteType;
    private Long postId;
}

package com.socaly.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentReplyEmail {
    private String subject;
    private String recipientEmail;
    private String recipientUsername;
    private String recipientProfileImage;
    private String replyUsername;
    private String replyUserProfileImage;
    private String postUsername;
    private String postTitle;
    private String postTimestamp;
    private String communityName;
    private String commentText;
    private String commentTimestamp;
    private String replyText;
}

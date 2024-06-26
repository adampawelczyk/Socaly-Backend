package com.socaly.userSettings;

import com.socaly.auth.AuthService;
import com.socaly.user.User;
import com.socaly.util.Sorting;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class UserSettingsService {
    private final AuthService authService;
    private final UserSettingsRepository userSettingsRepository;
    private final UserSettingsMapper userSettingsMapper;

    UserSettingsResponse get() {
        final User currentUser = authService.getCurrentUser();
        final Optional<UserSettings> userSettings = userSettingsRepository.findById(currentUser.getSettings().getId());

        if (userSettings.isPresent()) {
            return userSettingsMapper.mapToUserSettingsResponse(userSettings.get());
        } else {
            throw new UserSettingsNotFoundException(currentUser.getUsername());
        }
    }

    void updateCommunityContentSort(final Sorting sorting) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setCommunityContentSort(sorting);
        userSettingsRepository.save(userSettings);
    }

    void updateOpenPostsInNewTab(final boolean openPostsInNewTab) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setOpenPostsInNewTab(openPostsInNewTab);
        userSettingsRepository.save(userSettings);
    }

    void updatePostCommentEmails(final boolean postCommentEmails) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setPostCommentEmails(postCommentEmails);
        userSettingsRepository.save(userSettings);
    }

    void updateCommentReplyEmails(final boolean commentReplyEmails) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setCommentReplyEmails(commentReplyEmails);
        userSettingsRepository.save(userSettings);
    }

    void updatePostUpVoteEmails(final boolean postUpVoteEmails) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setPostUpVoteEmails(postUpVoteEmails);
        userSettingsRepository.save(userSettings);
    }

    void updateCommentUpVoteEmails(final boolean commentUpVoteEmails) {
        final User currentUser = authService.getCurrentUser();

        UserSettings userSettings = currentUser.getSettings();
        userSettings.setCommentUpVoteEmails(commentUpVoteEmails);
        userSettingsRepository.save(userSettings);
    }
}

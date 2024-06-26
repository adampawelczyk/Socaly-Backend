package com.socaly.community;

import com.socaly.user.User;
import com.socaly.user.UserRepository;
import com.socaly.auth.AuthService;
import com.socaly.userCommunitySettings.UserCommunitySettings;
import com.socaly.userCommunitySettings.UserCommunitySettingsNotFoundException;
import com.socaly.userCommunitySettings.UserCommunitySettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserCommunitySettingsRepository userCommunitySettingsRepository;
    private final CommunityMapper communityMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional
    CommunityResponse create(final CommunityRequest communityRequest) {
        final User currentUser = authService.getCurrentUser();
        final Community savedCommunity = communityRepository.save(communityMapper.mapToCommunity(communityRequest, currentUser));
        join(communityRequest.getName());

        return communityMapper.mapToCommunityResponse(savedCommunity);
    }

    @Transactional(readOnly = true)
    List<CommunityResponse> getAll() {
        return communityRepository
                .findAll()
                .stream()
                .map(communityMapper::mapToCommunityResponse)
                .collect(Collectors.toList());
    }

    CommunityResponse findCommunityByName(final String communityName) {
        final Community community = communityRepository.findByName(communityName).orElseThrow(
                () -> new CommunityNotFoundException(communityName));

        return communityMapper.mapToCommunityResponse(community);
    }

    void join(final String communityName) {
        final Community community = communityRepository.findByName(communityName).orElseThrow(
                () -> new CommunityNotFoundException(communityName)
        );

        addCurrentUserToCommunity(community);
        saveCurrentUserCommunitySettings(community);
    }

    private void addCurrentUserToCommunity(final Community community) {
        final User currentUser = authService.getCurrentUser();

        community.getUsers().add(currentUser);
        communityRepository.save(community);
    }

    private void saveCurrentUserCommunitySettings(final Community community) {
        final User currentUser = authService.getCurrentUser();

        UserCommunitySettings userCommunitySettings = new UserCommunitySettings();
        userCommunitySettings.setCommunityId(community.getId());
        userCommunitySettingsRepository.save(userCommunitySettings);

        currentUser.getUserCommunitySettings().add(userCommunitySettings);
        userRepository.save(currentUser);
    }

    void leave(final String communityName) {
        final Community community = communityRepository.findByName(communityName).orElseThrow(
                () -> new CommunityNotFoundException(communityName)
        );

        removeCurrentUserFromCommunity(community);
        removeCurrentUserCommunitySettings(community);
    }

    private void removeCurrentUserFromCommunity(final Community community) {
        final User currentUser = authService.getCurrentUser();
        
        community.getUsers().remove(currentUser);
        communityRepository.save(community);
    }

    private void removeCurrentUserCommunitySettings(final Community community) {
        final User currentUser = authService.getCurrentUser();
        
        List<UserCommunitySettings> userCommunitySettingsList = currentUser.getUserCommunitySettings();
        UserCommunitySettings userCommunitySettings = userCommunitySettingsList
                .stream()
                .filter(settings -> Objects.equals(settings.getCommunityId(), community.getId()))
                .findFirst()
                .orElseThrow(() -> new UserCommunitySettingsNotFoundException(currentUser.getUsername()));

        currentUser.getUserCommunitySettings().remove(userCommunitySettings);
        userRepository.save(currentUser);

        userCommunitySettingsRepository.delete(userCommunitySettings);
    }

    List<CommunityResponse> getAllByUser(final String username) {
        final User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("No user found with name - " + username)
        );

        return communityRepository
                .findAll()
                .stream()
                .filter(community -> community.getUsers().contains(user))
                .map(communityMapper::mapToCommunityResponse)
                .collect(Collectors.toList());
    }
}

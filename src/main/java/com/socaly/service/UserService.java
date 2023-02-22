package com.socaly.service;

import com.socaly.dto.UserDto;
import com.socaly.mapper.UserMapper;
import com.socaly.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .stream().map(userMapper::mapToDto)
                .findFirst()
                .orElseThrow(
                    () -> new UsernameNotFoundException(username)
                );
    }
}

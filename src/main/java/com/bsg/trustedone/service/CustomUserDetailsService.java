package com.bsg.trustedone.service;

import com.bsg.trustedone.mapper.UserMapper;
import com.bsg.trustedone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = this.userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        return userMapper.toUserDetailDto(user);
    }
}

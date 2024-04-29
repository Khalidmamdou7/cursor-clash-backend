package com.cursorclash.backend.model;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String MSG_USER_NOT_FOUND="There is no user with %s email";
    private final Repo repo;
    @Override
    public UserDetails loadUserByUsername(String Email) throws UsernameNotFoundException {
        return repo.findByEmail(Email).orElseThrow(()->new UsernameNotFoundException(String.format(MSG_USER_NOT_FOUND,Email)));
    }
}

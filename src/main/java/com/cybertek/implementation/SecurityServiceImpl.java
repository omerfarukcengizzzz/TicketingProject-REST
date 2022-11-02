package com.cybertek.implementation;

import com.cybertek.entity.User;
import com.cybertek.entity.common.UserPrincipal;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);

        if (user == null) throw new UsernameNotFoundException("This user does not exists!");

        return new UserPrincipal(user);
    }
}

package com.taveeshsharma.httprequesthandler.manager;

import com.taveeshsharma.httprequesthandler.dto.documents.User;
import com.taveeshsharma.httprequesthandler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserManager implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        if(user.isPresent())
            return user.get();
        else
            throw new UsernameNotFoundException("User not found");
    }

    public Optional<User> findExistingUser(User user){
        return userRepository.findByUserNameAndRoles(user.getUsername(), user.getRoles());
    }

    public void save(User user){
        userRepository.save(user);
    }

}

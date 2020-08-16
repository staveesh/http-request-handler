package com.taveeshsharma.httprequesthandler.manager;

import com.taveeshsharma.httprequesthandler.dto.documents.User;
import com.taveeshsharma.httprequesthandler.dto.documents.UserRole;
import com.taveeshsharma.httprequesthandler.repository.UserRepository;
import com.taveeshsharma.httprequesthandler.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserManager implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        if(user.isPresent()) {
            List<GrantedAuthority> authorities = getUserAuthority(user.get().getRoles());
            return buildUserForAuthentication(user.get(), authorities);
        }
        else
            throw new UsernameNotFoundException("User not found");
    }

    private List<GrantedAuthority> getUserAuthority(Set<UserRole> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> roles.add(new SimpleGrantedAuthority(role.getRole())));
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities){
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(), user.getPassword(), authorities
        );
    }

    public void save(User user){
        for(UserRole role : user.getRoles()){
            UserRole savedRole = roleRepository.findByRole(role.getRole());
            role.setId(savedRole.getId());
        }
        userRepository.save(user);
    }

}

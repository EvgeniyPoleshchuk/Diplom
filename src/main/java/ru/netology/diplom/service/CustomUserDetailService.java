package ru.netology.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.diplom.exceptions.ErrorInputDataException;
import ru.netology.diplom.model.UserData;
import ru.netology.diplom.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository dao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserData userData = dao.findByEmail(username);
        if(userData == null){
            throw new ErrorInputDataException("User service Unauthorized");
        }
        return User.builder()
                .username(userData.getEmail())
                .password(userData.getPassword())
                .build();
    }
}

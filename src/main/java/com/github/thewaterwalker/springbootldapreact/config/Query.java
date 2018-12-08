package com.github.thewaterwalker.springbootldapreact.config;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.github.thewaterwalker.springbootldapreact.users.User;
import com.github.thewaterwalker.springbootldapreact.users.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Query implements GraphQLQueryResolver {

    private UserRepository userRepository;

    public Query(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(Long id) {
        return this.userRepository.findById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}

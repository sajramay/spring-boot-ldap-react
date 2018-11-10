package com.ramays.springbootldapreact.users;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UsersDataFetcher implements DataFetcher<Collection<User>> {

    private UserRepository userRepository;

    public UsersDataFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<User> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return userRepository.findAll();
    }
}

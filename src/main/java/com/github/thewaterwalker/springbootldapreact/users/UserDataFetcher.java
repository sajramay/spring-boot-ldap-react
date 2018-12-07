
package com.github.thewaterwalker.springbootldapreact.users;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserDataFetcher implements DataFetcher<Optional<User>> {

    private UserRepository userRepository;

    public UserDataFetcher(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> get(DataFetchingEnvironment dataFetchingEnvironment) {
        Long id = dataFetchingEnvironment.getArgument("id");
        return userRepository.findById(id);
    }
}

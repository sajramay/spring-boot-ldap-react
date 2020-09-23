/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.thewaterwalker.springbootldapreact.graphql;

import com.github.thewaterwalker.springbootldapreact.entity.User;
import com.github.thewaterwalker.springbootldapreact.entity.UserRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QueryResolver implements GraphQLQueryResolver  {

    private final UserRepository userRepository;

    public Optional<User> getUser(Long id, DataFetchingEnvironment dfe) {

        // Pre-load the LineItems that we will need in the UserResolver
        DataLoader<Long, ?> dataLoader = dfe.getDataLoader(GraphQLConfig.LINE_ITEM_DATA_LOADER);
        Optional<User> userOpt = this.userRepository.findById(id);
        userOpt.ifPresent( u -> {
            dataLoader.load(u.getId());
            dataLoader.dispatchAndJoin();
        });
        return userOpt;
    }

    public Collection<User> users(Collection<Long> ids, DataFetchingEnvironment dfe) {
        // Pre-load the LineItems that we will need in the UserResolver
        DataLoader<Long, ?> dataLoader = dfe.getDataLoader(GraphQLConfig.LINE_ITEM_DATA_LOADER);
        List<User> allusers = this.userRepository.findAllById(ids);
        final List<Long> userIds = allusers.stream().map(User::getId).collect(Collectors.toList());
        dataLoader.loadMany(userIds);
        dataLoader.dispatchAndJoin();
        return allusers;
    }

    public Collection<User> allUsers(DataFetchingEnvironment dfe) {
        // Pre-load the LineItems that we will need in the UserResolver
        DataLoader<Long, ?> dataLoader = dfe.getDataLoader(GraphQLConfig.LINE_ITEM_DATA_LOADER);
        List<User> allusers = this.userRepository.findAll();
        final List<Long> userIds = allusers.stream().map(User::getId).collect(Collectors.toList());
        dataLoader.loadMany(userIds);
        dataLoader.dispatchAndJoin();
        return allusers;
    }

    public String description() {
        return "hello";
    }
}

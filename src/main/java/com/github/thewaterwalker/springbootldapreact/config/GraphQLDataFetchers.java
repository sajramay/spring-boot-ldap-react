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

package com.github.thewaterwalker.springbootldapreact.config;

import com.github.thewaterwalker.springbootldapreact.users.User;
import com.github.thewaterwalker.springbootldapreact.users.UserRepository;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GraphQLDataFetchers {

    private final UserRepository userRepository;
    private final GraphQLDataLoaders graphQLDataLoaders;

    public DataFetcher<Map<String, Object>> userFetcher() {
        return dataFetchingEnvironment -> {
            Long id = dataFetchingEnvironment.getArgument("id");
            Optional<User> userOpt = this.userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return Collections.emptyMap();
            }

            HashMap<String, Object> map = new HashMap<>();
            User user = userOpt.get();
            map.put("id", user.getId());
            map.put("firstName", user.getFirstName());
            map.put("lastName", user.getLastName());
            map.put("description", "some description");

            return map;
        };
    }

    public DataFetcher<Collection<User>> allUsersFetcher() {
        return dataFetchingEnvironment -> this.userRepository.findAll();
    }

    public DataFetcher<Collection<User>> usersFetcher() {
        return dataFetchingEnvironment -> {
            DataLoader<Long, User> dl = DataLoader.newDataLoader(this.graphQLDataLoaders.userBatchLoader);
            List<Long> ids = dataFetchingEnvironment.getArgument("ids");
            dl.loadMany(ids);
            return dl.dispatchAndJoin();
        };
    }
}

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Secured({WebSecurityConfig.ROLE_APP_READONLY, WebSecurityConfig.ROLE_APP_USER})
public class GraphQLDataFetchers {

    private final UserRepository userRepository;

    public DataFetcher<Map<String, Object>> userFetcher() {
        return dataFetchingEnvironment -> {
            Long id = dataFetchingEnvironment.getArgument("id");
            Optional<User> userOpt = this.userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return Collections.emptyMap();
            }
            return mapUser(userOpt.get());
        };
    }

    public DataFetcher<Collection<Map<String, Object>>> allUsersFetcher() {
        return dataFetchingEnvironment -> {
            List<User> all = this.userRepository.findAll();
            return all.stream().map(this::mapUser).collect(Collectors.toList());
        };
    }

    public DataFetcher<Collection<Map<String, Object>>> usersFetcher() {
        return dataFetchingEnvironment -> {
            Collection<Long> ids = dataFetchingEnvironment.getArgument("ids");
            List<User> all = this.userRepository.findAllById(ids);
            return all.stream().map(this::mapUser).collect(Collectors.toList());
        };
    }

    private Map<String, Object> mapUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        // this field is not part of the JPA entity but is part of the GraphQL entity
        map.put("description", "some description");
        return map;
    }
}

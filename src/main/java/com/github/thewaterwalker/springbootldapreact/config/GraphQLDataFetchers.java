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
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class GraphQLDataFetchers {

    private final UserRepository userRepository;

    public DataFetcher<User> userFetcher() {
        return dataFetchingEnvironment -> {
            Long id = dataFetchingEnvironment.getArgument("id");
            return this.userRepository.findById(id).orElse(null);
        };
    }

    public DataFetcher<Collection<User>> usersFetcher() {
        return dataFetchingEnvironment -> this.userRepository.findAll();
    }
}

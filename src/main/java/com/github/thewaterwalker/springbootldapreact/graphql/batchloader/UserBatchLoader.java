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

package com.github.thewaterwalker.springbootldapreact.graphql.batchloader;

import com.github.thewaterwalker.springbootldapreact.entity.User;
import com.github.thewaterwalker.springbootldapreact.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@RequiredArgsConstructor
@Component
public class UserBatchLoader implements BatchLoader<Long, User> {

    private final UserRepository userRepository;

    @Override
    public CompletionStage<List<User>> load(List<Long> set) {
        return CompletableFuture.supplyAsync(() -> this.userRepository.findAllById(set));
    }
}

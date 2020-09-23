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

import com.github.thewaterwalker.springbootldapreact.entity.LineItem;
import com.github.thewaterwalker.springbootldapreact.entity.LineItemRepository;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LineItemBatchLoader implements MappedBatchLoader<Long, List<LineItem>> {

    private final LineItemRepository lineItemRepository;

    @Override
    public CompletionStage<Map<Long, List<LineItem>>> load(Set<Long> userIds) {
        return CompletableFuture.supplyAsync(() -> {
            // Preload the set of line items per user and store them against the user-id
            final Collection<LineItem> lineitems = this.lineItemRepository.findByUserIdIn(userIds);
            return lineitems.stream().collect(Collectors.groupingBy(LineItem::getUserId));
        });
    }
}

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

import com.github.thewaterwalker.springbootldapreact.entity.LineItem;
import com.github.thewaterwalker.springbootldapreact.entity.LineItemRepository;
import com.github.thewaterwalker.springbootldapreact.entity.User;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class UserResolver implements GraphQLResolver<User> {

    private final LineItemRepository lineItemRepository;

    public BigDecimal totalSpent(User u, DataFetchingEnvironment dfe) {

        // User the DataLoader which will have preloaded the LineItems from QueryResolver, rather than
        // read the LineItems per invocation to each method in this resolver class
        DataLoader<Long, List<LineItem>> dataLoader = dfe.getDataLoader(GraphQLConfig.LINE_ITEM_DATA_LOADER);
        Optional<CompletableFuture<List<LineItem>>> ifCompleted = dataLoader.getIfCompleted(u.getId());
        if (ifCompleted.isPresent()) {
            try {
                List<LineItem> lineItems = ifCompleted.get().get();
                if (lineItems != null) {
                    return lineItems.stream().map(LineItem::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                } else {
                    return BigDecimal.ZERO;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Otherwise just load the line items again
        Collection<LineItem> lineItems = this.lineItemRepository.findByUserId(u.getId());
        return lineItems.stream().map(LineItem::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal maximumSpent(User u, DataFetchingEnvironment dfe) {

        // User the DataLoader which will have preloaded the LineItems from QueryResolver, rather than
        // read the LineItems per invocation to each method in this resolver class
        DataLoader<Long, List<LineItem>> dataLoader = dfe.getDataLoader(GraphQLConfig.LINE_ITEM_DATA_LOADER);
        Optional<CompletableFuture<List<LineItem>>> ifCompleted = dataLoader.getIfCompleted(u.getId());
        if (ifCompleted.isPresent()) {
            try {

                List<LineItem> lineItems = ifCompleted.get().get();
                if (lineItems != null) {
                    return lineItems.stream().map(LineItem::getAmount).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                } else {
                    return BigDecimal.ZERO;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Otherwise just load the items again
        Collection<LineItem> lineItems = this.lineItemRepository.findByUserId(u.getId());
        return lineItems.stream().map(LineItem::getAmount).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }
}

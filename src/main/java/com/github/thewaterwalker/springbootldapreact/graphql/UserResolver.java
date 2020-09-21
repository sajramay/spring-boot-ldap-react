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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class UserResolver implements GraphQLResolver<User> {

    private final LineItemRepository lineItemRepository;

    public BigDecimal totalSpent(User u) {
        Collection<LineItem> lineItems = this.lineItemRepository.findByUserId(u.getId());
        return lineItems.stream().map(LineItem::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public BigDecimal maximumSpent(User u) {
        Collection<LineItem> lineItems = this.lineItemRepository.findByUserId(u.getId());
        return lineItems.stream().map(LineItem::getAmount).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }
}

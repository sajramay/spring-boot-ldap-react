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

import com.github.thewaterwalker.springbootldapreact.graphql.batchloader.LineItemBatchLoader;
import com.github.thewaterwalker.springbootldapreact.graphql.batchloader.UserBatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class GraphQLConfig {

    public final static String USER_DATA_LOADER = "USER_DATA_LOADER";
    public final static String LINE_ITEM_DATA_LOADER = "LINE_ITEM_DATA_LOADER";

    @Bean
    @RequestScope
    public DataLoaderRegistry dataLoaderRegistry(UserBatchLoader userBatchLoader, LineItemBatchLoader lineItemBatchLoader) {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
        dataLoaderRegistry.register(USER_DATA_LOADER, DataLoader.newDataLoader(userBatchLoader));
        dataLoaderRegistry.register(LINE_ITEM_DATA_LOADER, DataLoader.newMappedDataLoader(lineItemBatchLoader));
        return dataLoaderRegistry;
    }
}

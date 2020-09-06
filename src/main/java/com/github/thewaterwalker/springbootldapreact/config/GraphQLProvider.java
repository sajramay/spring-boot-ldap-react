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

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GraphQLProvider {

    private final GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    public GraphQLSchema graphQLSchema() {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(loadSchema());
        RuntimeWiring runtimeWiring = buildWiring();

        return new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                    .dataFetcher("user", graphQLDataFetchers.userFetcher())
                    .dataFetcher("users", graphQLDataFetchers.usersFetcher())
                )
                .build();
    }

    private Reader loadSchema() {
        final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("graphql-schema.graphqls");
        Objects.requireNonNull(resourceAsStream);
        return new InputStreamReader(resourceAsStream);
    }
}

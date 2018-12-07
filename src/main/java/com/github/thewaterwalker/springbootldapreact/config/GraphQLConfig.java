package com.github.thewaterwalker.springbootldapreact.config;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.io.Reader;

@Configuration
public class GraphQLConfig {

    @Autowired
    private DataFetcher userDataFetcher;

    @Autowired
    private DataFetcher usersDataFetcher;

    @Bean
    public GraphQL loadGraphQLSchema() {
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(loadSchema());
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private Reader loadSchema() {
        return new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("graphql-schema.sdl"));
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("users", usersDataFetcher)
                        .dataFetcher("user", userDataFetcher)
                )
                .build();
    }
}


package com.ramays.springbootldapreact.users;

import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/graphql")
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private UserRepository userRepository;

    private GraphQL graphQL;

    public UserController(UserRepository userRepository, GraphQL graphQL) {
        this.userRepository = userRepository;
        this.graphQL = graphQL;
    }

    @PostMapping
    public ResponseEntity userQuery(@RequestBody String query) {
        return ResponseEntity.ok().body(graphQL.execute(query));
    }
}

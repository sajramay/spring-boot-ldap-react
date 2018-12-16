package com.github.thewaterwalker.springbootldapreact.users;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name="USER")
public @Data class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    String firstName;

    @Column
    String lastName;
}

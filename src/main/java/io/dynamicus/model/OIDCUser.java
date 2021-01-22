package io.dynamicus.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class OIDCUser {
    @JsonAlias(value = "sub")
    public String userId;

    @JsonAlias(value = "name")
    public String name;

    @JsonAlias(value = "preferred_username")
    public String preferedUsername;

    @JsonAlias(value = "email")
    public String email;
}

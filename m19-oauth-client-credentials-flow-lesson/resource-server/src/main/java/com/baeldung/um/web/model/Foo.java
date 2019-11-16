package com.baeldung.um.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Foo {
    private long id;
    private String name;
}
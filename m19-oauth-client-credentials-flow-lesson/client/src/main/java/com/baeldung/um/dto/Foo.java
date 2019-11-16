package com.baeldung.um.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Foo {

    private long id;
    private String name;

    @Override
    public String toString() {
        return "Foo [id=" + id + ", name=" + name + "]";
    }

}

package com.baeldung.lsso.web.dto;

import java.time.LocalDate;

public record TaskDto(

    Long id,

    String name,

    String description,

    LocalDate dateCreated,

    LocalDate dueDate) {

}
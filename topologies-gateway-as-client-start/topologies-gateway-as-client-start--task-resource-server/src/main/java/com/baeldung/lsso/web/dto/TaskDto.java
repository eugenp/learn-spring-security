package com.baeldung.lsso.web.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record TaskDto (

    Long id,

    String name,

    String description,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate dateCreated,

    @DateTimeFormat(iso = ISO.DATE)
    LocalDate dueDate) {
}
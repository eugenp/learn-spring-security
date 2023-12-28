package com.baeldung.lsso.web.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public record ProjectDto(

    Long id,

    String name,

    @DateTimeFormat(iso = ISO.DATE) LocalDate dateCreated) {

}

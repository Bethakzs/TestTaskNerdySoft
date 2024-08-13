package org.example.testtasknerdysoft.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    @Pattern(regexp = "^[A-Z][a-zA-Z\\s]*$", message = "Title must start with an uppercase letter")
    private String title;

    @NotBlank(message = "Author is required")
    @Pattern(regexp = "^[A-Z][a-z]+ [A-Z][a-z]+$", message = "Author name must be in the format 'Firstname Lastname'")
    private String author;

    private int amount;
}

package ru.netology.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RenameFile {
    @JsonProperty
    private String filename;

}

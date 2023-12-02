package ru.netology.diplom.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name ="file_data",schema = "public")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("filename")
    @Column(nullable = false)
    private String fileName;

    @Column
    private long size;
    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "userdata_email")
    private UserData userData;

    @Column(nullable = false)
    private byte [] fileData;

    public FileData() {

    }
}

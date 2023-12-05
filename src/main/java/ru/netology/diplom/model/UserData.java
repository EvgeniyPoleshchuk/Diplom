package ru.netology.diplom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_data",schema = "public")
public class UserData  {
    @Id
    private long id;
    @Column
    private String email;
    @Column
    private String password;



}

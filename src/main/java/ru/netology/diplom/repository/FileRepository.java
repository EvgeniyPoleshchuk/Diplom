package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.diplom.model.FileData;
import ru.netology.diplom.model.UserData;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileData,Long> {
    List<FileData>findFileDataByUserDataEmail(String userName);
    FileData findByFileNameAndUserDataEmail(String fileName,String email);
}

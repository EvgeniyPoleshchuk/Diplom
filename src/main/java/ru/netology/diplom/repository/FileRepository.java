package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplom.model.FileData;

import java.util.List;
@Repository
public interface FileRepository extends JpaRepository<FileData,Long> {
    List<FileData>findFileDataByUserDataEmail(String userName);
    FileData findByFileNameAndUserDataEmail(String fileName,String email);
}

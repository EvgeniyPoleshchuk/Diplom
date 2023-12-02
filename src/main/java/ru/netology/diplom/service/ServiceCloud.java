package ru.netology.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.Token.JWTUtil;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.exceptions.ErrorInputDataException;
import ru.netology.diplom.model.FileData;
import ru.netology.diplom.model.RenameFile;
import ru.netology.diplom.model.UserData;
import ru.netology.diplom.repository.FileRepository;
import ru.netology.diplom.repository.UserRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ServiceCloud {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private String userName;
    private AtomicInteger count = new AtomicInteger(0);

    public ServiceCloud(FileRepository fileRepository, UserRepository userRepository, JWTUtil jwtUtil) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void fileUpload(String token, String name, MultipartFile file) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        UserData user = userRepository.findByEmail(userName);
        FileData fileDataFromBd = fileRepository.findByFileNameAndUserDataEmail(name, user.getEmail());
        FileData fileData;
        try {
            fileData = FileData.builder()
                    .fileName(name)
                    .size(file.getSize())
                    .date(LocalDateTime.now())
                    .fileData(file.getBytes())
                    .userData(user).build();
            if (fileDataFromBd != null) {
                //Если у пользователя уже есть файл с таким именем в БД то добавляет число перед именем.
                fileData.setFileName("(" + count.incrementAndGet() + ")" + name);
            }
            fileRepository.save(fileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FileResponse> allFiles(String token, Integer limit) {
        String name = jwtUtil.resolveToken(token);
        if (name == null) {
            log.error("Ошибка вывода списка всех файлов");
            throw new ErrorInputDataException("Ошибка вывода списка всех файлов");
        }
        UserData data = userRepository.findByEmail(jwtUtil.getUsername(name));
        log.info("Список файлов успешно выведен на экран");
        return fileRepository.findFileDataByUserDataEmail(data.getEmail()).stream()
                .map(a -> new FileResponse(a.getFileName(), a.getSize())).toList();
    }

    public ByteArrayResource fileDownload(String name, String token) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(name, userName);
        return new ByteArrayResource(fileData.getFileData());
    }

    public void fileDelete(String fileName, String token) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(fileName, userName);
        fileRepository.delete(fileData);
        log.info("Файл {} успешно удален", fileData.getFileName());
    }

    public void renameFile(String fileName, RenameFile renameFile, String token) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(fileName, userName);
        fileData.setFileName(renameFile.getFilename());
        fileRepository.save(fileData);
        log.info("Файл {} успешно переименован в {} ", fileName, renameFile.getFilename());

    }
}

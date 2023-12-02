package ru.netology.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.Token.JWTUtil;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.exceptions.ErrorFileException;
import ru.netology.diplom.exceptions.ErrorInputDataException;
import ru.netology.diplom.model.FileData;
import ru.netology.diplom.model.RenameFile;
import ru.netology.diplom.model.UserData;
import ru.netology.diplom.repository.FileRepository;
import ru.netology.diplom.repository.UserRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ServiceCloud {
    private final String fileRepositoryDir = "D:\\allFiles\\"; //Базовая директива для создания хранилища файлов;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private String userName;

    public ServiceCloud(FileRepository fileRepository, UserRepository userRepository, JWTUtil jwtUtil) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void fileUpload(String token, String name, MultipartFile file) {
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        String userDir = fileRepositoryDir + userName;
        File createDir = new File(userDir);
        if (createDir.mkdir()) {
            log.info("Папка нового пользователя успешно создана");
        }
        File file1 = new File(createDir + "\\" + name);
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file1));
                stream.write(bytes);
                stream.close();
                var fileData = FileData.builder()
                        .fileName(file1.getName())
                        .size(Math.toIntExact(file1.length()))
                        .date(LocalDateTime.now())
                        .userData(userRepository.findByEmail(userName)).build();
                fileRepository.save(fileData);
                log.info("Файл {} успешно загружен в хранилище по пути {}", name, userDir);
            } catch (IOException e) {
                log.error("Ошибка загрузки файла");
                throw new ErrorFileException("Ошибка загрузки файла " + e.getMessage());
            }
        }
    }

    public List<FileResponse> allFiles(String token, Integer limit) {
        String name = jwtUtil.resolveToken(token);
        if(name == null){
            log.error("Ошибка вывода списка всех файлов");
            throw new ErrorInputDataException("Ошибка вывода списка всех файлов");
        }
        UserData data = userRepository.findByEmail(jwtUtil.getUsername(name));
        log.info("Список файлов успешно выведен на экран");
        return fileRepository.findFileDataByUserDataId(data.getId()).stream()
                .map(a -> new FileResponse(a.getFileName(), a.getSize())).toList();
    }

    public Resource fileDownload(String name, String token)  {
        FileData fileData = fileRepository.findByFileName(name);
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        Path path = Paths.get(fileRepositoryDir + userName + "\\" + fileData.getFileName());
        try {
            log.info("Файл {} успешно загружен", fileData.getFileName());
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            log.error("Ошибка загрузки файла");
            throw new ErrorFileException("Ошибка загрузки файла");
        }
    }

    public void fileDelete(String fileName, String token) {
        FileData fileData = fileRepository.findByFileName(fileName);
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        try {
            Files.delete(Path.of(fileRepositoryDir + userName + "\\" + fileName));
        } catch (IOException e) {
            log.error("Не удалось удалить файл");
            throw new ErrorFileException("Не удалось удалить файл");
        }
        fileRepository.delete(fileData);
        log.info("Файл {} успешно удален", fileData.getFileName());

    }

    public void renameFile(String fileName, RenameFile renameFile, String token) {
        FileData fileData = fileRepository.findByFileName(fileName);
        userName = jwtUtil.getUsername(jwtUtil.resolveToken(token));
        String userDir = fileRepositoryDir + userName;
        Path path = Paths.get(userDir + "\\" + fileData.getFileName());
        try {
            Files.move(path, Path.of(userDir + "\\" + renameFile.getFilename()));
        } catch (IOException e) {
            log.error("Ошибка переименования файла {}", fileName);
            throw new ErrorFileException("Не удалось переименовать файл " + e.getMessage());
        }
        fileData.setFileName(renameFile.getFilename());
        fileRepository.save(fileData);
        log.info("Файл {} успешно переименован в {} ", fileName, renameFile.getFilename());

    }
}

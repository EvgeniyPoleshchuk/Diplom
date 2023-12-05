package ru.netology.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.dto.FileResponse;
import ru.netology.diplom.exceptions.ErrorFileException;
import ru.netology.diplom.exceptions.ErrorInputDataException;
import ru.netology.diplom.model.FileData;
import ru.netology.diplom.model.RenameFile;
import ru.netology.diplom.model.UserData;
import ru.netology.diplom.repository.FileRepository;
import ru.netology.diplom.repository.UserRepository;
import ru.netology.diplom.repository.UserTokenRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ServiceCloud {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTokenRepository tokenRepository;
    private AtomicInteger count = new AtomicInteger(0);

    public String fileUpload(String token, String name, MultipartFile file) {
        UserData user = getUserByAuthToken(token);
        assert user != null;
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
            log.info("Файл {} успешно загружен", fileData.getFileName());

            return "Файл успешно загружен";
        } catch (IOException e) {
            throw new ErrorFileException("Не удалось загрузить файл");
        }
    }

    public List<FileResponse> allFiles(String token, Integer limit) {
        UserData data = getUserByAuthToken(token);
        if (data == null) {
            log.error("Ошибка вывода списка всех файлов");
            throw new ErrorInputDataException("Ошибка вывода списка всех файлов");
        }
        log.info("Список файлов успешно выведен на экран");
        return fileRepository.findFileDataByUserDataEmail(data.getEmail()).stream()
                .map(a -> new FileResponse(a.getFileName(), a.getSize())).toList();
    }

    public ByteArrayResource fileDownload(String name, String token) {
        UserData userName = getUserByAuthToken(token);
        if (userName == null) {
            log.error("Ошибка скачивания файла");
            throw new ErrorFileException("Ошибка скачивания файла");
        }
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(name, userName.getEmail());
        log.info("Файл {} успешно скачан", fileData.getFileName());
        return new ByteArrayResource(fileData.getFileData());
    }

    public void fileDelete(String fileName, String token) {
        UserData userName = getUserByAuthToken(token);
        if (userName == null) {
            log.error("Не удалось удалить файл");
            throw new ErrorFileException("Ошибка удаления файла");
        }
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(fileName, userName.getEmail());
        fileRepository.delete(fileData);
        log.info("Файл {} успешно удален", fileData.getFileName());
    }

    public void renameFile(String fileName, RenameFile renameFile, String token) {
        UserData userName = getUserByAuthToken(token);
        if (userName == null) {
            log.error("Ошибка переименования файла");
            throw new ErrorFileException("Ошибка переименования файла");
        }
        FileData fileData = fileRepository.findByFileNameAndUserDataEmail(fileName, userName.getEmail());
        fileData.setFileName(renameFile.getFilename());
        fileRepository.save(fileData);
        log.info("Файл {} успешно переименован в {} ", fileName, renameFile.getFilename());
    }

    private UserData getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = tokenRepository.getUserNameByToken(authTokenWithoutBearer);
            return userRepository.findByEmail(username);
        }
        return null;
    }
}

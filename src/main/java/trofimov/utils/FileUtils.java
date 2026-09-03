package trofimov.utils;

import aquality.selenium.browser.AqualityServices;
import java.io.IOException;
import java.nio.file.*;

public class FileUtils {
    private static final String FILES_DIR = "files";

    private FileUtils() {
    }

    public static String createFile(String fileName) {
        try {
            Path filePath = Paths.get(FILES_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, fileName);
            AqualityServices.getLogger().info("Создан файл: %s с содержимым: %s", fileName, fileName);
            return fileName;
        } catch (FileAlreadyExistsException fileAlreadyException) {
            throw new RuntimeException("Файл уже существует", fileAlreadyException);
        } catch (FileSystemException fileSystemException) {
            throw new RuntimeException("Ошибка при создании файла", fileSystemException);
        } catch (IOException ioException) {
            throw new RuntimeException("Не удалось создать или записать файл", ioException);
        }
    }

    public static String getLocalFilePath(String fileName) {
        return Paths.get(FILES_DIR, fileName).toString();
    }
}
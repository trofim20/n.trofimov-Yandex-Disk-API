package tests;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import trofimov.api.ApiResponse;
import trofimov.models.YandexDiskFileData;
import trofimov.utils.FileUtils;

public class YandexDiskTest extends BaseTest {

    /**
     * Тест проверяющий загрузку файла на диск (GET)
     */
    @Test
    void testYandexDiskUploadFile() {
        String fileName = createTestFile("upload.txt");
        String remotePath = getRemotePath(fileName);
        ApiResponse<Response> response = yandexDiskApi.uploadFile(FileUtils.getLocalFilePath(fileName), remotePath);
        Assertions.assertEquals(HttpStatus.SC_CREATED, response.getStatusCode(), "Загрузка файла должна вернуть 201");
    }

    /**
     * Тест проверяющий, что файл создан и соотвествует заданому (GET)
     */
    @Test
    public void testYandexDiskGetResource() {
        String fileName = createTestFile("getResource.txt");
        String remotePath = getRemotePath(fileName);

        uploadTestFile(fileName);

        ApiResponse<YandexDiskFileData> response = yandexDiskApi.getResource(remotePath);
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatusCode(), "Статус получения ресурса должен быть 200");
        Assertions.assertNotNull(response.getBody(), "Тело ответа не должно быть пустым");
        Assertions.assertEquals(fileName, response.getBody().getName(), "Имя файла не совпадает");
    }

    /**
     * Тест проверяющий, что файл перемещен - переименован и соотвествует нужным изменениям (POST)
     */
    @Test
    void testYandexDiskMoveResource() {
        String fileName = createTestFile("move.txt");
        String targetPath = String.format("/new-%s", fileName);
        String remotePath = getRemotePath(fileName);

        uploadTestFile(fileName);
        ApiResponse<Response> response = yandexDiskApi.moveResource(remotePath, targetPath);
        Assertions.assertEquals(HttpStatus.SC_CREATED, response.getStatusCode(), "Перемещение-переименование должно вернуть 201");

        ApiResponse<YandexDiskFileData> movedResource = yandexDiskApi.getResource(targetPath);
        Assertions.assertEquals(HttpStatus.SC_OK, movedResource.getStatusCode());
        Assertions.assertEquals(String.format("new-%s", fileName), movedResource.getBody().getName());
    }

    /**
     * Тест проверяющий, что файл удален (DELETE)
     */
    @Test
    void shouldDeleteResourceToTrash() {
        String fileName = createTestFile("delete.txt");
        String remotePath = getRemotePath(fileName);

        uploadTestFile(fileName);
        ApiResponse<Response> response = yandexDiskApi.permanentDelete(remotePath);
        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusCode(), "Удаление в корзину должно вернуть 204");

    }

    /**
     * Тест проверяющий, что файл удален, а после востановлен (PUT)
     */
    @Test
    void shouldRestoreResourceFromTrash() {
        String fileName = createTestFile("restore.txt");
        String remotePath = getRemotePath(fileName);

        uploadTestFile(fileName);
        ApiResponse<Response> deleteResponse = yandexDiskApi.deleteResource(remotePath);
        Assertions.assertEquals(HttpStatus.SC_NO_CONTENT, deleteResponse.getStatusCode());
        YandexDiskFileData trashFile = yandexDiskApi.getFileFromTrash(fileName);
        Assertions.assertNotNull(trashFile, "Файл должен быть найден в корзине");
        ApiResponse<Response> restoreResponse = yandexDiskApi.restoreFromTrash(trashFile.getPath());

        Assertions.assertEquals(HttpStatus.SC_CREATED, restoreResponse.getStatusCode(), "Восстановление должно вернуть 201");
    }
}
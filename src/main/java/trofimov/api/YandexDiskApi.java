package trofimov.api;

import aquality.selenium.browser.AqualityServices;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import trofimov.constant.ApiEndpoints;
import trofimov.models.YandexDiskFileData;

import java.io.File;
import java.util.List;
import java.util.Map;

import static trofimov.constant.ApiConstant.*;

public class YandexDiskApi extends BaseApi {
    private final String accessToken;

    public YandexDiskApi(String baseUrl, String accessToken) {
        super(baseUrl);
        this.accessToken = accessToken;
    }

    public ApiResponse<Response> getUploadUrl(String remotePath) {
        AqualityServices.getLogger().info("Получение URL для загрузки файла в %s", remotePath);
        Response response = getRequestWithAuth(ApiEndpoints.YANDEX_RESOURCES_UPLOAD, Map.of(PATH, remotePath, OVERWRITE, TRUE));
        return new ApiResponse<>(response.getStatusCode(), response);
    }

    public ApiResponse<Response> uploadFile(String localFilePath, String remotePath) {
        AqualityServices.getLogger().info("Загрузка файла: %s в %s", localFilePath, remotePath);
        File file = new File(localFilePath);

        if (!file.exists()) {
            throw new RuntimeException(String.format("Файл не найден: %s", localFilePath));
        }

        ApiResponse<Response> uploadUrlResponse = getUploadUrl(remotePath);
        String href = uploadUrlResponse.getBody().jsonPath().getString(HREF);

        Response uploadResponse = RestAssured.given()
                .header(AUTHORIZATION_HEADER, String.format("%s %s", AUTH_SCHEME, accessToken))
                .multiPart(FILE, file)
                .put(href);

        AqualityServices.getLogger().info("Ответ на загрузку: %s", uploadResponse.getStatusCode());

        return new ApiResponse<>(uploadResponse.getStatusCode(), uploadResponse);
    }

    public ApiResponse<YandexDiskFileData> getResource(String remotePath) {
        AqualityServices.getLogger().info("Получение информации о ресурсе: %s", remotePath);
        Response response = getRequestWithAuth(
                ApiEndpoints.YANDEX_RESOURCES,
                Map.of(PATH, remotePath)
        );
        YandexDiskFileData fileData = null;
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            fileData = response.as(YandexDiskFileData.class);
        }

        return new ApiResponse<>(response.getStatusCode(), fileData);
    }

    public ApiResponse<Response> moveResource(String fromPath, String toPath) {
        AqualityServices.getLogger().info("Перемещение ресурса из %s в %s", fromPath, toPath);
        Response response = postRequestWithAuth(
                ApiEndpoints.YANDEX_RESOURCES_MOVE,
                Map.of(FROM, fromPath, PATH, toPath, OVERWRITE, TRUE)
        );
        return new ApiResponse<>(response.getStatusCode(), response);
    }

    public ApiResponse<Response> permanentDelete(String path) {
        AqualityServices.getLogger().info("Безвозвратное удаление файла: %s", path);
        Response response = deleteRequestWithAuth(
                ApiEndpoints.YANDEX_RESOURCES,
                Map.of(PATH, path, PERMANENTLY, TRUE)
        );
        return new ApiResponse<>(response.getStatusCode(), response);
    }

    public ApiResponse<Response> deleteResource(String path) {
        AqualityServices.getLogger().info("Удаление файла: %s", path);
        Response response = deleteRequestWithAuth(
                ApiEndpoints.YANDEX_RESOURCES,
                Map.of(PATH, path)
        );
        return new ApiResponse<>(response.getStatusCode(), response);
    }

    public ApiResponse<Response> restoreFromTrash(String path) {
        AqualityServices.getLogger().info("Восстановление файла из корзины: %s", path);
        Response response = putRequestWithAuth(
                ApiEndpoints.YANDEX_RESOURCES_RESTORE,
                Map.of(PATH, path)
        );
        return new ApiResponse<>(response.getStatusCode(), response);
    }

    public ApiResponse<List<YandexDiskFileData>> getTrashFiles() {
        AqualityServices.getLogger().info("Получение файлов в корзине");
        Response response = getRequestWithAuth(
                ApiEndpoints.YANDEX_TRASH_RESOURCES,
                Map.of(LIMIT, DEFAULT_LIMIT)
        );

        if (response.getStatusCode() == HttpStatus.SC_OK) {
            List<YandexDiskFileData> files = response.jsonPath().getList(EMBEDDED_ITEMS, YandexDiskFileData.class);
            return new ApiResponse<>(response.getStatusCode(), files);
        }

        return new ApiResponse<>(response.getStatusCode(), null);
    }

    public YandexDiskFileData getFileFromTrash(String fileName) {
        ApiResponse<List<YandexDiskFileData>> response = getTrashFiles();
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            List<YandexDiskFileData> files = response.getBody();
            return files.stream()
                    .filter(item -> item.getName().equals(fileName))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
package tests;

import org.junit.jupiter.api.BeforeEach;
import trofimov.api.ApiResponse;
import trofimov.api.YandexDiskApi;
import trofimov.utils.FileUtils;
import trofimov.utils.SettingUtils;

public abstract class BaseTest {
    protected YandexDiskApi yandexDiskApi;

    @BeforeEach
    public void setUp() {
        String accessToken = SettingUtils.getTestData().getAccessToken();
        String apiUrl = SettingUtils.getTestData().getApiUrl();
        yandexDiskApi = new YandexDiskApi(apiUrl, accessToken);
    }

    protected String createTestFile(String fileName) {
        FileUtils.createFile(fileName);
        return fileName;
    }

    protected String getRemotePath(String fileName) {
        return String.format("/%s", fileName);
    }

    protected void uploadTestFile(String fileName) {
        String remotePath = getRemotePath(fileName);
        String localPath = FileUtils.getLocalFilePath(fileName);

        ApiResponse<?> response = yandexDiskApi.uploadFile(localPath, remotePath);
        if (response.getStatusCode() != 201) {
            throw new IllegalStateException("Не удалось загрузить тестовый файл");
        }
    }
}
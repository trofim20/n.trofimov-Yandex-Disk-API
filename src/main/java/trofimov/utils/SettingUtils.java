package trofimov.utils;

import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import trofimov.models.TestData;

public class SettingUtils {
    private static final ISettingsFile testData = new JsonSettingsFile("testData.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    private SettingUtils() {
    }

    public static TestData getTestData() {
        try {
            String json = testData.getValue("/testData").toString();
            return mapper.readValue(json, TestData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
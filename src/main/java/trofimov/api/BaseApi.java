package trofimov.api;

import java.util.Map;

import aquality.selenium.browser.AqualityServices;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.Method;
import trofimov.utils.SettingUtils;

import static trofimov.constant.ApiConstant.AUTHORIZATION_HEADER;
import static trofimov.constant.ApiConstant.AUTH_SCHEME;

public abstract class BaseApi {
    private final RequestSpecification requestSpecification;

    protected BaseApi(String baseUrl) {
        this.requestSpecification = RestAssured.given().baseUri(baseUrl).contentType(ContentType.JSON);
    }

    protected Response postRequestWithAuth(String endpoint, Map<String, String> queryParams) {
        return sendRequestWithAuth(Method.POST, endpoint, queryParams);
    }

    protected Response getRequestWithAuth(String endpoint, Map<String, String> queryParams) {
        return sendRequestWithAuth(Method.GET, endpoint, queryParams);
    }

    protected Response putRequestWithAuth(String endpoint, Map<String, String> queryParams) {
        return sendRequestWithAuth(Method.PUT, endpoint, queryParams);
    }

    protected Response deleteRequestWithAuth(String endpoint, Map<String, String> queryParams) {
        return sendRequestWithAuth(Method.DELETE, endpoint, queryParams);
    }

    protected RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    protected Response sendRequestWithAuth(Method method, String endpoint, Map<String, String> queryParams) {
        AqualityServices.getLogger().info("Отправка %s запроса на endpoint %s с параметрами %s", method, endpoint, queryParams);

        RequestSpecification requestSpecification = RestAssured.given()
                .spec(getRequestSpecification())
                .header(AUTHORIZATION_HEADER, String.format("%s %s", AUTH_SCHEME, SettingUtils.getTestData().getAccessToken()))
                .queryParams(queryParams);

        Response response = requestSpecification.request(method.name(), endpoint);
        AqualityServices.getLogger().info("Получен ответ со статус-кодом: %s", response.getStatusCode());
        return response;
    }
}

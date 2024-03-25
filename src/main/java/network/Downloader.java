package network;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;

import static burp.api.montoya.http.RequestOptions.requestOptions;
import static burp.api.montoya.http.message.StatusCodeClass.*;
import static burp.api.montoya.http.message.requests.HttpRequest.httpRequestFromUrl;

public class Downloader {
    private final Logging logging;

    private final Http http;

    public static final String DEFAULT_FILE = "https://github.com/jkwakman/Open-Cookie-Database/raw/master/open-cookie-database.csv";

    public Downloader(MontoyaApi api) {
        this.logging = api.logging();
        this.http = api.http();
    }

    public byte[] getData(String file) {
        try {
            HttpResponse response = download(file);
            return response.body().getBytes();
        } catch (IllegalStateException e) {
            logging.logToOutput(e.getMessage());
            return null;
        }
    }

    public HttpResponse download(String url) {
        HttpRequest request = httpRequestFromUrl(url);
        HttpResponse response = http.sendRequest(request, requestOptions().withUpstreamTLSVerification()).response();

        logging.logToOutput("Downloading file: " + url);

        if (response.isStatusCodeClass(CLASS_4XX_CLIENT_ERRORS) || response.isStatusCodeClass(CLASS_5XX_SERVER_ERRORS)) {
            String exceptionMessage = "Failed to download " + url + ". Error code: " + response.statusCode();
            throw new IllegalStateException(exceptionMessage);
        }

        if (response.isStatusCodeClass(CLASS_3XX_REDIRECTION)) {
            String redirect = response.headerValue("Location");
            return download(redirect);
        }

        return response;
    }
}

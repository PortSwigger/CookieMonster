package csv;

import com.opencsv.bean.CsvBindByName;

public class CookieFileBean {

    @CsvBindByName(column = "Enabled")
    private int selected = 1;

    @CsvBindByName(column = "Platform")
    private String platform;

    @CsvBindByName(column = "Category")
    private String category;

    @CsvBindByName(column = "Cookie / Data Key name")
    private String cookie;

    @CsvBindByName(column = "Wildcard match")
    private int wildcard;

    public int isSelected() {
        return selected;
    }

    public String getPlatform() {
        return platform;
    }

    public String getCategory() {
        return category;
    }

    public String getCookie() {
        return cookie;
    }

    public int getWildcard() {
        return wildcard;
    }
}

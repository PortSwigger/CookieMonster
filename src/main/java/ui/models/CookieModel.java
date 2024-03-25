package ui.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CookieModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean selected;

    private String platform;

    private String category;

    private String cookie;

    private int wildcard;

    public CookieModel(boolean selected, String platform, String category, String cookie, int wildcard) {
        this.selected = selected;
        this.platform = platform;
        this.category = category;
        this.cookie = cookie;
        this.wildcard = wildcard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CookieModel that = (CookieModel) o;
        return Objects.equals(cookie, that.cookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cookie);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getWildcard() {
        return wildcard;
    }

    public void setWildcard(int wildcard) {
        this.wildcard = wildcard;
    }
}

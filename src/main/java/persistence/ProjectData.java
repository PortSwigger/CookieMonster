package persistence;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.persistence.PersistedObject;

public class ProjectData {

    public static final String PROJECT_DATA_KEY = "cookie-monster-data";

    private final PersistedObject persistedObject;

    public ProjectData(PersistedObject persistedObject) {
        this.persistedObject = persistedObject;
    }

    public ByteArray getByteArrayByKey(String key) {
        return this.persistedObject.getByteArray(key);
    }

    public void saveByteArray(String key, byte[] value) {
        persistedObject.setByteArray(key, ByteArray.byteArray(value));
    }
}

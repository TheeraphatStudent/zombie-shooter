package client.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Communication<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, List<T>> models;

    public Communication() {
        models = new HashMap<>();
    }

    public void setContent(String key, List<T> values) {
        this.models.put(key, values);
    }

    public Map<String, List<T>> getContent() {
        return this.models;
    }
}

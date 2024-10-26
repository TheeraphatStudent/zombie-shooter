package client.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ClientObj;

public class Communication implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, List<ClientObj>> models;

    public Communication() {
        models = new HashMap<String, List<ClientObj>>();

    }

    public void setContent(String key, List<ClientObj> values) {
        this.models.put(key, values);
        
    }

    public Map<String, List<ClientObj>> getContent() {
        return this.models;

    }
}

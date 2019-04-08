package wang.blog.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {

    private Map<String, Object> objects;

    public ViewObject() {
        objects = new HashMap<>();
    }

    public void set(String key, Object value){
        objects.put(key, value);
    }

    public Object get(String key){
        return objects.get(key);
    }


}

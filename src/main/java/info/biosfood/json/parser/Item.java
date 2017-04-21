package info.biosfood.json.parser;

import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("_id")
    private String id;

    private String name;

    @SerializedName("registered")
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return new StringBuilder().append("\n").append(this.getClass().getSimpleName()).append(" {\n")
                .append(" id: ").append(id).append(",\n")
                .append(" name: ").append(name).append(",\n")
                .append(" timestamp: ").append(timestamp)
                .append("\n}")
                .toString();
    }

}

package sahil.iiitk_foundationday_app.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatsList {

    public ChatsList() {
    }
    private String id,name;
    private long numOfNew;
    private List<Msg> messages;

    public ChatsList(JSONObject object) throws JSONException {
        if (object.has("id")) this.id = object.getString("id");
        if (object.has("name")) this.name = object.getString("name");
       if (object.has("numOfNew")) this.numOfNew=object.getLong("numOfNew");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumOfNew() {
        return numOfNew;
    }

    public void setNumOfNew(long numOfNew) {
        this.numOfNew = numOfNew;
    }

    @Override
    public boolean equals(Object obj) {
        ChatsList chatsList=(ChatsList) obj;
        return chatsList.getId().equals(this.id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Msg> getMessages() {
        return messages;
    }

    public void setMessages(List<Msg> messages) {
        this.messages = messages;
    }
}

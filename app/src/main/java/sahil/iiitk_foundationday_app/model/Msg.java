package sahil.iiitk_foundationday_app.model;


import org.json.JSONException;
import org.json.JSONObject;

public class Msg {

    public Msg() {
    }

    private String message,sender,time;
    private long id;

    public Msg(JSONObject object) throws JSONException {
        if (object.has("id")) this.id = object.getLong("id");
        if (object.has("message")) this.message = object.getString("message");
        if (object.has("sender")) this.sender = object.getString("sender");
        if (object.has("time")) this.time = object.getString("time");
    }

    @Override
    public boolean equals(Object obj) {
        Msg msg=(Msg) obj;
        return msg.getId() == this.id;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

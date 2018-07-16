package sahil.iiitk_foundationday_app.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyPersonalRegistrations {

    private String ffid;
    private List<SingleEventPersonal> eventList;
    private JSONArray jsonArray;

    public MyPersonalRegistrations(){
    }
    public MyPersonalRegistrations(Context context){
    }

    public MyPersonalRegistrations(JSONObject object) throws JSONException {
        if (object.has("ffid")) this.ffid=object.getString("ffid");
    }

    @Override
    public boolean equals(Object obj) {
        MyPersonalRegistrations reg=(MyPersonalRegistrations) obj;
        return this.ffid.equals(reg.getFfid());
    }

    public String getFfid() {
        return ffid;
    }

    public void setFfid(String ffid) {
        this.ffid = ffid;
    }

    public List<SingleEventPersonal> getEventList() {
        return eventList;
    }

    public void setEventList(List<SingleEventPersonal> eventList) {
        this.eventList = eventList;
    }
}

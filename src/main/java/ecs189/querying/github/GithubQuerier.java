package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br /><br />");
            // Add the commits
            for (int c = 0; c < commits.length(); c++){
                JSONObject commit = commits.getJSONObject(c);
                JSONObject author = commit.getJSONObject("author");
                sb.append("commit " + commit.getString("sha"));
                sb.append("<br />");
                sb.append("Author: " + author.getString("name") + " &lt" + author.getString("email") + "&gt");
                sb.append("<br /><br />");
                sb.append("&emsp;" + commit.getString("message"));
                sb.append("<br /><br />");
            }

            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        URL url;
        int page = 1;
        Boolean loop = true;
        JSONArray roots = new JSONArray();
        JSONObject json;
        JSONArray root;
        do{
            url = new URL(BASE_URL + user + "/events"  + "?page=" + page);
            page++;
            json = Util.queryAPI(url);
            root = json.getJSONArray("root");
            if (root.length()==0){
                loop = false;
            }
            else{
                roots.put(root);
            }
        }while(loop);

        //Call method to flatten arrays into a single array
        JSONArray events = combineRoot(roots);


        System.out.println(events);
        int j = 0;
        for (int i = 0; i < events.length() && j < 10; i++) {
            JSONObject event = events.getJSONObject(i);
            if (event.getString("type").equals("PushEvent")){
                eventList.add(event);
                j++;
            }
        }
        return eventList;
    }
    private static JSONArray combineRoot(JSONArray roots) {
        JSONArray root;//Combine all the arrays into a single array
        JSONArray cRoot = new JSONArray();
        for (int i = 0; i < roots.length(); i++){
            root = roots.getJSONArray(i);
            for (int j = 0; j < root.length(); j++){
                cRoot.put(root.getJSONObject(j));
            }
        }
        return cRoot;
    }
}
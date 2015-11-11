import java.util.UUID;

/**
 * Created by jelink on 11/11/15.
 */
public class Tweet {

    private UUID tweetId;
    private String username;
    private String body;

    public Tweet (UUID _tweetId, String _username, String _body) {
        tweetId = _tweetId;
        username = _username;
        body = _body;
    }

    public UUID getTweetId() {
        return tweetId;
    }

    public void setTweetId(UUID _tweetId) {
        tweetId = _tweetId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String _username) {
        username = _username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String _body) {
        body = _body;
    }
}

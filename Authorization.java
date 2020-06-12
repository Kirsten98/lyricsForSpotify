public class Authorization {
    /**
     * Variables of various types of tokens retrieved from Spotify API's
     */
    private String authorizationToken;
    private String access_token;
    private String refresh_token;
    private int expires_in;


    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    protected void resetAuthentication () {
        authorizationToken = "";
        access_token = "";
        refresh_token = "";
        expires_in = 0;
    }
}

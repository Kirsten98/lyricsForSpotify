public class Authorization {
    /**
     * Variables of various types of tokens retrieved from Spotify API's
     */
    private static String authorizationToken;
    private static String access_token;
    private static String refresh_token;
    private static int expires_in;

    private static Authorization instance = null;

    private Authorization(){

    }

    public static Authorization getInstance() {
        if (instance == null) {
            return new Authorization();
        } else {
            return instance;
        }
    }
    public static String getAuthorizationToken() {
        return authorizationToken;
    }

    public static void setAuthorizationToken(String newAuthorizationToken) {
        authorizationToken = newAuthorizationToken;
    }

    public static String getAccess_token() {
        return access_token;
    }

    public static void setAccess_token(String newAccessToken) {
        access_token = newAccessToken;
    }

    public static String getRefresh_token() {
        return refresh_token;
    }

    public static void setRefresh_token(String newRefreshToken) {
        refresh_token = newRefreshToken;
    }

    public static int getExpires_in() {
        return expires_in;
    }

    public static void setExpires_in(int newExpiresIn) {
        expires_in = newExpiresIn;
    }

    public static void resetAuthentication () {
        authorizationToken = "";
        access_token = "";
        refresh_token = "";
        expires_in = 0;
    }
}

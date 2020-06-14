import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ThreadControl{

    protected Thread currentSongCheckThread,expiredTokenRefresh, songProgressThread;
    LyricsForSpotifyApplication parent;
    protected boolean currentSongCheckExit = false;
    protected boolean expiredTokenRefreshExit = false;
    protected boolean songProgressThreadExit = false;

    public ThreadControl(LyricsForSpotifyApplication parent){
        this.parent = parent;

        currentSongCheckThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Will keep the thread alive until the current song is no longer playing
                while(parent.isCurrentSongStillPlaying() && !currentSongCheckExit)
                    parent.isCurrentSongStillPlaying();
            }
        });

        expiredTokenRefresh = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!expiredTokenRefreshExit) {
                        TimeUnit.SECONDS.sleep(parent.userAuthorization.getExpires_in() - 10);
                        URL accessTokenRefreshURL = new URL("https://accounts.spotify.com/api/token?grant_type=refresh_token&refresh_token=" + parent.userAuthorization.getRefresh_token() + "&client_id=" + Configuration.getClientID() + "&client_secret="+ Configuration.getClientSecret());
                        HttpURLConnection connection = (HttpURLConnection) accessTokenRefreshURL.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestProperty("client_id", Configuration.getEncodedClientID());
                        connection.setRequestProperty("client_secret", Configuration.getEncodedClientSecret());
                        connection.setRequestProperty("Content-length", "0");
                        System.out.println(connection.getResponseCode());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        String bufferedInput;
                        while ((bufferedInput = bufferedReader.readLine()) != null) {
                            String[] splitInput = bufferedInput.split("\"");
                            for (int i = 0; i < splitInput.length; i++) {
                                System.out.println(splitInput[i]);
                                switch (splitInput[i]) {
                                    case ("access_token"):
                                        parent.userAuthorization.setAccess_token(splitInput[i + 2]);
                                        i += 2;
                                        break;
                                    case ("expires_in"):
                                        String[] expiresSplit = splitInput[i + 1].split(":");
                                        parent.userAuthorization.setExpires_in(Integer.parseInt(expiresSplit[1].split(",")[0]));
                                        i++;
                                        break;
                                }
                            }
                        }
                        System.out.println("Access Token: " + parent.userAuthorization.getAccess_token());
                        System.out.println("Expires in: " + parent.userAuthorization.getExpires_in());
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    parent.runStepBasedOnStatus("New Application");
                }
            }
        });

        songProgressThread = new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    while (songProgressThread != null || songProgressThread.isInterrupted() || !songProgressThread.isAlive() && !songProgressThreadExit){
                        TimeUnit.MILLISECONDS.sleep(100);
                        URL playerURL = new URL("https://api.spotify.com/v1/me/player/currently-playing");
                        HttpURLConnection connection  = (HttpURLConnection) playerURL.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Authorization", "Bearer " + parent.userAuthorization.getAccess_token());

                        System.out.println("Progress response Code: " + connection.getResponseCode());
                        if (connection.getResponseCode() == 200){
                            String input;
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            while ((input = bufferedReader.readLine()) != null){
                                String[] inputSplit = input.split(":|,");
                                switch (inputSplit[0].replace(" ","")) {
                                    case "\"progress_ms\"":
                                        System.out.println("Current progress: "+inputSplit[1]);
                                        parent.currentSongProgressMS = Double.parseDouble(inputSplit[1].replace("\"","").replace(" ",""));
                                        break;

                                    case "\"duration_ms\"":
                                        System.out.println("Current duration: "+inputSplit[1]);
                                        parent.currentSongDuration = Double.parseDouble(inputSplit[1].replace("\"","").replace(" ",""));

                                        break;
                                }
                            }
                            parent.scrollPane.setVvalue((parent.currentSongProgressMS / parent.currentSongDuration)*100);
                            System.out.println(parent.scrollPane.getVvalue());
                        }else {
                            System.out.println("Error during song progress check: Error Code " + connection.getResponseCode());
                        }
                    }

                } catch (Exception e){
                    songProgressThread.interrupt();
                    System.out.println(e.getMessage());
                    songProgressThreadStart();
                }
            }
        });

    }

    public void startCurrentSongCheckThread() {
        currentSongCheckExit = false;
        currentSongCheckThread.start();
    }

    public void interruptCurrentSongCheckThread() {
        currentSongCheckThread.interrupt();
    }

    public void expiredTokenRefreshStart() {
        expiredTokenRefreshExit = false;
        expiredTokenRefresh.start();
    }

    protected void songProgressThreadStart() {
            songProgressThreadExit = false;
            songProgressThread.start();
    }

    public void resetThreads() {
        currentSongCheckExit = true;
        expiredTokenRefreshExit = true;
        songProgressThreadExit = true;

        if (currentSongCheckThread != null && !currentSongCheckThread.isInterrupted()) {
            currentSongCheckThread.interrupt();
        }
        if (expiredTokenRefresh != null && !expiredTokenRefresh.isInterrupted()) {
            expiredTokenRefresh.interrupt();
        }
        if (songProgressThread != null && !songProgressThread.isInterrupted()) {
            songProgressThread.interrupt();
        }

    }

}

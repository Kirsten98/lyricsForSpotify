import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LyricsForSpotifyApplication extends Application {

    //FIXME Go through all Catch scenarios and make something happen more meaningful if the method is to fail connection.
    //TODO have fallback for getLyrics with second API for lyrics.
    //TODO Create a thread to monitor isAuthorized.

    /**
     * Variables used to access Spotify API's
     */
    private String spotifyBaseURL = "https://api.spotify.com";
    protected String redirectURI ="http://localhost:8080";
    private String deviceID;

    protected Authorization userAuthorization = Authorization.getInstance();
    private static Logger logger = Logger.getLogger(LyricsForSpotifyApplication.class.getName());


    /**
     * Variables used to determine the status of the application
     */
    protected boolean streamFriendlyBoolean = false;
    private boolean autoScroll = true;

    /**
     * Variables for the current track information from Spotify and APISeed API's
     */
    protected static TextConfigurations textConfigurations = new TextConfigurations();
    protected static StageControl stageControl;
    protected ThreadControl threadControl = new ThreadControl(this);

    protected static String currentArtist;
    protected static String currentSong;
    protected static Text currentLyrics = new Text();
    protected static Text song = new Text();
    protected static Text artist = new Text();
    protected ScrollPane scrollPane = new ScrollPane(currentLyrics);
    private static HashMap<String, String> mapOfLyrics = new HashMap<>(); // Used to cache lyrics of the first time the song is played to be used at later times.
    protected double currentSongProgressMS, currentSongDuration =1;
        //TODO complete logging

    /**
     * Main method that starts the JavaFX application
     * @param args
     */
    public static void main(String[] args) {
        addStreamMusicLyricsToMapOfLyrics();
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        logger.log(Level.INFO, "Main Method: Calling launch(args)");
        launch(args);

    }

    /**
        This method is to manually insert lyrics for Kirsten's Spotify Playlist "Stream Music" into mapOfLyrics that cannot be obtained by APISEED lyrics API.
     */
    private static void addStreamMusicLyricsToMapOfLyrics(){
        // Preset lyrics for Stream Music
        mapOfLyrics.put("On The Road","'Cause they ain't never seein' me fold under pressure\n" +
                "They ain't ever, ever seen me fold, not never\n" +
                "Always keep one up on 'em, 'cause I'm too clever\n" +
                "I will never, ever sell my soul not never\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "\n" +
                "Yeah, they bet I'ma fall\n" +
                "And I'm only 24, but bitch, I been through it all\n" +
                "I got so many hits, can't remember them all\n" +
                "While I'm takin' a shit, look at the plaques on the wall\n" +
                "Y'all just sit back, won't you kick back\n" +
                "Keep on actin' like you did that, got no respect\n" +
                "For nobody who's just fake in life, aye, aye\n" +
                "You ain't who you really say you are\n" +
                "So pick up the pace, there ain't no slowing me up\n" +
                "Get the fuck out my face, can you stop blowing me up?\n" +
                "'Cause one time is expensive, one mil a set list\n" +
                "Bought a new car 'fore you walk out for breakfast, yeah\n" +
                "\n" +
                "'Cause they ain't never seein' me fold under pressure\n" +
                "They ain't ever, ever seen me fold, not never\n" +
                "Always keep one up on 'em, 'cause I'm too clever\n" +
                "I will never, ever sell my soul not never\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "\n" +
                "Yeah, where you been?\n" +
                "When it was time to do some work and put it in\n" +
                "I just be laughin' when I see 'em, look at them\n" +
                "Leave when I lose and pop back up soon as I win\n" +
                "You ain't my day one and I know you not my friend\n" +
                "So I can't judge you when you do some phony shit and just pretend\n" +
                "Like you really down with me, because bein' loyal starts within\n" +
                "I seen that money overpower love and turn it thin\n" +
                "But it ain't stop me, I just kept goin' up, took it on the chin\n" +
                "I wish every time they say I would fold, I'd make an \"M\"\n" +
                "Fuck around be as rich as Jeff Besos, say it again\n" +
                "Never lose, I go overtime on 'em, stay in that gym, let's get it\n" +
                "\n" +
                "'Cause they ain't never seein' me fold under pressure\n" +
                "They ain't ever, ever seen me fold, not never\n" +
                "Always keep one up on 'em, 'cause I'm too clever\n" +
                "I will never, ever sell my soul not never\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "\n" +
                "I'm tired of bein' tired\n" +
                "This my last shot, you can't be mad, at least I tried\n" +
                "We supposed to be grown, but yet, you still actin' childish\n" +
                "I can get on the jet alone, I don't need no extra baggage\n" +
                "This year turned into a savage, I got baguettes in my necklace\n" +
                "Driving fast and I won't crash it and I'm supposed to be sober\n" +
                "But some kind of way, I just keep getting older\n" +
                "It's gon' take more than some pressure to fold me\n" +
                "Tried as hard as I can, but at this point it's outta my hands\n" +
                "I ain't runnin' out of these bands for nothin' or no one\n" +
                "She said I'm a dog, but it takes one to know one\n" +
                "Been goin' hard, been by myself, I don't need no love\n" +
                "\n" +
                "'Cause they ain't never seein' me fold under pressure\n" +
                "They ain't ever, ever seen me fold, not never\n" +
                "Always keep one up on 'em, 'cause I'm too clever\n" +
                "I will never, ever sell my soul not never\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time\n" +
                "'Cause I been on the road, been on the road, I\n" +
                "Quit actin' like you been with me this whole time");
        mapOfLyrics.put("Hollywood's Bleeding","Hollywood's bleeding, vampires feeding, darkness turns to dust\n" +
                "Everyone's gone but no one's leaving, nobody left but us\n" +
                "Tryna chase a feeling but we never feel it, riding on our last train home\n" +
                "Dying in our sleep, we're living out a dream, we only make it out alone\n" +
                "\n" +
                "I just keep on hoping that you call me\n" +
                "You say you wanna see me but you can't right now\n" +
                "You never took the time to get to know me\n" +
                "Was scared of losing something that we never found\n" +
                "We're running out of reasons but we can't let go\n" +
                "Yeah, Hollywood is bleeding but we call it home\n" +
                "\n" +
                "Outside the winter sky turning grey\n" +
                "City up in smoke it's only ash when it rains\n" +
                "Howl at the moon and go to sleep in the day\n" +
                "Love for everybody 'til the drugs fade away\n" +
                "In the morning, blocking out the sun with the shades\n" +
                "She gotta check her pulse and tell herself that she okay\n" +
                "It seem like dying young is an honor\n" +
                "But who be at my funeral? I wonder\n" +
                "I go out, and all the eyes on me\n" +
                "I show out, do you like what you see?\n" +
                "And now, they closing in on me\n" +
                "Let 'em sharpen all they teeth\n" +
                "This is more than I can handle\n" +
                "Blood in my Lambo\n" +
                "Wish I could go, I'm losing hope\n" +
                "I light a candle, some Palo Santo\n" +
                "For all these demons, wish I could just go on\n" +
                "\n" +
                "I just keep on hoping that you call me\n" +
                "You say you wanna see me but you can't right now\n" +
                "You never took the time to get to know me\n" +
                "Was scared of losing something that we never found\n" +
                "We're running out of reasons but we can't let go\n" +
                "Yeah, Hollywood is bleeding but we call it home");
        mapOfLyrics.put("Saint-Tropez", "Ooh, yeah, yeah, yeah\n" +
                "\n" +
                "Such a long time\n" +
                "I've been waiting, I've been waiting for a long time\n" +
                "Such a long time\n" +
                "I've been waiting, I've been waiting for a long time\n" +
                "Such a long time (Ooh)\n" +
                "\n" +
                "Ooh, this shit bliss, I'm so rich (turned as shit, ooh)\n" +
                "Abs like Abercrombie Fitch (Damn, ooh)\n" +
                "Mille on my wrist\n" +
                "Versace boxers on my dick (On my dick, damn)\n" +
                "Bud Light running through my piss (Ooh)\n" +
                "On a yacht, fifty meters, insuffici— (insufficient)\n" +
                "Fifty carats on my fist (On my fist)\n" +
                "The roof go down when I hit switch (Ooh, bitch)\n" +
                "I money-ball like Bradley Pitt (Bradley Pitt yeah)\n" +
                "I work so hard for all this shit (All this shit)\n" +
                "Pumping out classics, in the Batmobile going bat shit\n" +
                "\n" +
                "Such a long time (damn)\n" +
                "I've been waiting, I've been waiting for a long time (Such a long time)\n" +
                "Such a long time (Such a long time)\n" +
                "I've been waiting, I've been waiting for a long time\n" +
                "Such a long time\n" +
                "\n" +
                "I'm in Saint-Tropez, I had to check wrist (Wow)\n" +
                "I just bought my girl a new necklace (A new necklace)\n" +
                "1-2-3-4-5-6 (5-6)\n" +
                "I take 'em all, don't matter what the price is (What the price is)\n" +
                "I said I'm sorry mama for my vices (for my vices)\n" +
                "You'll never understand what my life is (what my life is)\n" +
                "1-2-3-4-5-6 (Damn)\n" +
                "Shit I'm checking off my bucket list (My bucket list)\n" +
                "You try to give advice, I don't need it\n" +
                "I've been doing what I want since fetus\n" +
                "What you call a holiday, I call another day\n" +
                "And I ain't ever stopping, no apologies\n" +
                "\n" +
                "Such a long time (Such a long time)\n" +
                "I've been waiting, I've been waiting for a long time (Such a long time)\n" +
                "Such a long time (Such a long time)\n" +
                "I've been waiting, I've been waiting for a long time (For a long time)\n" +
                "Such a long time\n" +
                "\n" +
                "I'm in Saint-Tropez, I had to check wrist\n");
        mapOfLyrics.put("Enemies (feat. DaBaby)","Used to have friends, but now I got enemies\n" +
                "Used to keep 'em close, now they dead to me\n" +
                "Used to have friends, now I got enemies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "They said I would never get this fa-a-ar\n" +
                "Think that we don't see who you are, are, are\n" +
                "Laughing to the bank, like ha, ha, ha\n" +
                "That's it, I'm just talking too much, blah, blah, blah (Shut the fuck up)\n" +
                "\n" +
                "So, where did y'all go\n" +
                "When I was shit broke, couldn't even buy smokes\n" +
                "Now your mama needs tickets to my stadium show\n" +
                "She love it when she hear me on the radio, oh\n" +
                "\n" +
                "I know it's hard to swallow your pride\n" +
                "Sorry that you can't get over me\n" +
                "Now you're out my life, I'm so relieved, I\n" +
                "\n" +
                "Used to have friends, now I got enemies\n" +
                "Used to keep 'em close, now they dead to me\n" +
                "Money tend to show all their tendencies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "Sometimes, every time they let me down\n" +
                "Sometimes, every time they let me down\n" +
                "Used to have friends, now I got enemies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "Close to my enemies (Let's go)\n" +
                "I need better energy (Huh)\n" +
                "When I told you, \"Fuck you\", you thought I was playin', huh? (Yeah, yeah)\n" +
                "But I meant that shit literally\n" +
                "She wanted to vibe, but I told her fuck that (Nope)\n" +
                "She looked at me like she surprised\n" +
                "The pack in the post and I'm home alone, wanna buy, see the door, they go for 35\n" +
                "I try to put it on for my patnas they turn into enemies right before a n***** eyes\n" +
                "You reach for a shake, I'ma hold out a fist, give a n**** a quarter pound without the fries\n" +
                "I just went double platinum with no features just to show a n**** I don't really need him\n" +
                "Pass a man a plate and he can make it shake, it's guaranteed he gon' bite the hand that feed him\n" +
                "And I know you think that I ain't see it\n" +
                "And I know your bitch ain't gotta call, don't make me go and buy your bitch a little Prius\n" +
                "Fuck her on the camera, we can call it even\n" +
                "Friends are like the Autumn, every year they leavin'\n" +
                "And I'ma rake 'em in a pile, throw 'em in a bag\n" +
                "Tie them bitches up and leave 'em\n" +
                "'Cause most of these n***** are decievin'\n" +
                "And I\n" +
                "\n" +
                "Used to have friends, now I got enemies\n" +
                "Used to keep 'em close, now they dead to me\n" +
                "Money tend to show all their tendencies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "Sometimes, every time they let me down\n" +
                "Sometimes, every time they let me down\n" +
                "Used to have friends, now I got enemies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "It's too late to turn this shit around\n" +
                "Only held me down when you wanted me to drown\n" +
                "It's too late to turn this shit around\n" +
                "So don't try to tell me that you're happy for me now\n" +
                "\n" +
                "Used to have friends, now I got enemies\n" +
                "Used to keep 'em close, now they dead to me\n" +
                "Money tend to show all they tendencies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "Sometimes, every time they let me down\n" +
                "Sometimes, every time they let me down\n" +
                "Used to have friends, now I got enemies\n" +
                "Enemies, yeah, so sad\n" +
                "\n" +
                "Ooh\n" +
                "It's too late\n" +
                "It's too late\n");
        mapOfLyrics.put("Allergic","Wasted on Sunday\n" +
                "Erase you on Monday\n" +
                "Allergic, allergic\n" +
                "Gave in by Friday\n" +
                "Went straight back to sideways\n" +
                "Allergic, allergic\n" +
                "\n" +
                "I took your pills and your drugs\n" +
                "Just to feel something else\n" +
                "'Cause I can't feel you no more\n" +
                "\n" +
                "So sad but true\n" +
                "Friends with all my demons\n" +
                "The only one who sees them\n" +
                "Too bad for you\n" +
                "So sad but true\n" +
                "Give a hundred million reasons\n" +
                "But why could you believe them?\n" +
                "Too bad for you\n" +
                "\n" +
                "Yeah we fight and we fuck\n" +
                "Until we open the cuts\n" +
                "And now we're soberin' up\n" +
                "But never sober enough\n" +
                "Allergic, allergic\n" +
                "Instead of holdin' me down\n" +
                "You're only holdin' me up\n" +
                "It shouldn't be so hard\n" +
                "This is impossible love\n" +
                "Allergic, allergic\n" +
                "\n" +
                "I took your pills and your drugs\n" +
                "Just to feel something else\n" +
                "'Cause I can't feel you no more\n" +
                "\n" +
                "So sad but true\n" +
                "Friends with all my demons\n" +
                "The only one who sees them\n" +
                "Too bad for you\n" +
                "So sad but true\n" +
                "Give a hundred million reasons\n" +
                "But why could you believe them?\n" +
                "Too bad for you\n" +
                "\n" +
                "So sad but true\n" +
                "Give a hundred million reasons\n" +
                "But why could you believe them?\n" +
                "Too bad for you\n" +
                "\n");
        mapOfLyrics.put("A Thousand Bad Times","Said you needed a ride, but you wanted my car\n" +
                "Without that face, girl, you wouldn't get far\n" +
                "I really like you, despite who you are (Who you are)\n" +
                "You see me on TV, you know I'm a star\n" +
                "You say you don't know me, but I know that's false\n" +
                "I'll pay the price, girl, whatever that cost (What it cost)\n" +
                "\n" +
                "You've made my life so hard\n" +
                "But that's what gets me off\n" +
                "\n" +
                "I had a thousand bad times, so what's another time to me? (What's that to me?)\n" +
                "You tried to blow my house down, but what's another house to me? (What's that to me?)\n" +
                "'Cause I can take anything that you give me (That you give)\n" +
                "It's gonna take a lot more to kill me, bitch\n" +
                "So thank you for the grave, I needed me a place to sleep (Place to sleep)\n" +
                "\n" +
                "And I don't wanna meet your mama (No)\n" +
                "She pro'lly crazier than you (She pro'lly crazier than you)\n" +
                "I'm gonna need some thicker armor (Armor)\n" +
                "To spend another night with you (And on and on and on)\n" +
                "\n" +
                "Baby, I know just what to do\n" +
                "Every time you fuck me over, I come back to you\n" +
                "Baby, I don't wanna know the truth (Know the truth)\n" +
                "I ignore them when they tell me all the shit you do (Shit you do)\n" +
                "I always get my heart broke, like I needed the practice\n" +
                "Foot on my throat till my world is collapsing\n" +
                "Now this what I chose, it's the law of attraction, yeah (And on and on and on)\n" +
                "\n" +
                "Now you've made my life so hard\n" +
                "But that's what gets me off\n" +
                "\n" +
                "I had a thousand bad times, so what's another time to me? (What's that to me?)\n" +
                "You tried to blow my house down, but what's another house to me? (What's that to me?)\n" +
                "'Cause I can take anything that you give me (That you give)\n" +
                "It's gonna take a lot more to kill me, bitch\n" +
                "So thank you for the grave, I needed me a place to sleep (Place to sleep)\n" +
                "\n" +
                "I should get out, but I still want more\n" +
                "I should get out, what am I waiting for?\n" +
                "\n" +
                "It's all the same to me\n" +
                "It's all a game to me\n" +
                "It's all the same to me\n" +
                "It's all the same\n" +
                "\n" +
                "I had a thousand bad times, so what's another time to me? (What's that to me?)\n" +
                "You tried to blow my house down, but what's another house to me? (What's that to me?)\n" +
                "'Cause I can take anything that you give me (That you give)\n" +
                "It's gonna take a lot more to kill me, bitch\n" +
                "So thank you for the grave, I needed me a place to sleep (Place to sleep)");
        mapOfLyrics.put("Circles","Oh, oh, oh-oh\n" +
                "Oh, oh, oh-oh\n" +
                "Oh, oh, oh-oh, oh-oh, oh-oh\n" +
                "\n" +
                "We couldn't turn around till we were upside down\n" +
                "I'll be the bad guy now, but know I ain't too proud\n" +
                "I couldn't be there even when I try\n" +
                "You don't believe it, we do this every time\n" +
                "\n" +
                "Seasons change and our love went cold\n" +
                "Feed the flame 'cause we can't let go\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away\n" +
                "I dare you to do something\n" +
                "I'm waiting on you again, so I don't take the blame\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away, run away\n" +
                "\n" +
                "Let go, I got a feeling that it's time to let go\n" +
                "I said so, I knew that this was doomed from the get-go\n" +
                "You thought that it was special, special\n" +
                "But it was just the sex though, the sex though\n" +
                "And I still hear the echoes (The echoes)\n" +
                "I got a feeling that it's time to let it go, let it go\n" +
                "\n" +
                "Seasons change and our love went cold\n" +
                "Feed the flame 'cause we can't let go\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away\n" +
                "I dare you to do something\n" +
                "I'm waiting on you again, so I don't take the blame\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away, run away\n" +
                "\n" +
                "Maybe you don't understand what I'm going through\n" +
                "It's only me, what you got to lose?\n" +
                "Make up your mind, tell me, what are you gonna do?\n" +
                "It's only me, let it go\n" +
                "\n" +
                "Seasons change and our love went cold\n" +
                "Feed the flame 'cause we can't let go\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away\n" +
                "I dare you to do something\n" +
                "I'm waiting on you again, so I don't take the blame\n" +
                "Run away, but we're running in circles\n" +
                "Run away, run away, run away");
        mapOfLyrics.put("Die For Me (feat. Future & Halsey)","(Oh yeah, oh yeah, oh yeah)\n" +
                "You'd die for me\n" +
                "(Said you'd die, said you'd die, but you lied, you lied to me)\n" +
                "\n" +
                "Said you'd take a bullet, told me you would die for me\n" +
                "I had a really bad feeling you'd been lying to me\n" +
                "We were on the low, but you were getting high with me\n" +
                "When it's past 11, that's a different side, I see\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "But you lied to me, you lied to me, you lied to me\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "But you lied to me, you lied to me, you lied to me\n" +
                "\n" +
                "It was love at first sight, felt like you were chosen\n" +
                "But that blood in your veins, yeah, I know it's frozen\n" +
                "Got no patience no more, been waiting too long\n" +
                "You hid that shit away and I was the last one to know\n" +
                "Bitch, aye, I heard you comin' in I was lying there, awake\n" +
                "Didn't know that it was you, got the gun right out the safe (Damn)\n" +
                "Remember when you got my ass arrested? (Wow)\n" +
                "At least when I was in jail, I got some rest in\n" +
                "Ooh, I surrender, time to forget you\n" +
                "I'm too tired to forgive you, it's too hard on my liver\n" +
                "And you know it's all over now\n" +
                "All your friends, you know, they sold you out\n" +
                "\n" +
                "Said you'd take a bullet, told me you would die for me (Die for me)\n" +
                "I had a really bad feeling you'd been lying to me (Lying to me)\n" +
                "We were on the low but you were getting high with me (High with me)\n" +
                "When it's past 11, that's a different side, I see\n" +
                "Said you'd die for me, you'd die for me, you'd die for me (Die, die)\n" +
                "But you lied to me, you lied to me, you lied to me (Lie, lie, lie, lie)\n" +
                "Said you'd die for me, you'd die for me, you'd die for me (Die, die, die)\n" +
                "But you lied to me, you lied to me, you lied to me, yeah\n" +
                "\n" +
                "It was a V.I.P., happened to be one of my best nights\n" +
                "9 a.m., I came from out the club, it was daylight (Daylight)\n" +
                "Got a bad girl, I was treatin' her too nice (Too nice)\n" +
                "Caught you bein' vulnerable, that ain't what I need (That ain't what I need)\n" +
                "You just sold your soul, girl, quit cryin' (Sold your soul)\n" +
                "From your passport, it looks like you lyin' (You know you cold)\n" +
                "You broken down by your past, don't deny it\n" +
                "Your ex called, you was vulnerable, you was flyin', nah (Flyin')\n" +
                "Wasn't on the best of terms, girl, I was tryin' (I was tryin')\n" +
                "I did everything to reach out to you (I was reachin' out)\n" +
                "Said you never had me caught up in no drama (No drama)\n" +
                "I done ran into my karma (I done ran into)\n" +
                "\n" +
                "Said you'd take a bullet, told me you would die for me\n" +
                "I had a really bad feeling you been lying to me\n" +
                "We've been on the low but you been getting high with me\n" +
                "When it's past 11, that's a different side, I see\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "But you lied to me, you lied to me, you lied to me\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "But you lied to me, you lied to me, you lied to me\n" +
                "\n" +
                "Settle down, I'll spell it out, it's simple enough\n" +
                "I came around, I figured out, should follow my gut\n" +
                "I don't play anymore, I went through your phone\n" +
                "And called the girls in your DM's and took all them home\n" +
                "And I know it's been a while since the last time you heard from me\n" +
                "Grew into a savage and that's why they gave this verse to me\n" +
                "Turns out it shows, 'cause they turn out at shows\n" +
                "I sold fifteen million copies of a breakup note\n" +
                "Brought some strangers in our beds and now you lost your right to privacy\n" +
                "Spilling all our secrets when you thought that they'd probably die with me\n" +
                "And I know you fuckin' love it on the low\n" +
                "And you don't have to say I'm crazy, 'cause I know\n" +
                "Nothing's changed, though\n" +
                "\n" +
                "Said you'd take a bullet, told me you would die for me\n" +
                "(You'd die, you'd die, you'd die)\n" +
                "I had a really bad feeling you'd been lying to me\n" +
                "(But you lied, you lied, you lied)\n" +
                "We were on the low, but you were getting high with me\n" +
                "(You'd die, you'd die, you'd die)\n" +
                "When it's past 11, that's a different side I see\n" +
                "(But you lied, you lied, you lied)\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "(You'd die, you'd die, you'd die)\n" +
                "But you lied to me, you lied to me, you lied to me\n" +
                "(But you lied, you lied, you lied)\n" +
                "Said you'd die for me, you'd die for me, you'd die for me\n" +
                "(You'd die, you'd die, you'd die)\n" +
                "But you lied to me ...\n" +
                "You lied");
        mapOfLyrics.put("Take What You Want (feat. Ozzy Osbourne & Travis Scott)","I feel you crumble in my arms down to your heart of stone\n" +
                "You bled me dry just like the tears you never show\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "\n" +
                "I never needed anything from you\n" +
                "And all I ever asked was for the truth\n" +
                "You showed your tongue and it was forked in two\n" +
                "Your venom was lethal, I almost believed you\n" +
                "Yeah, you preyed on my every mistake\n" +
                "Waited on me to break\n" +
                "Held me under hoping I would drown\n" +
                "Like a plague, I was wasting away, tryna find my way out\n" +
                "Find my way out\n" +
                "\n" +
                "And if finally came the day\n" +
                "I start giving my heart away\n" +
                "For heaven's sakes, my bones will break\n" +
                "But you'll never own my soul, no\n" +
                "\n" +
                "I feel you crumble in my arms down to your heart of stone\n" +
                "You bled me dry just like the tears you never show\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "\n" +
                "I took them stones you threw, made chains for the crew (Ice)\n" +
                "I brought up ten hoes, this coupe only made for two (Yessir)\n" +
                "They all ran through it, it ain't nothing left to do\n" +
                "I need some more reasons to live out this evening (Straight up)\n" +
                "I been sipping forever and just taking whatever\n" +
                "Hoping, thinking whenever you'll be back around\n" +
                "Let's go our ways, whichever\n" +
                "You say how is however long\n" +
                "'Cause you know I'll never be alone\n" +
                "\n" +
                "Love\n" +
                "Shorty gone back\n" +
                "Need it on sight\n" +
                "Crack it all back\n" +
                "Give her that pipe\n" +
                "All of my gang\n" +
                "Shorty went bad\n" +
                "\n" +
                "I feel you crumble in my arms down to your heart of stone\n" +
                "You bled me dry just like the tears you never show\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "Why don't you take what you want from me?\n" +
                "Take what you need from me\n" +
                "Take what you want and go\n" +
                "\n" +
                "(Take it all away)\n" +
                "\n" +
                "Why don't you\n" +
                "(Take what you want, take what you need)\n" +
                "Take what you need from me\n" +
                "(Take what you want, take what you need)\n" +
                "Why don't you\n" +
                "Whatever you need");
        mapOfLyrics.put("I'm Gonna Be","Can you feel it?\n" +
                "Can you feel it?\n" +
                "Can you feel it?\n" +
                "Can you feel it?\n" +
                "\n" +
                "Pop the top, fill my cup up, yeah\n" +
                "Keep 'em pourin' 'til I'm fucked up, oh yeah\n" +
                "Diamonds on me with my shirt tucked, yeah\n" +
                "Mink was 80k, that's fucked up, oh yeah\n" +
                "\n" +
                "Hey, I rock that shit, but not for long\n" +
                "Then I go cop another one\n" +
                "Some people think I'm living wrong\n" +
                "We live this life, but not for long\n" +
                "\n" +
                "So I'm gonna be what I want, what I want, what I want, yeah\n" +
                "I'm gonna do what I want, when I want, when I want, yeah\n" +
                "I'm goin' hard till I'm gone, till I'm gone, till I'm gone, yeah\n" +
                "Can you feel it? Can you feel it?\n" +
                "\n" +
                "I'm gonna be what I want, what I want, what I want, yeah\n" +
                "I'm gonna do what I want, when I want, when I want, yeah\n" +
                "I'm goin' hard till I'm gone, till I'm gone, till I'm gone, yeah\n" +
                "Can you feel it? Can you feel it?\n" +
                "\n" +
                "Hey, why you so mad?\n" +
                "Never look back\n" +
                "Can't let up the gas, we moving so fast, yeah, let's make it last\n" +
                "Yeah, I'm on to you, mmm, mmm\n" +
                "You're too comfortable, aye\n" +
                "Who you talkin' to? Mmm, mmm\n" +
                "Ain't no time for you, aye\n" +
                "\n" +
                "I do what I want, ten fold on a yacht (Ooh)\n" +
                "Richard Mille my watch, thousand dollar Crocs\n" +
                "They tryna tell me that it's luck\n" +
                "You probably think I made it up\n" +
                "I got it all, it ain't enough\n" +
                "But I'm still gonna run it up\n" +
                "\n" +
                "So I'm gonna be what I want, what I want, what I want, yeah\n" +
                "I'm gonna do what I want, when I want, when I want, yeah\n" +
                "I'm goin' hard till I'm gone, till I'm gone, till I'm gone, yeah\n" +
                "Can you feel it? Can you feel it?\n" +
                "\n" +
                "I'm gonna be what I want, what I want, what I want, yeah\n" +
                "I'm gonna do what I want, when I want, when I want, yeah\n" +
                "I'm goin' hard till I'm gone, till I'm gone, till I'm gone, yeah\n" +
                "Can you feel it? Can you feel it?\n" +
                "\n" +
                "Ever since I got a chance, I been goin\n" +
                "Every chip out on the table, bitch, I'm all in\n" +
                "Aye, I'm gonna be\n" +
                "Aye, I'm gonna be\n" +
                "Bitch, I'm gonna be\n" +
                "\n" +
                "I'm gonna be what I want, what I want, what I want, yeah\n" +
                "I'm gonna do what I want, when I want, when I want, yeah\n" +
                "I'm goin' hard till I'm gone, till I'm gone, till I'm gone, yeah\n" +
                "Can you feel it? Can you feel it?\n" +
                "\n" +
                "(Can you feel it?)\n" +
                "(Can you feel it?)\n" +
                "(Can you feel it? I'm gonna be)\n" +
                "(Can you feel it?)");
        mapOfLyrics.put("Staring At The Sun","Wait\n" +
                "I know you got a lot of shit you like to say\n" +
                "(To say, to say)\n" +
                "Slow it down, think you're getting a little carried away\n" +
                "You're too close to the flame\n" +
                "But you don't wanna turn around\n" +
                "Like you got me figured out\n" +
                "But girl what I can promise is I let you down\n" +
                "So don't put up a fight, you'll get lost in the light\n" +
                "\n" +
                "If you keep staring at the Sun, you won't see\n" +
                "What you had become, this could be\n" +
                "Everything you thought it was\n" +
                "Blinded by the thought of us, so\n" +
                "Give me a chance I will fuck up again\n" +
                "I warned you in advance\n" +
                "But you just keep on staring at the Sun\n" +
                "\n" +
                "Wait\n" +
                "I know I got a lot of shit going on with me\n" +
                "Now we're free to love anyone then loving me\n" +
                "I tried to rewind and get reminded of the time when I wait for nobody\n" +
                "Can't get to close, no, let it go\n" +
                "'Cause I can count on you to let me down\n" +
                "I won't put up a fight, I got lost in the light\n" +
                "\n" +
                "If you keep staring at the Sun, you won't see\n" +
                "What you had become, this could be\n" +
                "Everything you thought it was\n" +
                "Blinded by the thought of us, so\n" +
                "Give me a chance I will fuck up again\n" +
                "I warned you in advance\n" +
                "But you just keep on staring at the Sun\n" +
                "\n" +
                "Wait\n" +
                "Got a couple little things I would like to say\n" +
                "Today is not your day\n" +
                "You should've walked away\n" +
                "But you won't listen, you just keep on\n" +
                "\n" +
                "Staring at the Sun, you won't see\n" +
                "What you had become, this could be\n" +
                "Everything you thought it was\n" +
                "Blinded by the thought of us, so\n" +
                "Give me a chance I will fuck up again\n" +
                "I warned you in advance\n" +
                "But you just keep on staring at the Sun\n");
        mapOfLyrics.put("Internet","\n" +
                "I just seen lil mama on Instagram and she flashin'\n" +
                "Don't care about your puppies, just that ass and them breasts\n" +
                "Oh girl, you a model? Damn, I never would've guessed it\n" +
                "And if you tryna throw out all them vibes, I'ma catch 'em\n" +
                "\n" +
                "The lifestyle we live is just too dangerous\n" +
                "Paranoid, since I been leakin' my shit\n" +
                "Wonder if it'll come out on the web\n" +
                "And I can't help all these bitches on my dick\n" +
                "Taking the photo and posting that shit\n" +
                "Coming home late and not callin' a bitch\n" +
                "She always be askin' me, \"Where have you been?\"\n" +
                "Whoa\n" +
                "\n" +
                "The world has gone to shit and we all know that\n" +
                "People freakin' out like, \"Get the Prozac\"\n" +
                "The one with chino shorts got all the broads, man\n" +
                "Well, fuck the internet and you can quote that, whoa\n" +
                "\n" +
                "Instalove, well if ignorance is bliss, then don't wake me up\n" +
                "And I'll probably be the last to know\n" +
                "'Cause I don't get on the internet no more\n" +
                "Instalove, well if ignorance is bliss, then don't wake me up\n" +
                "And I'll probably be the last to know\n" +
                "'Cause I don't get on the internet no more");
        mapOfLyrics.put("Goodbyes (Feat. Young Thug)","[Post Malone:]\n" +
                "Me and Kurt feel the same\n" +
                "Too much pleasure is pain\n" +
                "My girl spites me in vain\n" +
                "All I do is complain\n" +
                "She needs something to change\n" +
                "Need to take off the e-e-edge\n" +
                "So fuck it all tonight\n" +
                "And don't tell me to shut up\n" +
                "When you know you talk too much\n" +
                "But you don't got shit to say (say)\n" +
                "\n" +
                "I want you out of my head\n" +
                "I want you out of my bedroom tonight (bedroom)\n" +
                "There's no way I could save you (save you)\n" +
                "'Cause I need to be saved too\n" +
                "I'm no good at goodbyes\n" +
                "\n" +
                "We're both actin' insane\n" +
                "But too stubborn to change\n" +
                "Now I'm drinkin' again\n" +
                "80 proof in my veins\n" +
                "And my fingertips stained\n" +
                "Looking over the e-e-edge\n" +
                "Don't fuck with me tonight\n" +
                "Say you needed this heart, then you got it (got it)\n" +
                "Turns out that it wasn't what you wanted (wanted)\n" +
                "And we wouldn't let go and we lost it\n" +
                "Now I'm a goner\n" +
                "\n" +
                "I want you out of my head (head)\n" +
                "I want you out of my bedroom tonight (bedroom)\n" +
                "There's no way I could save you (save you)\n" +
                "'Cause I need to be saved too (saved too)\n" +
                "I'm no good at goodbyes\n" +
                "\n" +
                "[Young Thug:]\n" +
                "I want you out of my life\n" +
                "I want you back here tonight\n" +
                "I'm tryna cut you, no knife\n" +
                "I wanna slice you and dice you\n" +
                "My argue possessive\n" +
                "It got you precise\n" +
                "Can you not turn off the TV?\n" +
                "I'm watchin' a fight\n" +
                "I flood the garage\n" +
                "Blue diamond, no shark\n" +
                "Your Barbie life doll\n" +
                "Is Nicki Minaj\n" +
                "You don't need a key to drive\n" +
                "Your car on the charger\n" +
                "I just wanna see the side (yeah)\n" +
                "The one that's unbothered\n" +
                "And I don't want you to never go outside (outside)\n" +
                "I promise if they play, my n**** slidin' (slidin')\n" +
                "I'm fuckin' her, and the tour bus still ridin' (ridin')\n" +
                "Yeah, yeah, yeah, yeah, yeah\n" +
                "\n" +
                "[Post Malone:]\n" +
                "I want you out of my head (out of my head)\n" +
                "I want you out of my bedroom tonight\n" +
                "There's no way I could save you (save you)\n" +
                "'Cause I need to be saved too (saved too)\n" +
                "I'm no good at goodbyes\n" +
                "\n" +
                "Goodbye, goodbye, goodbye\n" +
                "Goodbye, goodbye, goodbye\n" +
                "Goodbye, goodbye, goodbye\n" +
                "I'm no good at goodbyes\n" +
                "Goodbye, goodbye, goodbye\n" +
                "Goodbye, goodbye, goodbye\n" +
                "Goodbye, goodbye, goodbye\n" +
                "I'm no good at goodbyes");
        mapOfLyrics.put("Myself","(Wish I could of been there myself)\n" +
                "'Cause what it is, is how I live\n" +
                "All the places I've been\n" +
                "I wish I could've been there myself\n" +
                "I made so much, spent so much\n" +
                "And I can't get enough\n" +
                "I wish I could've been there myself\n" +
                "(Wish I could've been there myself)\n" +
                "\n" +
                "Your second cousin lives in Orlando (Orlando)\n" +
                "And yeah I just finished a show\n" +
                "People shoving shots down their damn throats\n" +
                "I'm smiling \"Yes\", but I'm gonna say no (Gonna say no)\n" +
                "\n" +
                "'Cause what it is, is how I live\n" +
                "All the places I've been\n" +
                "I wish I could've been there myself\n" +
                "(Wish I could've been there myself)\n" +
                "I made so much, spent so much\n" +
                "And I can't get enough (Can't get enough)\n" +
                "I wish I could've been there myself\n" +
                "\n" +
                "All of this American dreaming\n" +
                "Everybody sick of believing\n" +
                "Oh, let's not give a fuck, chill\n" +
                "Giving a fuck has no meaning\n" +
                "Oh, I'm sick of believing\n" +
                "All of this American dreaming\n" +
                "Oh, let's not give a fuck, chill\n" +
                "Giving a fuck has no meaning (Oh)\n" +
                "\n" +
                "'Cause what it is, is how I live\n" +
                "All the places I've been\n" +
                "I wish I could've been there myself\n" +
                "(Wish I could've been there myself)\n" +
                "I made so much, spent so much\n" +
                "And I can't get enough (Can't get enough)\n" +
                "I wish I could've been there myself\n" +
                "(Wish I could've been there myself)\n" +
                "(No I can't get enough)\n");
        mapOfLyrics.put("I Know","Our love, will never be another\n" +
                "You're just a devil undercover\n" +
                "Found you when you were in the gutter\n" +
                "Shit was sweet until I was a sucker\n" +
                "\n" +
                "Shout out Jonas Brothers\n" +
                "I learned more than I care to discover\n" +
                "Don't you know that I'm more than a comma\n" +
                "You act up then act like it's nothing\n" +
                "Sold your soul, you Stone Cold, you a Stunner\n" +
                "\n" +
                "Every time you left, shit was never right\n" +
                "In another bed every single night\n" +
                "Had it to a science, you were so precise\n" +
                "See it in your eyes, saw you in the light\n" +
                "Somehow mami, I still want you *\n" +
                "Listen to me, don't drive away\n" +
                "Kill me softly, your hold on me is\n" +
                "Something I can't explain\n" +
                "\n" +
                "I know you could never be my bitch\n" +
                "Shit could never be like this\n" +
                "I know, stop thinking you're in my plans\n" +
                "Hundred times, you blew another chance\n" +
                "I know, you were getting down on the low\n" +
                "Then running back to me in the morning\n" +
                "I know, shit could never be like this\n" +
                "You could never be my bitch, no, no\n" +
                "\n" +
                "Our love, will never be another\n" +
                "You're just a devil undercover\n" +
                "Found you when you were in the gutter\n" +
                "Shit was sweet until I was a sucker\n" +
                "\n" +
                "Rather be single for life\n" +
                "Then be fucking with you\n" +
                "I ain't rolling the dice, no\n" +
                "I ain't playing to lose\n" +
                "\n" +
                "Every time you left, shit was never right\n" +
                "In another bed every single night\n" +
                "Had it to a science, you were so precise\n" +
                "See it in your eyes, saw you in the light\n" +
                "Somehow mami, I still want you\n" +
                "Listen to me, don't drive away\n" +
                "Kill me softly, your hold on me is\n" +
                "Something I can't explain\n" +
                "\n" +
                "I know you could never be my bitch\n" +
                "Shit could never be like this\n" +
                "I know, stop thinking you're in my plans\n" +
                "Hundred times, you blew another chance\n" +
                "I know, you were getting down on the low\n" +
                "Then running back to me in the morning\n" +
                "I know, shit could never be like this\n" +
                "You could never be my bitch, no, no\n" +
                "\n" +
                "(Be my bitch)\n" +
                "(It could never be like that)\n" +
                "(It could never be like that)\n" +
                "(It could never be like that)\n");
        mapOfLyrics.put("Must Have Been The Wind","I heard glass shatter on the wall in the apartment above mine\n" +
                "At first I thought that I was dreamin'\n" +
                "But then I heard the voice of a girl and it sounded like she'd been cryin'\n" +
                "Now I'm too worried to be sleepin'\n" +
                "\n" +
                "So I took the elevator to the second floor\n" +
                "Walked down the hall and then I knocked up on her door\n" +
                "She opened up and I asked about the things I've been hearing\n" +
                "\n" +
                "She said, \"I think your ears are playing tricks on you\"\n" +
                "Sweater zipped up to her chin\n" +
                "\"Thanks for caring, sir, that's nice of you\n" +
                "But I have to go back in\n" +
                "Wish I could tell you about the noise\n" +
                "But I didn't hear a thing\"\n" +
                "She said, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "She said, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "\n" +
                "So I was layin' on the floor of my room, cold concrete on my back\n" +
                "No, I just couldn't shake the feeling\n" +
                "I didn't want to intrude because I knew that I didn't have all the facts\n" +
                "But I couldn't bear the thought of leavin' her\n" +
                "\n" +
                "So I took the elevator to the second floor\n" +
                "Walked down the hall and then I knocked up on her door\n" +
                "She opened up and I asked about the things I've been hearing\n" +
                "\n" +
                "She said, \"I think your ears are playing tricks on you\"\n" +
                "Sweater zipped up to her chin\n" +
                "\"Thanks for caring, sir, that's nice of you\n" +
                "But I have to go back in\n" +
                "Wish I could tell you about the noise\n" +
                "But I didn't hear a thing\"\n" +
                "She said, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "She said, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "\n" +
                "Aim my boom box at the roof, I'm playing \"Lean On Me\"\n" +
                "Just so that she knows that she can lean on me\n" +
                "And when she hears the words, I hope she knows she'll be okay\n" +
                "Aim my boom box at the roof, I'm playing \"Lean On Me\"\n" +
                "Just so that she knows that she can lean on me\n" +
                "And when she hears the words, I know exactly what I'll say\n" +
                "\n" +
                "Promise I'm not playing tricks on you\n" +
                "You're always welcome to come in\n" +
                "You could stay here for an hour or two\n" +
                "If you ever need a friend\n" +
                "We can talk about the noise, when you're ready, but till then\n" +
                "I'll say, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "I'll say, \"It must have been the wind, must have been the wind\n" +
                "Must have been the wind, it must have been the wind\"\n" +
                "\n");
        mapOfLyrics.put("do u even miss me at all?","Do you even miss me at all?\n" +
                "When I post that video of women on my snapchat\n" +
                "Only really tryna get a way to see you call\n" +
                "Do you even miss me at all?\n" +
                "Cause' I need that thing from you\n" +
                "Like I'm the only one in this game for two\n" +
                "Hope that you'd be there when I fall\n" +
                "Do you even miss me at all?\n" +
                "Do you even miss me at all?\n" +
                "Do you even miss me at all?\n" +
                "Cause' I need that thing from you\n" +
                "Like I'm the only one in this game for two\n" +
                "Hope that you'd be there when I fall\n" +
                "Do you even miss me at all?\n" +
                "\n" +
                "Yea, girl you say you love me\n" +
                "Then you go behind my back and saying fuck me\n" +
                "Coulda pay your ass a brawl but you lucky\n" +
                "You should say there's no one else above me\n" +
                "(But I can't get a text back though)\n" +
                "Girl I used to love you\n" +
                "Now all the shit you do make it hard to trust you\n" +
                "Stab me in the back know it cut deep\n" +
                "You saying fuck me, I'm saying fuck you\n" +
                "All your misery I don't wanna know that\n" +
                "See me on IG, asking where the hoes at\n" +
                "I can have another you by the morning\n" +
                "Used to kill that pussy, go and get a toe tag\n" +
                "Aye fuck it, tell me don't you love it\n" +
                "Once you leave bitches chasing cause' the way I run it\n" +
                "High key coulda made you wifey\n" +
                "One of one, no one else like me\n" +
                "\n" +
                "Do you even miss me at all?\n" +
                "When I post that video of women on my snapchat\n" +
                "Only really tryna get a way to see you call\n" +
                "Do you even miss me at all?\n" +
                "Do you even miss me at all?\n" +
                "Do you even miss me at all?\n" +
                "Cause' I need that thing from you\n" +
                "Like I'm the only one in this game for two\n" +
                "Hoping you'd be there when I fall\n" +
                "Do you even miss me at all?\n" +
                "\n" +
                "Girl we had plans\n" +
                "Then you called me out, I don't really understand\n" +
                "Used to all about the romance\n" +
                "Now I'm steady wondering if you're with another man\n" +
                "Cause' I still thinking bout' the things you do\n" +
                "Forgetting everything that we've been through\n" +
                "But do you need me, like I need you?\n" +
                "Don't tell me that you care about me if it ain't true\n" +
                "\n" +
                "Do you even miss me at all?\n" +
                "When I post that video of women on my snapchat\n" +
                "Only really tryna get a way to see you call\n" +
                "Do you even miss me at all?\n" +
                "Cause' I need that thing from you\n" +
                "Like I'm the only one in this game for two\n" +
                "Hope that you'd be there when I fall\n" +
                "Do you even miss me at all? (Do you really miss me baby)\n" +
                "Do you even miss me at all? (God no no no no no no)\n" +
                "Do you even miss me at all?\n" +
                "Cause' I need that thing from you\n" +
                "Like I'm the only one in this game for two\n" +
                "Hope that you'd be there when I fall\n" +
                "Do you even miss me at all?");
        mapOfLyrics.put("5 shots","[Gianni:]\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Yeah yeah\n" +
                "\n" +
                "Coming through late\n" +
                "Homie sending addy so you know I'm on the way\n" +
                "Aw yeah\n" +
                "Pull up to the gate\n" +
                "You already buzzing I can see it in your face\n" +
                "Aw yeah\n" +
                "Mixing Coke with that Hennessy, keeping you company\n" +
                "1, 2, 3, 4, 5 shots with me and I know where you oughta be\n" +
                "Yeah yeah\n" +
                "And you ain't gotta pretend\n" +
                "If we keep it going, we both know how it ends\n" +
                "Know what's up cause I already saw you ditching your friends\n" +
                "But is it even worth the time that I spend\n" +
                "Aw no\n" +
                "Man, what am I thinking?\n" +
                "I say we call it, you say keep drinking\n" +
                "Only here for one night then baby I'm leaving\n" +
                "Thinking that it's done but we don't mean it\n" +
                "Aw yeah\n" +
                "\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Sheesh\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Yeah yeah\n" +
                "\n" +
                "[Kyle:]\n" +
                "Now we lit, pour it up, fuck a sip\n" +
                "Girl, I fuck with you a lot\n" +
                "Come and fuck with me a bit\n" +
                "Drunk as fuck, seeing triple or are you just really thick?\n" +
                "Know you dancing on that Henny\n" +
                "Can you dance up on a (di-), on a dick?\n" +
                "Shake that ass yeah, throw it back yeah\n" +
                "You and I are a perfect match yeah, off the Jack yeah\n" +
                "Drinking hour after hour yeah, whiskey sour yeah\n" +
                "Then we end up on the floor yeah\n" +
                "Then the bed then the shower yeah\n" +
                "Woo\n" +
                "Aye I got tunnel vision\n" +
                "Liquor keep on multiplying like we never learned about division\n" +
                "Beat it up, put that ass in submission\n" +
                "I done wrote the Kama Sutra, I can show you every sex position\n" +
                "Woo\n" +
                "You on my mind, fuck around and turn that water into wine\n" +
                "The night is young but we running out of time\n" +
                "Even though we can't drive, I can take you on a ride\n" +
                "Skrt, skrt\n" +
                "Aye\n" +
                "\n" +
                "[Gianni and Kyle:]\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Sheesh\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Yeah yeah\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Imma need a second\n" +
                "Woo\n" +
                "All this liquor got me second guessing\n" +
                "Yeah yeah\n" +
                "Now we 5 shots deep\n" +
                "Now we 1, 2, 3, 4, 5 shots deep\n" +
                "Yeah yeah\n");
        mapOfLyrics.put("talk is overrated","\n" +
                "[Jeremy Zucker:]\n" +
                "I don't wanna smoke, I don't need a drink\n" +
                "Just tell me how you feel, tell me what you think\n" +
                "'Cause I've been on my own for a fuckin' while\n" +
                "And I don't need a girl, I just wanna smile\n" +
                "Gettin' my mind right\n" +
                "I'll wait 'til the time's right\n" +
                "I'm meanin' to tell you\n" +
                "Why it's hard to sleep at night\n" +
                "There's nothin' to fear now\n" +
                "Girl, we should be here now\n" +
                "So why don't you hear me out?\n" +
                "I'm saying\n" +
                "\n" +
                "Talk is overrated, let's just vibe\n" +
                "And love is overrated in my mind\n" +
                "Girl, talk is overrated, let's just vibe\n" +
                "Just for tonight\n" +
                "I'll be yours if you want me to\n" +
                "I'll be yours if you want me to\n" +
                "\n" +
                "I've been hella stressed, I would rather chill\n" +
                "I know you looked at me, wonder how I deal\n" +
                "But look inside my soul, I don't mean to front\n" +
                "'Cause really I don't know what the fuck I want\n" +
                "I remember when we were more than friends\n" +
                "I would just pretend, that was cold\n" +
                "After all this shit, I could not let you in\n" +
                "Summer went, saved up all the money\n" +
                "That I would have spent\n" +
                "On you, girl\n" +
                "\n" +
                "Talk is overrated, let's just vibe\n" +
                "And love is overrated in my mind\n" +
                "Girl, talk is overrated, let's just vibe\n" +
                "Just for tonight\n" +
                "I'll be yours if you want me to\n" +
                "I'll be yours if you want me to\n" +
                "\n" +
                "[Blackbear:]\n" +
                "Yeah, let's talk it out\n" +
                "Girl, time out\n" +
                "I know that you only with me for the clout, sit down\n" +
                "You had too much to drink\n" +
                "Maybe it's my fault\n" +
                "I remember you would pull up, pour up Hennessy askin' to smoke\n" +
                "Oh no, there you go again\n" +
                "Mix and blow it down\n" +
                "Brand new hoes around, yeah\n" +
                "And when the night is done\n" +
                "You be hittin' my line\n" +
                "Tell me your phone at one percent\n" +
                "3 a.m., you wanna vibe\n" +
                "\n" +
                "[Jeremy Zucker:]\n" +
                "Talk is overrated, let's just vibe\n" +
                "And love is overrated in my mind\n" +
                "Girl, talk is overrated, let's just vibe\n" +
                "Just for tonight\n" +
                "I'll be yours if you want me to\n" +
                "I'll be yours if you want me to");
        mapOfLyrics.put("Swim","I've been falling much more deep\n" +
                "Than I wanna\n" +
                "I've been wishing I could breathe\n" +
                "Underwater\n" +
                "I hold my breath\n" +
                "I can't see what comes next\n" +
                "I don't know when\n" +
                "I'll see dry land again\n" +
                "\n" +
                "Another 40 days I'm lost at sea\n" +
                "I'm just gonna swim until you love me\n" +
                "Hoping that your heart will rescue me\n" +
                "I'm just gonna swim until you love me\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "\n" +
                "I've been drowning in these sheets\n" +
                "Feeling lonely\n" +
                "Wishing you were here with me\n" +
                "Every morning\n" +
                "Over my head\n" +
                "The tide comes rolling in\n" +
                "I don't know when\n" +
                "I'll see dry land again\n" +
                "\n" +
                "Another 40 days I'm lost at sea\n" +
                "I'm just gonna swim until you love me\n" +
                "Hoping that your heart will rescue me\n" +
                "I'm just gonna swim until you love me\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "\n" +
                "I will never get over you\n" +
                "\n" +
                "Another 40 days I'm lost at sea\n" +
                "I'm just gonna swim until you love me\n" +
                "Hoping that your heart will rescue me\n" +
                "I'm just gonna swim until you love me\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "Swim until you love me\n" +
                "Swim, swim, swim\n" +
                "\n" +
                "I will never get over you\n" +
                "\n");
        mapOfLyrics.put("Beachside","\n" +
                "[Jackson Breit:]\n" +
                "My oh my\n" +
                "Summer days, sun rays\n" +
                "I'm feelin' it's all right on time\n" +
                "For you, and me\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "\n" +
                "[Bazanji:]\n" +
                "Yeah, I've been livin' like the summer days\n" +
                "Sunny weather never rains\n" +
                "Goin' out every night\n" +
                "Meet another girl another name\n" +
                "Runnin' my city they knowing it's me (ok)\n" +
                "Doin' the show they be comin' to see (uh huh)\n" +
                "Makin' the bread and I'm packin' it up (ok)\n" +
                "Then I walk in the bar and all drinks are free! (ok)\n" +
                "\n" +
                "N-no I'll never change up\n" +
                "I've been sticking with the day ones\n" +
                "Getting bigger since that day one\n" +
                "And one day go 'round the nation\n" +
                "They know the flow from Germany to Tokyo\n" +
                "Gettin' nosy like Pinocchio\n" +
                "I'm chasin' all these dreams\n" +
                "And y'all still busy chasin' hoes\n" +
                "\n" +
                "It's all right\n" +
                "No class we up all night\n" +
                "Sun's out, and the girl's out\n" +
                "In the sun dress, they look nice (What?)\n" +
                "Friday goin' out, (Okay) Saturday do it again (Yeah)\n" +
                "Never take a day off (Yeah)\n" +
                "Every night is like the weekend\n" +
                "\n" +
                "[Jackson Breit:]\n" +
                "My oh my\n" +
                "Summer days, sun rays\n" +
                "I'm feelin' it's all right on time\n" +
                "For you, and me\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "\n" +
                "Got sweat up on the brow\n" +
                "Chillin' with the turtle\n" +
                "I told 'er what momma don't know won't hurt her\n" +
                "But I, knew she was bad in the summer time\n" +
                "(Yeah, uh huh)\n" +
                "But back to how it goes\n" +
                "Three hours by the cold\n" +
                "Sand sticking to our body drop top on the row\n" +
                "Let your hair down long blonde\n" +
                "She got them jeans on\n" +
                "Tell her when she's long gone\n" +
                "I've got the goodies for you\n" +
                "(Yeah, yeah yeah)\n" +
                "I've got the goodies for you now\n" +
                "Give it to me now\n" +
                "Give it to me now\n" +
                "Give it to me now\n" +
                "Give it to me now\n" +
                "\n" +
                "My oh my\n" +
                "Summer days, sun rays\n" +
                "I'm feelin' it's all right on time\n" +
                "For you, and me\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "\n" +
                "[Bazanji:]\n" +
                "I'm getting noted all my homies makin' money\n" +
                "And I'm tryna chase a dream but everybody tryna stop me\n" +
                "They been sayin' it ain't worth it and I gotta work a job\n" +
                "But I hit a hundred thousand now they tryna tag along\n" +
                "\n" +
                "I'mma live it every day, I'mma live it till my grave\n" +
                "I'mma live it like the summer, hope this life won't fade away\n" +
                "I've been rollin' round my city with the music up, windows down\n" +
                "Pull up to the party and we pourin' up another round. (Okay)\n" +
                "\n" +
                "Pour the shots and bottoms up, in the pool and pass a cup\n" +
                "Got your girl? Pass 'er up. Take the Uber to the club\n" +
                "Know we stayin' up all night, know the drink's on me tonight\n" +
                "We gon' party like our last you know everything's alright\n" +
                "\n" +
                "[Jackson Breit:]\n" +
                "My oh my\n" +
                "Summer days, sun rays\n" +
                "I'm feelin' it's all right on time\n" +
                "For you, and me\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n" +
                "(Yeah, yeah, yeah)\n" +
                "Beachside chillin' I'm gone\n");
        mapOfLyrics.put("jealous","[Gianni:]\n" +
                "Don't know what's come over me\n" +
                "Feels like I can't take the heat\n" +
                "It's hard to ignore all this pictures on my timeline\n" +
                "When it's really all I see\n" +
                "\n" +
                "It's like why you going out so much\n" +
                "Like it's not already hard enough\n" +
                "How you taking all my love\n" +
                "Act like I'm not what you're thinking of\n" +
                "\n" +
                "No time for complements but now I'm fishin'\n" +
                "On some type of drug, you my addiction\n" +
                "Like I only need you in addition\n" +
                "Tryna know what's up, yeah that's the mission\n" +
                "\n" +
                "And no I ain't that type of guy, but for some reason\n" +
                "No, I just can't help myself\n" +
                "And now I just keep asking why\n" +
                "Why I'm thinking that you're out with someone else\n" +
                "\n" +
                "[Gianni & Kyle:]\n" +
                "I got henny in the freezer\n" +
                "Say you comin' through\n" +
                "Cause I hate it that I need ya\n" +
                "Know you feel it too\n" +
                "\n" +
                "I got henny in the freezer\n" +
                "Say you comin' through\n" +
                "Cause I hate it that I need ya\n" +
                "Know you feel it too\n" +
                "\n" +
                "[Gianni:]\n" +
                "I get jealous\n" +
                "When I see you ain't at home\n" +
                "I get jealous\n" +
                "When you don't pick up the phone\n" +
                "I get jealous\n" +
                "But you tell me let it go\n" +
                "Something's up I can tell\n" +
                "Girl, you're see through\n" +
                "\n" +
                "I get jealous\n" +
                "When I see you ain't at home\n" +
                "I get jealous\n" +
                "When you don't pick up the phone\n" +
                "I get jealous\n" +
                "But you tell me let it go\n" +
                "Something's up I can tell\n" +
                "Girl, you're see through\n" +
                "\n" +
                "[Kyle:]\n" +
                "Yeah, I been thinking\n" +
                "How come we only meet on the weekend\n" +
                "How come we only text when we drinking\n" +
                "How can you tell me go rap about bitches but really it's my song you singing\n" +
                "You and me float but we really just sinking\n" +
                "Okay so tell me the reason we playing ourselves and we keeping these secrets, Yeah\n" +
                "Okay so I tell the truth. Yeah\n" +
                "I stay a hundred with you\n" +
                "Like tell me what what we got to do, Yeah\n" +
                "What else do we got to prove\n" +
                "You and me moving but we ain't got no fucking moves yeah\n" +
                "Should be winnin', but we lose, yeah\n" +
                "Under influence and booze and liquor is the only time I'm coming through, yeah\n" +
                "\n" +
                "Ay, how you manage what your plan is\n" +
                "Fucked around and got my heart around where your hand is\n" +
                "Ay, I been thinking hard and you got my head sprung\n" +
                "Ay, when I ain't got you these other bitches stand ins\n" +
                "\n" +
                "Let's be honest\n" +
                "Talkin' about my money, girl you want it\n" +
                "Up to speed and now we supersonic\n" +
                "I know you've been thinking everything\n" +
                "Let's take a break from the gin 'n' tonic\n" +
                "I'll get the bottle and pour out the shots\n" +
                "Maybe the chemistry feeling the same\n" +
                "I know that you and I been through a lot\n" +
                "Send my location and you on the way, sheesh\n" +
                "\n" +
                "[Gianni & Kyle:]\n" +
                "I got henny in the freezer\n" +
                "Say you comin' through\n" +
                "Cause I hate it that I need ya\n" +
                "Know you feel it too\n" +
                "\n" +
                "I got henny in the freezer\n" +
                "Say you comin' through\n" +
                "Cause I hate it that I need ya\n" +
                "Know you feel it too\n" +
                "\n" +
                "[Gianni:]\n" +
                "I get jealous\n" +
                "When I see you ain't at home\n" +
                "I get jealous\n" +
                "When you don't pick up the phone\n" +
                "I get jealous\n" +
                "But you tell me let it go\n" +
                "Something's up I can tell\n" +
                "Girl, you're see through\n" +
                "\n" +
                "I get jealous\n" +
                "When I see you ain't at home\n" +
                "I get jealous\n" +
                "When you don't pick up the phone\n" +
                "I get jealous\n" +
                "But you tell me let it go\n" +
                "Something's up I can tell\n" +
                "Girl, you're see through");
        mapOfLyrics.put("Lost Boy","There was a time when I was alone\n" +
                "Nowhere to go and no place to call home\n" +
                "My only friend was the man in the moon\n" +
                "And even sometimes he would go away too\n" +
                "\n" +
                "Then one night, as I closed my eyes\n" +
                "I saw a shadow flying high\n" +
                "He came to me with the sweetest smile\n" +
                "Told me he wanted to talk for awhile\n" +
                "He said, \"Peter Pan. That's what they call me\n" +
                "I promise that you'll never be lonely\"\n" +
                "And ever since that day\n" +
                "\n" +
                "I am a lost boy from Neverland\n" +
                "Usually hanging out with Peter Pan\n" +
                "And when we're bored we play in the woods\n" +
                "Always on the run from Captain Hook\n" +
                "\"Run, run, lost boy,\" they say to me\n" +
                "\"Away from all of reality\"\n" +
                "\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n" +
                "\n" +
                "He sprinkled me in pixie dust and told me to believe\n" +
                "Believe in him and believe in me\n" +
                "Together we will fly away in a cloud of green\n" +
                "To your beautiful destiny\n" +
                "As we soared above the town that never loved me\n" +
                "I realized I finally had a family\n" +
                "Soon enough we reached Neverland\n" +
                "Peacefully my feet hit the sand\n" +
                "And ever since that day\n" +
                "\n" +
                "I am a lost boy from Neverland\n" +
                "Usually hanging out with Peter Pan\n" +
                "And when we're bored we play in the woods\n" +
                "Always on the run from Captain Hook\n" +
                "\"Run, run, lost boy,\" they say to me\n" +
                "\"Away from all of reality.\"\n" +
                "\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n" +
                "\n" +
                "Peter Pan, Tinker Bell, Wendy Darling\n" +
                "Even Captain Hook\n" +
                "You are my perfect story book\n" +
                "Neverland, I love you so\n" +
                "You are now my home sweet home\n" +
                "Forever a lost boy at last\n" +
                "\n" +
                "Peter Pan, Tinker Bell, Wendy Darling\n" +
                "Even Captain Hook\n" +
                "You are my perfect story book\n" +
                "Neverland, I love you so\n" +
                "You are now my home sweet home\n" +
                "Forever a lost boy at last\n" +
                "And for always I will say\n" +
                "\n" +
                "I am a lost boy from Neverland\n" +
                "Usually hanging out with Peter Pan\n" +
                "And when we're bored we play in the woods\n" +
                "Always on the run from Captain Hook\n" +
                "\"Run, run, lost boy,\" they say to me\n" +
                "\"Away from all of reality\"\n" +
                "\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n" +
                "Neverland is home to lost boys like me\n" +
                "And lost boys like me are free\n");
        mapOfLyrics.put("Homemade Dynamite - REMIX","[Lorde:]\n" +
                "A couple rebel top gun pilots\n" +
                "Flyin' with nowhere to be, oh\n" +
                "Don't know you super well\n" +
                "But I think that you might be the same as me\n" +
                "Behave abnormally\n" +
                "\n" +
                "Let's let things come out of the woodwork\n" +
                "I'll give you my best side, tell you all my best lies\n" +
                "Yeah, awesome right?\n" +
                "So let's let things come out of the woodwork\n" +
                "I'll give you my best side, tell you all my best lines\n" +
                "Seeing me rollin', showin' someone else love\n" +
                "Dancin' with our shoes off\n" +
                "Know I think you're awesome, right?\n" +
                "\n" +
                "Our rules, our dreams, we're blind\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Our friends, our drinks, we get inspired\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "\n" +
                "[Khalid:]\n" +
                "I wanna feel the light, pushin' our limits\n" +
                "Unbuckled for the ride, mhmm\n" +
                "We're way too far from home\n" +
                "Let's be honest with ourselves, we're way too high to drive\n" +
                "So let's take on the night\n" +
                "If the light is in the air\n" +
                "Open, finally, we're goin' and we're free\n" +
                "\n" +
                "[Khalid & Lorde:]\n" +
                "Our rules, our dreams, we're blind\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Our friends, our drinks, we get inspired\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "\n" +
                "[SZA:]\n" +
                "I checked your girl at the door\n" +
                "I sent your friend to the store\n" +
                "It's only me and you, finally us two\n" +
                "And I don't regret drinkin' this liquor, makin' you listen\n" +
                "Yeah, I know you don't know me well\n" +
                "My girl's at the door\n" +
                "And I left my pretense at home\n" +
                "And it ain't no goin' back (goin' back)\n" +
                "\n" +
                "[SZA & Lorde:]\n" +
                "Our rules, our dreams, we're blind\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Our friends, our drinks, we get inspired\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "\n" +
                "[Post Malone:]\n" +
                "Walk, never fall, but we still run\n" +
                "So pour up, pour up another one\n" +
                "I know, I know we on to somethin'\n" +
                "We got, we got a loaded gun, yeah\n" +
                "Go and shoot me the look in your eyes\n" +
                "And you're here, I'm yours, you're mine\n" +
                "Find each other when we're losin' our minds\n" +
                "Then we take it all off\n" +
                "You're a runaway train, yeah\n" +
                "And you got me so faded\n" +
                "And I don't wanna chase it, no\n" +
                "You know I think you're awesome, right?\n" +
                "\n" +
                "[Post Malone, Lorde, SZA, Khalid:]\n" +
                "Our rules, our dreams, we're blind\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Our friends, our drinks, we get inspired\n" +
                "Blowin' shit up with homemade d-d-d-dynamite, yeah, yeah\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Blowin' shit up with homemade d-d-d-dynamite\n" +
                "Blowin' shit up\n" +
                "\n" +
                "[Lorde:]\n" +
                "Now you know it's really gonna blow\n");
        mapOfLyrics.put("Money Made Me Do It","Diamonds in my chain gold\n" +
                "Every 20 minutes change clothes\n" +
                "I had a mil before the label\n" +
                "Just as long as they know, money made me do it\n" +
                "I said rest in peace to Bankroll\n" +
                "You in a better place dog\n" +
                "You won't ever see me lay low\n" +
                "Start the engine watch me take off, money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "Watch me, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "\n" +
                "Take a ride when we wake up\n" +
                "Hit the gas melt your face off\n" +
                "Spend the day counting cake up\n" +
                "With a dime who think I changed up\n" +
                "I took the Lincoln down Rodeo\n" +
                "Ran through it like some Drain-o\n" +
                "Lots of bags can't complain though\n" +
                "We was shopping until they closed\n" +
                "Why did I do that? Did I do that? Ooh yeah\n" +
                "New whip now I can pay my rent, ooh yeah\n" +
                "\n" +
                "Diamonds in my chain gold\n" +
                "Every 20 minutes change clothes\n" +
                "I had a mil before the label\n" +
                "Just as long as they know, money made me do it\n" +
                "I said rest in peace to Bankroll\n" +
                "You in a better place dog\n" +
                "You won't ever see me lay low\n" +
                "Start the engine watch me take off, money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "Watch me, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "\n" +
                "[2 Chainz:]\n" +
                "Got a bandana round my neck\n" +
                "Like Bankroll Fresh\n" +
                "Pocket full of street money\n" +
                "Bout to count my blessing\n" +
                "One hundred, one thousand\n" +
                "100K, one million\n" +
                "We smoke, we lay up, we rap\n" +
                "We talked about a vision (switch it up)\n" +
                "I got diamonds like a rainbow, every time I change clothes\n" +
                "Shawty fucked my friend but I act like I ain't know\n" +
                "I'ma buy a rain coat, the storm might approach you\n" +
                "Clown like your daddy met your mom at a circus\n" +
                "I do on purpose, do it for the neighborhood\n" +
                "Smoking on that Meagan Good, with my n**** when I do it\n" +
                "Got that leather and that wood, got the marble on the floor\n" +
                "Got the burglar bars on the door at the bando\n" +
                "\n" +
                "Diamonds in my chain gold\n" +
                "Every 20 minutes change clothes\n" +
                "I had a mil before the label\n" +
                "Just as long as they know, money made me do it\n" +
                "I said rest in peace to Bankroll\n" +
                "You in a better place dog\n" +
                "You won't ever see me lay low\n" +
                "Start the engine watch me take off, money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it\n" +
                "Watch me, yeah, I said money made me do it\n" +
                "I said, yeah, I said money made me do it");
        mapOfLyrics.put("Came Up","[Hook - Post Malone:]\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n" +
                "\n" +
                "[Verse 1 - Key:]\n" +
                "I don't even worry about tomorrow\n" +
                "It might be some money it might be a hoe\n" +
                "I don't where I'm a be tomorrow\n" +
                "Might be counting dough, in bed with your hoe\n" +
                "Ok her nose keep running and the money keep coming\n" +
                "I'm a keep balling hey, Rucker Park Spalding hey\n" +
                "Care free I've be leaving care free\n" +
                "So much kush in the air we can barely even breathe\n" +
                "I really worry about things I don't know\n" +
                "You say I'm living wrong you ain't wrong, money long\n" +
                "So think about it it ain't nothing I don't know\n" +
                "All the money that I saw I don't think I'm ever wrong\n" +
                "\n" +
                "[Hook - Post Malone:]\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n" +
                "\n" +
                "[Verse 2 - Post Malone:]\n" +
                "Don't bother, bitch don't bother\n" +
                "I just left the mall balling hard like Jordan\n" +
                "Out in Fairfax I was smoking Marlboro\n" +
                "Bitch we so high, bitch I'm high as Ayatollah\n" +
                "Bitch I'm three high, bitch I'm high as Ayatollah\n" +
                "She don't want that white coca I ain't talking soda\n" +
                "Bitch ask me do you love me I'm like what I told ya\n" +
                "As I woke up I was smoking on that strong again\n" +
                "That forty keep it funky, I keep it funky\n" +
                "Man I'm smoking on that potent, that's unimportant\n" +
                "I just wanted her to fuck me, need her to fuck me\n" +
                "And she just want all of my money all my fucking money\n" +
                "\n" +
                "[Hook - Post Malone:]\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know where I go tomorrow\n" +
                "Bitch I came up and I don't know\n" +
                "I don't know what's gonna come tomorrow\n");
        mapOfLyrics.put("Up There","Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "\n" +
                "When I took a ride in the dead of night\n" +
                "Told myself that everything was okay\n" +
                "Try to understand how you standin' over me\n" +
                "Girl, it's drivin' me crazy\n" +
                "Yeah, it's freezin' fuckin' cold in the dead of night\n" +
                "The only heat, it come from the ashtray\n" +
                "I might as well get high as hell and just keep actin' like\n" +
                "Everything doesn't phase me\n" +
                "\n" +
                "Yeah, take me all the way to the top, baby\n" +
                "Don't even if I tell you to stop, baby\n" +
                "Take me all the way to the sky, baby, baby\n" +
                "\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Actin' like I got it all figured out\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Must be better than the hell on the ground\n" +
                "\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "Woo-ooh-ooh-ooh-ooh\n" +
                "\n" +
                "It's freezin' fuckin' cold in the dead of night\n" +
                "And I'ma drop the top with no Rogaine (skrrt, skrrt, skrrt)\n" +
                "And now might sound irrelevant but I prefer to stack my chips\n" +
                "When everything fallin' (backwoods)\n" +
                "And I ain't religious but I look into the sky\n" +
                "And pray to anyone holy\n" +
                "I been drinkin' way too much and man I got the blues\n" +
                "Because my baby don't hold me\n" +
                "\n" +
                "Yeah, take me all the way to the top, baby\n" +
                "Don't even if I tell you to stop, baby\n" +
                "Take me all the way to the sky, baby, baby\n" +
                "\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Actin' like I got it all figured out\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Must be better than the hell on the ground\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Actin' like I got it all figured out\n" +
                "I wanna go up there\n" +
                "And I don't ever wanna come down\n" +
                "I wanna see what's up there\n" +
                "Must be better than the hell on the ground");
        mapOfLyrics.put("Jackie Chan","[Post Malone:]\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "\n" +
                "[Preme:]\n" +
                "Drop top, how we rollin' down on Collins South Beach (yeah)\n" +
                "Look like Kelly rowland, this might be my destiny (yeah)\n" +
                "She want me to eat it, I guess dinner's on me (I got you, babe)\n" +
                "Know I got the sauce like a fuckin' recipe (ohh)\n" +
                "She just wanna do it for the 'Gram (you know, you know)\n" +
                "She just want this money in my hand (I know, you know)\n" +
                "I'ma give it to her when she dance, dance, dance (ayy)\n" +
                "She gon' catch an Uber out to Calabasas\n" +
                "\n" +
                "[Post Malone:]\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "\n" +
                "[Preme:]\n" +
                "I think you got the wrong impression 'bout me, baby ('bout me, baby)\n" +
                "Just 'cause they heard what hood I'm from they think I'm crazy (they think I'm crazy)\n" +
                "Okay, well maybe just a little crazy (just a little)\n" +
                "'Cause I admit I'm crazy 'bout that lady, yeah (oh)\n" +
                "Finger to the world, it's fuck you, pay me (I been slayin')\n" +
                "Run the pussy 'cause I'm runnin' out of patience\n" +
                "No more waitin' no, no (ayy)\n" +
                "Bouncin' like a yo-yo (ayy)\n" +
                "Livin' life on fast forward but we fuck in slow mo', yeah\n" +
                "\n" +
                "[Post Malone:]\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n" +
                "\n" +
                "I can't wait for the show, oh, oh\n" +
                "Got that good, yeah, I know, oh, oh\n" +
                "You should not be alone, oh, oh\n" +
                "All this drink got me throwed, oh, oh\n" +
                "Club got me right\n" +
                "And I feel so alive, ayy\n" +
                "She don't want a thing\n" +
                "She don't wanna be no wife\n" +
                "She just wanna stay all night\n" +
                "She just wanna sniff the white\n" +
                "Can't tell her nothing, no\n" +
                "Can't tell her nothing, no\n" +
                "\n" +
                "She say she's too young, don't want no man\n" +
                "So she gon' call her friends, now that's a plan\n" +
                "I just ordered sushi from Japan\n" +
                "Now your bitch wanna kick it, Jackie Chan\n");
        mapOfLyrics.put("Jesus In LA","Well, I shook hands with the devil\n" +
                "Down on the south side\n" +
                "And he bought us both a drink\n" +
                "With a pad and a pencil sat by his side\n" +
                "I said, \"Tell me what you think\"\n" +
                "\n" +
                "I've been looking for my savior, looking for my truth\n" +
                "I even asked my shrink\n" +
                "He brought me down to his level\n" +
                "Said, \"Son, you're not special\n" +
                "You won't find him where you think\"\n" +
                "\n" +
                "You won't find him down on Sunset\n" +
                "Or at a party in the hills\n" +
                "At the bottom of the bottle\n" +
                "Or when you're tripping on some pills\n" +
                "When they sold you the dream you were just 16\n" +
                "Packed a bag and ran away\n" +
                "And it's a crying shame you came all this way\n" +
                "'Cause you won't find Jesus in LA\n" +
                "And it's a crying shame you came all this way\n" +
                "'Cause you won't find Jesus in LA\n" +
                "\n" +
                "Took a sip of his whiskey\n" +
                "Said, \"Now that you're with me\n" +
                "Well, I think that you should stay\"\n" +
                "Yeah, I know you've been busy\n" +
                "Searching through the city\n" +
                "So let me share the way\n" +
                "\n" +
                "I know I'm not your savior\n" +
                "Know I'm not your truth\n" +
                "But I think we could be friends\n" +
                "He said \"Come down to my level\n" +
                "Hang out with the devil\n" +
                "Let me tell you, in the end...\n" +
                "\n" +
                "You won't find him down on sunset\n" +
                "Or at a party in the hills\n" +
                "At the bottom of the bottle\n" +
                "Or when you're tripping on some pills\n" +
                "When they sold you the dream you were just 16\n" +
                "Packed a bag and ran away\n" +
                "And it's a crying shame you came all this way\n" +
                "'Cause you won't find Jesus in LA\n" +
                "And it's a crying shame you came all this way\n" +
                "'Cause you won't find Jesus in LA\"\n" +
                "\n" +
                "And that is when I knew that it was time to go home\n" +
                "And that is when I realized that I was alone\n" +
                "And all the vibrant colors from the lights fade away\n" +
                "And I don't care what they say\n" +
                "\n" +
                "You won't find him down on sunset\n" +
                "Or at a party in the hills\n" +
                "At the bottom of the bottle\n" +
                "Or when you're tripping on some pills\n" +
                "When they sold you the dream you were just 16\n" +
                "Packed a bag and ran away\n" +
                "And it's a crying shame you came all this way\n" +
                "'Cause you won't find Jesus in LA\n" +
                "I won't find him down on sunset\n" +
                "Or at a party in the hills\n" +
                "At the bottom of the bottle\n" +
                "Or when I'm tripping on some pills\n" +
                "When they sold me the dream I was just 16\n" +
                "Packed my bag and ran away\n" +
                "And it's a crying shame I came all this way\n" +
                "'Cause I won't find Jesus in LA\n" +
                "And it's a crying shame I came all this way\n" +
                "'Cause I won't find Jesus in LA\n");
        mapOfLyrics.put("Stan - Recorded at Spotify Studios NYC","My tea's gone cold\n" +
                "I'm wondering why I got out of bed at all\n" +
                "The morning rain clouds up my window\n" +
                "And I can't see at all\n" +
                "And even if I could it'd all be gray\n" +
                "I put your picture on that wall\n" +
                "It reminds me that it's not so bad, it's not so bad\n" +
                "\n" +
                "Dear Slim, I wrote you, but you still ain't callin'\n" +
                "I left my cell, my pager and my home phone at the bottom\n" +
                "I sent two letters back in autumn, you must not've got 'em\n" +
                "There probably was a problem at the post office or somethin'\n" +
                "Sometimes I scribble addresses too sloppy when I jot 'em\n" +
                "But anyways, fuck it, what's been up, man? How's your daughter?\n" +
                "My girlfriend's pregnant too, I'm 'bout to be a father\n" +
                "If I have a daughter, guess what I'ma call her?\n" +
                "I'ma name her Bonnie\n" +
                "I read about your Uncle Ronnie too, I'm sorry\n" +
                "I had a friend kill himself over some bitch who didn't want him\n" +
                "I know you probably hear this every day, but I'm your biggest fan\n" +
                "I even got the underground shit that you did with Skam\n" +
                "I got a room full of your posters and your pictures, man\n" +
                "I like the shit you did with Rawkus too, that shit was phat\n" +
                "Anyways, I hope you get this, man, hit me back\n" +
                "Just to chat, truly yours, your biggest fan, this is Stan\n" +
                "\n" +
                "My tea's gone cold\n" +
                "I'm wondering why I got out of bed at all\n" +
                "The morning rain clouds up my window\n" +
                "And I can't see at all\n" +
                "And even if I could it'd all be gray\n" +
                "I put your picture on that wall\n" +
                "It reminds me that it's not so bad, it's not so bad\n" +
                "\n" +
                "Dear Slim, you still ain't called or wrote, I hope you have a chance\n" +
                "I ain't mad, I just think it's fucked up you don't answer fans\n" +
                "If you don't wanna to talk to me outside your concert\n" +
                "You didn't have to\n" +
                "But you coulda signed an autograph for Matthew\n" +
                "That's my little brother, man, he's only six years old\n" +
                "We waited in the blisterin' cold\n" +
                "For you, for four hours, and you just said no\n" +
                "That's pretty shitty, man, you're like his fuckin' idol\n" +
                "He wants to be just like you, man, he likes you more than I do\n" +
                "I ain't that mad, though I just don't like being lied to\n" +
                "Remember when we met in Denver?\n" +
                "You said if I'd write you, you would write back\n" +
                "See, I'm just like you in a way: I never knew my father neither\n" +
                "He used to always cheat on my mom and beat her\n" +
                "I can relate to what you're sayin' in your songs\n" +
                "So when I have a shitty day, I drift away and put 'em on\n" +
                "'Cause I don't really got shit else\n" +
                "So that shit helps when I'm depressed\n" +
                "I even got a tattoo of your name across the chest\n" +
                "Sometimes I even cut myself to see how much it bleeds\n" +
                "It's like adrenaline, the pain is such a sudden rush for me\n" +
                "See, everything you say is real, and I respect you 'cause you tell it\n" +
                "My girlfriend's jealous 'cause I talk about you 24/7\n" +
                "But she don't know you like I know you, Slim, no one does\n" +
                "She don't know what it was like for people like us growin' up\n" +
                "You gotta call me, man\n" +
                "I'll be the biggest fan you'll ever lose, sincerely yours, Stan\n" +
                "P.S.: We should be together too\n" +
                "\n" +
                "My tea's gone cold\n" +
                "I'm wondering why I got out of bed at all\n" +
                "The morning rain clouds up my window\n" +
                "And I can't see at all\n" +
                "And even if I could it'd all be gray\n" +
                "I put your picture on that wall\n" +
                "It reminds me that it's not so bad, it's not so bad\n" +
                "\n" +
                "And that story isn't over, but I have to tell the truth\n" +
                "I know I couldn't do a justice, only one who could is you\n" +
                "Because I idolized you, Marshall, and nobody understands\n" +
                "Maybe I'm a little crazy, maybe I'm just like Stan, damn\n");
    }

    @Override
    /**
     * Start method. Method is called during different phases of the application with the status label updated to the next phase in the application.
     * Listed Phases:
     */
    public void start(Stage primaryStage){
            stageControl = new StageControl(this);
            stageControl.homeStage();

            stageControl.getMainStage().setOnCloseRequest(closingEvent -> {
                threadControl.resetThreads();
                Platform.exit();
            });
            stageControl.getMainStage().show();
        runStepBasedOnStatus("New application");
    }

    protected void runStepBasedOnStatus(String status){


        switch (status){

            case ("Get Access and Refresh Token"):
                // ----> Called after user Spotify authentication <----
                logger.log(Level.INFO,"getAccessAndRefreshToken() Method Call");
                getAccessAndRefreshToken();
                threadControl.expiredTokenRefreshStart();
                break;

            case ("Error during Authentication"):
                //-----> Called if there is any reason the authentication with Spotify failed, offers the user to retry the connection <----
                logger.log(Level.WARNING,"Warning: connectToSpotify() method call fail - " + status);
                stageControl.errorStage("Authorization request was cancelled for the listed reason: " + status, false);

            case ("Get current song details"):
                // -----> Called when the authentication is successful to get the current song information <-----
                logger.log(Level.INFO,"Info: getCurrentSongInfo() Method Call");
                getCurrentSongInfo();
                break;

            case("Get Lyrics"):
                // -----> Called after getting the current song details to get the lyrics for that song <-----
                logger.log(Level.INFO,"Get Lyrics for current song");
                getLyrics();
                break;

            case("Check if current song is still playing"):
                    threadControl.startCurrentSongCheckThread();
                break;


            default:
                stageControl.homeStage();
                threadControl.resetThreads();
                userAuthorization.resetAuthentication();
                break;

        }
    }

    @Override
    public void stop(){
        System.out.println("Stage is Closing");
        userAuthorization.resetAuthentication();
        threadControl.resetThreads();
    }

    private void updateParents() {
        stageControl.setParent(this);
    }

    /**
     * From the authorization token provided from the successful authentication in connectToSpotify(),
     * authorization token is used in a POST call to Spotify URL to with client ID and Secret to obtain access and refresh token.
     */
    private void getAccessAndRefreshToken(){
        updateParents();
        try {
            URL spoitfyPOST = new URL("https://accounts.spotify.com/api/token?grant_type=authorization_code&code="+ userAuthorization.getAuthorizationToken() +"&redirect_uri=" + redirectURI +"&client_id="+Configuration.getClientID()+ "&client_secret="+ Configuration.getClientSecret());
            HttpURLConnection connection = (HttpURLConnection) spoitfyPOST.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("client_id", Configuration.getEncodedClientID());
            connection.setRequestProperty("client_secret", Configuration.getEncodedClientSecret());
            connection.setRequestProperty("Content-Length","0");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            System.out.println(connection.getResponseCode());
            System.out.println(connection.getURL());
            if (connection.getResponseCode() == 200){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String bufferedInput;
                while ((bufferedInput = bufferedReader.readLine()) != null){
                    System.out.println(bufferedInput);
                    String[] splitInput = bufferedInput.split("\"");
                    for (int i=0 ; i< splitInput.length; i++){
                        System.out.println(splitInput[i]);
                        switch (splitInput[i]){
                            case("access_token"):
                                userAuthorization.setAccess_token(splitInput[i+2]);
                                i+=2;
                                break;
                            case("expires_in"):
                                String[] expiresSplit = splitInput[i+1].split(":");
                                userAuthorization.setExpires_in(Integer.parseInt(expiresSplit[1].split(",")[0]));
                                i++;
                                break;
                            case("refresh_token"):
                                userAuthorization.setRefresh_token(splitInput[i+2]);
                                break;
                        }
                    }
                }

                // Starts Thread to check for progress on the song playing after obtaining access token
                threadControl.songProgressThreadStart();
                runStepBasedOnStatus("Get current song details");
                connection.disconnect();

            }else{
                connection.disconnect();
                stageControl.errorStage("Unable to retrieve access token. Error code: " + connection.getResponseCode(), false);
            }

        } catch (IOException e) {
            logger.log(Level.INFO,"Could not get Access Token. Error Message: " + e.getMessage());
        }
    }

    /**
     * From the access token obtained from getAccessAndRefreshToken(), a GET call is made to spotify API to obtain the users current playing songs
     *  Expected results from API Call: 200 - Successful Call / 204 - Successful call but there is no content
     */
    protected void getCurrentSongInfo(){
        updateParents();
        try {
            URL getCurrentSongURL = new URL(spotifyBaseURL+"/v1/me/player/currently-playing");
            HttpURLConnection connection = (HttpURLConnection) getCurrentSongURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + userAuthorization.getAccess_token());
            System.out.println(connection.getResponseCode());

            if(connection.getResponseCode() != 200){
                if (connection.getResponseCode() == 204){
                    stageControl.errorStage("No track playing", true);
                    connection.disconnect();

                }else {
                    stageControl.errorStage("Could not access Spotify at this time, please confirm you are using the latest version and try again", true);
                    connection.disconnect();
                }

            }else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputData;
                Vector<String> listOfNames= new Vector<>();

                while ((inputData = bufferedReader.readLine()) != null){
                    String[] JSONResponse = inputData.split("\"");
                    for (int i=0; i<JSONResponse.length; i++){
                        if (JSONResponse[i].equals("name")){
                            listOfNames.add(JSONResponse[i+2]);
                        }

                    }
                }
                currentSong = listOfNames.lastElement();
                currentArtist = listOfNames.firstElement();
                System.out.println("Current Artist: " + currentArtist);
                System.out.println("Current Song: " + currentSong);
                connection.disconnect();
                runStepBasedOnStatus("Get Lyrics");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  This method should only be called through the start method with the status set to "Check if current song is still playing"
     *  Description: Checks if the song has been changed.
     * @return Returns true if the current song is still playing and has not been changed, false otherwise
     */
    protected boolean isCurrentSongStillPlaying(){
        try {
            TimeUnit.SECONDS.sleep(2);
            URL getCurrentSongURL = new URL(spotifyBaseURL+"/v1/me/player/currently-playing");
            HttpURLConnection connection = (HttpURLConnection) getCurrentSongURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + userAuthorization.getAccess_token());
            System.out.println(connection.getResponseCode());

            if(connection.getResponseCode() != 200){
               logger.log(Level.INFO, "-----> Error in isCurrentSongStillPlaying() <----- \nResponse Code: " + connection.getResponseCode());
               connection.disconnect();
                isCurrentSongStillPlaying();
               //TODO Enhancement: Handle no track playing to possibly allow user to start music
            }else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputData;
                Vector<String> listOfNames= new Vector<>();

                while ((inputData = bufferedReader.readLine()) != null){
                    String[] JSONResponse = inputData.split("\"");
                    for (int i=0; i<JSONResponse.length; i++){
                        if (JSONResponse[i].equals("name")){
                            listOfNames.add(JSONResponse[i+2]);
                        }

                    }
                }
                //FIXME When switching to Radio, intermittently getting NoSuchElementException when getting last element.
                String tempCurrentSong = listOfNames.lastElement();
                String tempCurrentArtist = listOfNames.firstElement();
                connection.disconnect();
                if (!tempCurrentSong.equals(currentSong)){
                    System.out.println("-----> Song Change <-----");
                    System.out.println("Temp current Song: " +tempCurrentSong);
                    System.out.println("Current Song: "+ currentSong );
                    System.out.println("Temp current Artist: " + tempCurrentArtist);
                    System.out.println("Current Artist: "+ currentArtist);

                    currentSong = tempCurrentSong;
                    currentArtist = tempCurrentArtist;
//                    threadControl.interruptCurrentSongCheckThread();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            runStepBasedOnStatus("Get Lyrics");
                        }
                    });
                    return false;
                }
                else{
                    System.out.println(currentSong + " is still playing");
                    isCurrentSongStillPlaying();
                }

            }

        } catch (IOException | InterruptedException e) {
            logger.log(Level.WARNING, e.getStackTrace().toString());
        }
        return true;
    }


    /**
     *
     */
    protected void getLyrics(){
        updateParents();

        stageControl.lyricsStage(scrollPane);

        try {
            //TODO update with better lyric API
            URL APISeedsURL = new URL("https://orion.apiseeds.com/api/music/lyric/"+ currentArtist.replace(" ","%20") + "/"+
                    currentSong.replace(" ","%20") +"?apikey=" + Configuration.getApiKey());
            System.out.println(APISeedsURL.toString());
            HttpURLConnection connection = (HttpURLConnection) APISeedsURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; "
                    + "Windows NT 5.1; en-US; rv:1.8.0.11) ");


            System.out.println(connection.getResponseCode());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            if (connection.getResponseCode() != 200){
                if (streamFriendlyBoolean) {
                    connection.disconnect();
                    nextSong();
                } else if (connection.getResponseCode() == 204){
                   logger.log(Level.INFO,"Successful response, but no content");
                   stageControl.errorStage("No track playing", true);

                } else {
                    stageControl.errorStage("Unable to get lyrics at this time.", true);
                    logger.log(Level.INFO,"Unsuccessful response. Response Code: "+ connection.getResponseCode());
                }

                connection.disconnect();
                logger.log(Level.INFO,"Return to start to check if current song is still playing");
                runStepBasedOnStatus("Check if current song is still playing");
            }else {
               if (!mapOfLyrics.containsKey(currentSong)){
                   String input;
                   if ((input = bufferedReader.readLine()) != null){
                       //
//                       System.out.println(input);
                       String[] listFromInput = input.split("\\{|}");
                       for (int i = 0; i< listFromInput.length ; i++){
                           System.out.println(listFromInput[i]);
                           if (listFromInput[i].contains("text")){
                               String splitLyrics = listFromInput[i].replaceAll(",\"lang\"","");
                               splitLyrics = splitLyrics.split("\"text\":")[1];
                               System.out.println(splitLyrics);
                               mapOfLyrics.put(currentSong, splitLyrics);
                               currentLyrics.setText(mapOfLyrics.get(currentSong));
                               break;
                           }
                       }
                   }
                } else currentLyrics.setText(mapOfLyrics.get(currentSong));

                if ((currentLyrics.getText().contains("nigg") || currentLyrics.getText().contains("n**")) && streamFriendlyBoolean) {
                    nextSong();
                    connection.disconnect();
                    getLyrics();
                }else {
                    connection.disconnect();
                    currentLyrics.setText(currentLyrics.getText().replace("\\n", System.lineSeparator()).replace("\\r", System.lineSeparator().replace("\\", "")));
                    currentLyrics.setWrappingWidth(550);
                    currentLyrics.setLineSpacing(5);
                    System.out.println("End of getLyrics()");
                    logger.log(Level.INFO, "Return to start to check if current song is still playing");
                    runStepBasedOnStatus("Check if current song is still playing");
                }
            }

        } catch (IOException e) {
            logger.log(Level.INFO, e.getStackTrace().toString());
            if(mapOfLyrics.containsKey(currentSong)) {
                currentLyrics.setText(mapOfLyrics.get(currentSong));
                currentLyrics.setText(currentLyrics.getText().replace("\\n", System.lineSeparator()).replace("\\r",System.lineSeparator().replace("\\" ,"")));
                currentLyrics.setWrappingWidth(550);
                currentLyrics.setLineSpacing(5);
                if ((currentLyrics.getText().contains("nigg") || currentLyrics.getText().contains("n**")) && streamFriendlyBoolean) {
                    nextSong();
                }
            }else {
                if (streamFriendlyBoolean) {
                    nextSong();
                } else stageControl.errorStage("Unable to get lyrics at this time.", true);
            }
            logger.log(Level.INFO,"Return to start to check if current song is still playing");
            runStepBasedOnStatus("Check if current song is still playing");

            }


    }


    protected void nextSong(){
        updateParents();
        try {
            System.out.println("Skip Song");
            URL nextSongURL = new URL("https://api.spotify.com/v1/me/player/next");
            HttpURLConnection nextSongConnection = (HttpURLConnection) nextSongURL.openConnection();
            nextSongConnection.setRequestMethod("POST");
            nextSongConnection.setRequestProperty("Content-Length","0");
            nextSongConnection.setRequestProperty("Authorization", "Bearer " + userAuthorization.getAccess_token());
            System.out.println(nextSongConnection.getResponseMessage());
            System.out.println("Play users next song. Response code:" + nextSongConnection.getResponseCode());
            logger.log(Level.INFO,"User skipped song, getting current song details.");
            runStepBasedOnStatus("Get current song details");
            nextSongConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Presents user a webview screen to authenticate into Spotify.
     * If authentication is successful, authorization token is updated and status label text is updated to "Get Access and Refresh Token" and calls the start method.
     * if authentication fails, status label text is updated to "Error during Authentication" and calls the start method.
     *
     * @return Webview displaying the spotify authentication screen. Status label text should be updated with the next phase of the process.
     */
    protected WebView connectToSpotify(){
        updateParents();
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webEngine.load("https://accounts.spotify.com/authorize?client_id="+ Configuration.getClientID() + "&scope=user-read-playback-state%20user-modify-playback-state&response_type=code&redirect_uri="+ redirectURI +"&show_dialog=true");
        webView.setPrefHeight(900);

        webEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation != null && newLocation.startsWith(redirectURI)) {
                System.out.println("New Location: " + newLocation);
                if (newLocation.contains("?code=")){
                    String[] code = newLocation.split("/?code=");
                    userAuthorization.setAuthorizationToken(code[1]);
                    System.out.println("Authorization Token: " + userAuthorization.getAuthorizationToken());
                    runStepBasedOnStatus("Get Access and Refresh Token");


                } else if(newLocation.contains("?error=")){
                    String[] error = newLocation.split("/?error=");
                    String[] errorSplit2 = error[1].split("&");
                    String errorReason = errorSplit2[0];
                    System.out.println("Error Reason: " + errorReason);
                    stageControl.homeStage();
                }
            }
        });
        return webView;
    }

    private String getCurrentDevice(){
        try {
            logger.log(Level.INFO, "getCurrentDevice() method start");
            URL deviceURL = new URL("https://api.spotify.com/v1/me/player/devices");
            HttpURLConnection connection = (HttpURLConnection) deviceURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + userAuthorization.getAccess_token());
            System.out.println("getCurrentDevice() response code: " + connection.getResponseCode());

            String tempDeviceID = "";
            String input;
            BufferedReader deviceBufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((input = deviceBufferedReader.readLine()) !=null){
                System.out.println(input);
                if (input.contains("id")){
                    tempDeviceID = input.split(":")[1];
                }
                if (input.contains("is_active")){
                    if (input.split(":")[1].equals("true")){
                        deviceID= tempDeviceID;
                        return deviceID;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getConnectionRequest(){
        try {
            System.out.println(redirectURI);
            URL url = new URL("http://localhost:4040/api/requests/http");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("limit","5");
            connection.setRequestProperty("tunnel_name","https://192.168.0.113:8080");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;

                while ((inputLine = reader.readLine()) != null){
                    System.out.println(inputLine);
                    if (inputLine.contains("/?code=")){
                        String[] code = inputLine.split("=");
                        userAuthorization.setAuthorizationToken(code[1]);
                        System.out.println(userAuthorization.getAuthorizationToken());
                    }
                }
            }else System.out.println(responseCode+ ": GET request not completed");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    protected static void applyConfigurations(){

        song.setText("Song: " + currentSong);
        song.setFill(Paint.valueOf(textConfigurations.getTextColor()));
        song.setStyle("-fx-font-size: "+ textConfigurations.getSongTextSize()+";");
        song.setStrokeWidth(textConfigurations.getTextOutlineWidth());
        song.setStroke(Paint.valueOf(textConfigurations.getTextOutLine()));

        artist.setText("Artist: " + currentArtist);
        artist.setFill(Paint.valueOf(textConfigurations.getTextColor()));
        artist.setStrokeWidth(textConfigurations.getTextOutlineWidth());
        artist.setStroke(Paint.valueOf(textConfigurations.getTextOutLine()));
        artist.setStyle("-fx-font-size: "+ textConfigurations.getArtistTextSize() +"; -fx-text-fill: " + textConfigurations.getTextColor() +"; ");

        currentLyrics.setStrokeWidth(textConfigurations.getTextOutlineWidth());
        currentLyrics.setFill(Paint.valueOf(textConfigurations.getTextColor()));
        currentLyrics.setStroke(Paint.valueOf(textConfigurations.getTextOutLine()));
        currentLyrics.setStyle("-fx-font-size: "+ textConfigurations.getLyricTextSize() +"; -fx-text-fill: " + textConfigurations.getTextColor() +"; ");

        stageControl.getMainPane().setStyle("-fx-background-radius: 10; -fx-background-color: "+ textConfigurations.getBackgroundColor() +"; -fx-border-color: "+ textConfigurations.getBorderColor() + "; -fx-border-radius: 10; -fx-border-width: "+textConfigurations.getBorderSize()+";");


    }

    protected MenuBar getMenubar() {
        MenuBar mainMenu = new MenuBar();

        Menu settings = new Menu("Options");
        MenuItem textSettings = new MenuItem("Text Settings");
        textSettings.setOnAction(settingsAction -> Settings.settingsPanePopUp(textConfigurations));
        CheckMenuItem streamFriendlySetting = new CheckMenuItem("Stream Friendly");
        streamFriendlySetting.setOnAction(streamFriendlyAction -> {
            streamFriendlyBoolean = streamFriendlySetting.isSelected();
        });
        settings.getItems().addAll(streamFriendlySetting,textSettings);

        Menu main = new Menu("Home");
        MenuItem home = new MenuItem("Return Home");
        home.setOnAction( returnHome -> {
            userAuthorization.resetAuthentication();
            stageControl.homeStage();
        });
        MenuItem close = new MenuItem("Close");
        close.setOnAction(closeAction -> {
            stageControl.getMainStage().close();
            threadControl.resetThreads();
        });
        main.getItems().addAll(home,close);

        mainMenu.setStyle("-fx-border-radius: 10 10 0 0; -fx-background-radius: 10 10 0 0; -fx-background-color: darkgrey");
        mainMenu.getMenus().addAll(main, settings);

        return mainMenu;
    }
}

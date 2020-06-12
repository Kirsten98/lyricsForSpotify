public class TextConfigurations {
    private String textColor = "white",backgroundColor = "dimgrey",borderColor = "dimgrey", textOutLine = "black";
    private int borderSize =3, lyricTextSize = 18, songTextSize = 24, artistTextSize = 20;
    double textOutlineWidth =.1;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getLyricTextSize() {
        return lyricTextSize;
    }

    public void setLyricTextSize(int lyricTextSize) {
        this.lyricTextSize = lyricTextSize;
    }

    public int getSongTextSize() {
        return songTextSize;
    }

    public void setSongTextSize(int songTextSize) {
        this.songTextSize = songTextSize;
    }

    public int getArtistTextSize() {
        return artistTextSize;
    }

    public void setArtistTextSize(int artistTextSize) {
        this.artistTextSize = artistTextSize;
    }

    public String getTextOutLine() {
        return textOutLine;
    }

    public void setTextOutLine(String textOutLine) {
        this.textOutLine = textOutLine;
    }

    public double getTextOutlineWidth() {
        return textOutlineWidth;
    }

    public void setTextOutlineWidth(double textOutlineWidth) {
        this.textOutlineWidth = textOutlineWidth;
    }
}

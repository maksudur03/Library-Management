package antlr;

public class ProxyToken implements Token {
    int index;
    int type;
    int channel;
    int line;
    int charPos;
    String text;
    public ProxyToken(int index) { this.index = index; }
    public ProxyToken(int index, int type, int channel,
                      int line, int charPos, String text)
    {
        this.index = index;
        this.type = type;
        this.channel = channel;
        this.line = line;
        this.charPos = charPos;
        this.text = text;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getType() {
        return type;
    }
    public void setType(int ttype) {
        this.type = ttype;
    }
    public int getLine() {
        return line;
    }
    public void setLine(int line) {
        this.line = line;
    }
    public int getCharPositionInLine() {
        return charPos;
    }
    public void setCharPositionInLine(int pos) {
        this.charPos = pos;
    }
    public int getChannel() {
        return channel;
    }
    public void setChannel(int channel) {
        this.channel = channel;
    }
    public int getTokenIndex() {
        return index;
    }
    public void setTokenIndex(int index) {
        this.index = index;
    }
    public CharStream getInputStream() {
        return null;
    }
    public void setInputStream(CharStream input) {
    }
    public String toString() {
        String channelStr = "";
        if ( channel!=Token.DEFAULT_CHANNEL ) {
            channelStr=",channel="+channel;
        }
        return "["+getText()+"/<"+type+">"+channelStr+","+line+":"+getCharPositionInLine()+",@"+index+"]";
    }
}


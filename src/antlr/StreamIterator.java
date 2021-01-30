package antlr;

import java.util.Iterator;

public class StreamIterator implements Iterator {
    int i = 0;
    public boolean hasNext() {
        return i<nodes.size();
    }

    public Object next() {
        int current = i;
        i++;
        if ( current < nodes.size() ) {
            return nodes.get(current);
        }
        return eof;
    }

    public void remove() {
        throw new RuntimeException("cannot remove nodes from stream");
    }
}
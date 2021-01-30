package antlr;

import java.util.BitSet;

public class ErrorState {
    public int errors;
    public int warnings;
    public int infos;
    /** Track all msgIDs; we use to abort later if necessary
     *  also used in Message to find out what type of message it is via getMessageType()
     */
    public java.util.BitSet errorMsgIDs = new java.util.BitSet();
    public java.util.BitSet warningMsgIDs = new BitSet();
    // TODO: figure out how to do info messages. these do not have IDs...kr
    //public BitSet infoMsgIDs = new BitSet();
}


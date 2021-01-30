/* Copyright (c) 2001-2009, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package hsqldb.scriptio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.hsqldb.Database;
import org.hsqldb.Session;
import org.hsqldb.lib.StringConverter;
import org.hsqldb.persist.Crypto;
import org.hsqldb.rowio.RowInputTextLog;

/**
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 * @version 1.9.0
 * @since 1.9.0
 */
public class ScriptReaderDecode extends ScriptReaderText {

    DataInputStream dataInput;
    Crypto          crypto;
    byte[]          buffer = new byte[256];

    public ScriptReaderDecode(Database db, String fileName,
                              Crypto crypto) throws IOException {

        super(db);

        InputStream d =
            database.logger.getFileAccess().openInputStreamElement(fileName);
        InputStream stream = crypto.getInputStream(d);

        stream       = new GZIPInputStream(stream);
        dataStreamIn = new BufferedReader(new InputStreamReader(stream));
        rowIn        = new RowInputTextLog();
    }

    public ScriptReaderDecode(Database db, String fileName, Crypto crypto,
                              boolean forLog) throws IOException {

        super(db);

        this.crypto = crypto;

        InputStream d =
            database.logger.getFileAccess().openInputStreamElement(fileName);

        dataInput = new DataInputStream(new BufferedInputStream(d));
        rowIn     = new RowInputTextLog();
    }

    public boolean readLoggedStatement(Session session) throws IOException {

        if (dataInput == null) {
            return super.readLoggedStatement(session);
        }

        int count;

        try {
            count = dataInput.readInt();

            if (count * 2 > buffer.length) {
                buffer = new byte[count * 2];
            }

            dataInput.readFully(buffer, 0, count);
        } catch (Throwable t) {
            return false;
        }

        count = crypto.decode(buffer, 0, count, buffer, 0);

        String s = new String(buffer, 0, count, "ISO-8859-1");

        lineCount++;

//        System.out.println(lineCount);
        statement = StringConverter.unicodeStringToString(s);

        if (statement == null) {
            return false;
        }

        processStatement(session);

        return true;
    }

    public void close() {

        try {
            if (dataStreamIn != null) {
                dataStreamIn.close();
            }

            if (dataInput != null) {
                dataInput.close();
            }
        } catch (Exception e) {}
    }
}

package org.gauge;
import com.google.protobuf.CodedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class MessageLength {
    private long length;
    private CodedInputStream remainingStream;

    MessageLength(long length, CodedInputStream remainingStream) {
        this.length = length;
        this.remainingStream = remainingStream;
    }

    public long getLength() {
        return length;
    }

    public CodedInputStream getRemainingStream() {
        return remainingStream;
    }

    public byte[] toBytes() throws IOException {
        CodedInputStream stream = this.getRemainingStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < getLength(); i++) {
            outputStream.write(stream.readRawByte());
        }
        return outputStream.toByteArray();
    }

    public static MessageLength FromInputStream(InputStream is) throws IOException {
        CodedInputStream codedInputStream = CodedInputStream.newInstance(is);
        long size = codedInputStream.readRawVarint64();
        return new MessageLength(size, codedInputStream);
    }
}

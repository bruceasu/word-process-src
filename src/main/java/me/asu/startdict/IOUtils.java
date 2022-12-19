package me.asu.startdict;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOUtils {
    public IOUtils() {
    }

    public static byte[] readFully(InputStream in, int len, boolean isDetectPrematureEof) throws IOException {
        byte[] buffer = new byte[0];
        if (len == -1) {
            len = Integer.MAX_VALUE;
        }

        int length;
        for(int i = 0; i < len; i += length) {
            int k;
            if (i >= buffer.length) {
                k = Math.min(len - i, buffer.length + 1024);
                if (buffer.length < i + k) {
                    buffer = Arrays.copyOf(buffer, i + k);
                }
            } else {
                k = buffer.length - i;
            }

            length = in.read(buffer, i, k);
            if (length < 0) {
                if (isDetectPrematureEof && len != 2147483647) {
                    throw new EOFException("Detect premature EOF");
                }

                if (buffer.length != i) {
                    buffer = Arrays.copyOf(buffer, i);
                }
                break;
            }
        }

        return buffer;
    }

    public static byte[] readNBytes(InputStream var0, int var1) throws IOException {
        if (var1 < 0) {
            throw new IOException("length cannot be negative: " + var1);
        } else {
            return readFully(var0, var1, true);
        }
    }
}

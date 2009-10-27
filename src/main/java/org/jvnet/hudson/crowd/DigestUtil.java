package org.jvnet.hudson.crowd;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Getting a md5 digest of a string takes this much code in Java. Insane.
 *
 * @author Kohsuke Kawaguchi
 */
public class DigestUtil {
    /**
     * Write-only buffer.
     */
    private static final byte[] garbage = new byte[8192];

    /**
     * Computes MD5 digest of the given input stream.
     *
     * @param source
     *      The stream will be closed by this method at the end of this method.
     * @return
     *      32-char wide string
     */
    public static String getDigestOf(InputStream source) throws IOException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            DigestInputStream in =new DigestInputStream(source,md5);
            try {
                while(in.read(garbage)>0)
                    ; // simply discard the input
            } finally {
                in.close();
            }
            return toHexString(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);    // impossible
        }
    }

    public static String getDigestOf(String text) {
        try {
            return getDigestOf(new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static String toHexString(byte[] data, int start, int len) {
        StringBuilder buf = new StringBuilder();
        for( int i=0; i<len; i++ ) {
            int b = data[start+i]&0xFF;
            if(b<16)    buf.append('0');
            buf.append(Integer.toHexString(b));
        }
        return buf.toString();
    }

    public static String toHexString(byte[] bytes) {
        return toHexString(bytes,0,bytes.length);
    }
}

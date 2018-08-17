package com.niuchaoqun.sogouweixin.common;

import okhttp3.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SerializableCookie implements Serializable {
    public static final Logger logger = LoggerFactory.getLogger(SerializableCookie.class);

    private static final long serialVersionUID = -8594045714036645534L;

    private transient Cookie cookie;

    public String encode(Cookie cookie) {
        this.cookie = cookie;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;

        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            logger.debug("IOException in encodeCookie", e);
            return null;
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    logger.debug("Stream not closed in encodeCookie", e);
                }
            }
        }

        return byteArrayToHexString(byteArrayOutputStream.toByteArray());
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't
     * have to rely on any large Base64 libraries. Can be overridden if you
     * like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    public Cookie decode(String encodedCookie) {

        byte[] bytes = hexStringToByteArray(encodedCookie);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                bytes);

        Cookie cookie = null;
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie) objectInputStream.readObject()).cookie;
        } catch (IOException e) {
            logger.debug("IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            logger.debug("ClassNotFoundException in decodeCookie", e);
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    logger.debug("Stream not closed in decodeCookie", e);
                }
            }
        }
        return cookie;
    }

    /**
     * Converts hex values from strings to byte array
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private static long NON_VALID_EXPIRES_AT = -1L;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.name());
        out.writeObject(cookie.value());
        out.writeLong(cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
        out.writeObject(cookie.domain());
        out.writeObject(cookie.path());
        out.writeBoolean(cookie.secure());
        out.writeBoolean(cookie.httpOnly());
        out.writeBoolean(cookie.hostOnly());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Cookie.Builder builder = new Cookie.Builder();

        builder.name((String) in.readObject());

        builder.value((String) in.readObject());

        long expiresAt = in.readLong();
        if (expiresAt != NON_VALID_EXPIRES_AT) {
            builder.expiresAt(expiresAt);
        }

        final String domain = (String) in.readObject();
        builder.domain(domain);

        builder.path((String) in.readObject());

        if (in.readBoolean())
            builder.secure();

        if (in.readBoolean())
            builder.httpOnly();

        if (in.readBoolean())
            builder.hostOnlyDomain(domain);

        cookie = builder.build();
    }

}

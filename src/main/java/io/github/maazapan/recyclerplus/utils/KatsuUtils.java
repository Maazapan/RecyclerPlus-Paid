package io.github.maazapan.recyclerplus.utils;

import java.util.Base64;

public class KatsuUtils {

    public static String getURLFromBase64(String base64) {
        return new String(Base64.getDecoder().decode(base64.getBytes())).replace("{\"textures\":{\"SKIN\":{\"url\":\"", "").replace("\"}}}", "");
    }
}

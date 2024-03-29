package ua.edu.cdu.vu.price.aggregator.api.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

@UtilityClass
public class FileUtils {

    public static String encodeAsBase64(File file) {
        try {
            return new String(Base64.getEncoder().encode(new FileInputStream(file).readAllBytes()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

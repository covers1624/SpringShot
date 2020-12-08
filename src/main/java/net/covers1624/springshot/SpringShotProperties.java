package net.covers1624.springshot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by covers1624 on 25/11/20.
 */
@Component
@ConfigurationProperties(prefix = "springshot")
public class SpringShotProperties {

    private Path objectsDir;

    private int minHashLen = 5;

    private int maxUploadSize = 150 * 1024 * 1024;

    public Path getObjectsDir() {
        return objectsDir;
    }

    public void setObjectsDir(Path ssObjectDir) {
        this.objectsDir = Objects.requireNonNull(ssObjectDir).toAbsolutePath();
    }

    public int getMinHashLen() {
        return minHashLen;
    }

    public void setMinHashLen(int minHashLen) {
        this.minHashLen = minHashLen;
    }

    public int getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(int maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }
}

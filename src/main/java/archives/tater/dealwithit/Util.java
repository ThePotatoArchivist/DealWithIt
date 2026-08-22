package archives.tater.dealwithit;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface Util {
    static String snakeToTitleCase(String s) {
        return Arrays.stream(s.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}

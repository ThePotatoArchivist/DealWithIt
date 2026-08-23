package archives.tater.dealwithit;

import net.minecraft.util.RandomSource;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static net.minecraft.util.Util.shuffle;

public interface Util {
    static String snakeToTitleCase(String s) {
        return Arrays.stream(s.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    static <T> Collector<T, ?, List<T>> toShuffledList(RandomSource random) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            shuffle(list, random);
            return list;
        });
    }
}

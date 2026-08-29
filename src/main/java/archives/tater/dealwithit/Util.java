package archives.tater.dealwithit;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.util.Util.shuffle;
import static net.minecraft.util.Util.toMap;

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

    static <T> List<T> withAppended(List<T> list, T element) {
        var builder = ImmutableList.<T>builder();
        builder.addAll(list);
        builder.add(element);
        return builder.build();
    }

    static <K, V, U> Map<K, U> mapNonNullValues(Map<K, V> map, Function<V, @Nullable U> mapper) {
        return map.entrySet().stream().<Map.Entry<K, U>>mapMulti((entry, yield) -> {
            var value = mapper.apply(entry.getValue());
            if (value == null) return;
            yield.accept(Map.entry(entry.getKey(), value));
        }).collect(toMap());
    }

    static <K, V, T> Stream<T> mapEntries(Map<K, V> map, BiFunction<K, V, T> transform) {
        return map.entrySet().stream().map(entry -> transform.apply(entry.getKey(), entry.getValue()));
    }

    static <T> Stream<Holder<T>> streamOrdered(HolderLookup<T> registry, TagKey<T> orderTag) {
        return Stream.concat(
                registry.get(orderTag).stream().flatMap(HolderSet::stream),
                registry.listElements().filter(holder -> !holder.is(orderTag))
        );
    }
}

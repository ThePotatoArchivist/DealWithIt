package archives.tater.dealwithit.client.datagen;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultiDataProvider implements DataProvider {

    private final String name;
    private final List<DataProvider> providers;

    public MultiDataProvider(String name, List<DataProvider> providers) {
        this.name = name;
        this.providers = providers;
    }

    public MultiDataProvider(String name, DataProvider... providers) {
        this(name, Arrays.asList(providers));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(providers.stream().map(provider -> provider.run(cache)).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return name;
    }
}

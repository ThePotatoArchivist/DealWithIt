package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.registry.DealWithItSounds;

import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder.RegistrationBuilder.ofFile;

public class ModSoundsProvider extends FabricSoundsProvider {
    public ModSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registryLookup, SoundExporter exporter) {
        exporter.add(DealWithItSounds.CARD_STACK_SHUFFLE, SoundTypeBuilder.of(DealWithItSounds.CARD_STACK_SHUFFLE)
                .sound(ofFile(DealWithIt.id("shuffle1")))
                .sound(ofFile(DealWithIt.id("shuffle2")))
        );
        exporter.add(DealWithItSounds.CARD_STACK_BREAK, SoundTypeBuilder.of()
                .sound(ofFile(DealWithIt.id("break1")))
                .sound(ofFile(DealWithIt.id("break2")))
                .sound(ofFile(DealWithIt.id("break3")))
                .subtitle("subtitles.block.generic.break")
        );
        exporter.add(DealWithItSounds.CARD_STACK_PLACE, SoundTypeBuilder.of(DealWithItSounds.CARD_STACK_PLACE)
                .sound(ofFile(DealWithIt.id("place1")))
                .sound(ofFile(DealWithIt.id("place2")))
                .sound(ofFile(DealWithIt.id("place3")))
                .sound(ofFile(DealWithIt.id("place4")))
                .sound(ofFile(DealWithIt.id("place5")))
                .sound(ofFile(DealWithIt.id("place6")))
                .sound(ofFile(DealWithIt.id("place7")))
                .sound(ofFile(DealWithIt.id("place8")))
        );
        exporter.add(DealWithItSounds.CARD_STACK_PICKUP, SoundTypeBuilder.of(DealWithItSounds.CARD_STACK_PICKUP)
                .sound(ofFile(DealWithIt.id("pickup1")))
                .sound(ofFile(DealWithIt.id("pickup2")))
                .sound(ofFile(DealWithIt.id("pickup3")))
                .sound(ofFile(DealWithIt.id("pickup4")))
                .sound(ofFile(DealWithIt.id("pickup5")))
        );
        exporter.add(DealWithItSounds.CARD_STACK_HIT, SoundTypeBuilder.of()
                .sound(ofFile(DealWithIt.id("hit1")))
                .sound(ofFile(DealWithIt.id("hit2")))
                .sound(ofFile(DealWithIt.id("hit3")))
                .sound(ofFile(DealWithIt.id("hit4")))
                .sound(ofFile(DealWithIt.id("hit5")))
                .sound(ofFile(DealWithIt.id("hit6")))
                .sound(ofFile(DealWithIt.id("hit7")))
                .sound(ofFile(DealWithIt.id("hit8")))
                .subtitle("subtitles.block.generic.hit")
        );
        exporter.add(DealWithItSounds.CARD_STACK_STEP, SoundTypeBuilder.of()
                .sound(ofFile(DealWithIt.id("hit1")))
                .sound(ofFile(DealWithIt.id("hit2")))
                .sound(ofFile(DealWithIt.id("hit3")))
                .sound(ofFile(DealWithIt.id("hit4")))
                .sound(ofFile(DealWithIt.id("hit5")))
                .sound(ofFile(DealWithIt.id("hit6")))
                .sound(ofFile(DealWithIt.id("hit7")))
                .sound(ofFile(DealWithIt.id("hit8")))
                .subtitle("subtitles.block.generic.step")
        );
        exporter.add(DealWithItSounds.CARD_STACK_FALL, SoundTypeBuilder.of()
                .sound(ofFile(DealWithIt.id("place1")))
                .sound(ofFile(DealWithIt.id("place2")))
                .sound(ofFile(DealWithIt.id("place3")))
                .sound(ofFile(DealWithIt.id("place4")))
                .sound(ofFile(DealWithIt.id("place5")))
                .sound(ofFile(DealWithIt.id("place6")))
                .sound(ofFile(DealWithIt.id("place7")))
                .sound(ofFile(DealWithIt.id("place8")))
                .subtitle("subtitles.block.generic.fall")
        );
        exporter.add(DealWithItSounds.CARD_BOX_INSERT, SoundTypeBuilder.of(DealWithItSounds.CARD_BOX_INSERT)
                .sound(ofFile(DealWithIt.id("insert1")))
                .sound(ofFile(DealWithIt.id("insert2")))
                .sound(ofFile(DealWithIt.id("insert3")))
        );
        exporter.add(DealWithItSounds.CARD_FLIP, SoundTypeBuilder.of(DealWithItSounds.CARD_FLIP)
                .sound(ofFile(DealWithIt.id("pickup1")))
                .sound(ofFile(DealWithIt.id("pickup2")))
                .sound(ofFile(DealWithIt.id("pickup3")))
                .sound(ofFile(DealWithIt.id("pickup4")))
                .sound(ofFile(DealWithIt.id("pickup5")))
        );
    }

    @Override
    public String getName() {
        return "Sounds";
    }
}

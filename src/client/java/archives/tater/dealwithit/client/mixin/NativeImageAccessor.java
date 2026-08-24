package archives.tater.dealwithit.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.platform.NativeImage;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {
    @Accessor
    NativeImage.Format getFormat();

    @Accessor("pixels")
    long dealwithit$getPixels();

    @Accessor
    long getSize();

    @Invoker
    void callCheckAllocated();

    @Invoker
    int callGetPixelABGR(final int x, final int y);
}

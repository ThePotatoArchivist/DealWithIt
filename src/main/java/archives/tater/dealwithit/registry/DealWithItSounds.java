package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public interface DealWithItSounds {

    SoundEvent CARD_STACK_BREAK = register("block.card_stack.break");
    SoundEvent CARD_STACK_STEP = register("block.card_stack.step");
    SoundEvent CARD_STACK_PLACE = register("block.card_stack.place");
    SoundEvent CARD_STACK_HIT = register("block.card_stack.hit");
    SoundEvent CARD_STACK_FALL = register("block.card_stack.fall");
    SoundEvent CARD_STACK_SHUFFLE = register("block.card_stack.shuffle");
    SoundEvent CARD_STACK_PICKUP = register("block.card_stack.pickup");
    SoundEvent CARD_BOX_INSERT = register("item.card_box.insert");
    SoundEvent CARD_FLIP = register("item.card.flip");

    SoundType CARD_STACK = new SoundType(1f, 1f, CARD_STACK_BREAK, CARD_STACK_STEP, CARD_STACK_PLACE, CARD_STACK_HIT, CARD_STACK_FALL);

    private static SoundEvent register(String path) {
        return register(DealWithIt.id(path));
    }

    private static SoundEvent register(Identifier id) {
        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                id,
                SoundEvent.createVariableRangeEvent(id)
        );
    }

    static void init() {

    }
}

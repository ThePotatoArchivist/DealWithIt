package archives.tater.dealwithit.client;

import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.network.ServerboundCycleCardStackPayload;
import archives.tater.dealwithit.registry.DealWithItComponents;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CardStackMouseAction implements ItemSlotMouseAction {

    private final Minecraft minecraft;
    private final ScrollWheelHandler scrollWheelHandler;

    public CardStackMouseAction(Minecraft minecraft) {
        this.minecraft = minecraft;
        scrollWheelHandler = new ScrollWheelHandler();
    }

    @Override
    public boolean matches(Slot slot) {
        return slot.getItem().has(DealWithItComponents.CARD_STACK);
    }

    @Override
    public boolean onMouseScrolled(double scrollX, double scrollY, int slotIndex, ItemStack itemStack) {
        var cardStack = itemStack.getOrDefault(DealWithItComponents.CARD_STACK, CardStack.EMPTY);
        if (cardStack.size() <= 1) return false;

        var wheelXY = scrollWheelHandler.onMouseScroll(scrollX, scrollY);
        var wheel = wheelXY.y == 0 ? -wheelXY.x : wheelXY.y;
        if (wheel != 0) cycleCards(itemStack, slotIndex, wheel);

        return true;
    }

    @Override
    public void onStopHovering(Slot hoveredSlot) {

    }

    @Override
    public void onSlotClicked(Slot slot, ContainerInput containerInput) {

    }

    private void cycleCards(ItemStack stack, int slotIndex, int amount) {
        if (minecraft.getConnection() == null) return;

        CardStack.cycle(stack, amount);
        ClientPlayNetworking.send(new ServerboundCycleCardStackPayload(slotIndex, amount));
    }
}

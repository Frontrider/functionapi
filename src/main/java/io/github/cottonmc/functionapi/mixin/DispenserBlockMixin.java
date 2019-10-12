package io.github.cottonmc.functionapi.mixin;


import io.github.cottonmc.functionapi.events.special.DispenserEvent;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {


    private DispenserEvent event = new DispenserEvent();

    /**
     * if there is no event, then we do the command.
     */
    @Inject(at = @At("RETURN"), cancellable = true, method = "getBehaviorForItem")
    private void dispense(ItemStack itemStack_1, CallbackInfoReturnable<DispenserBehavior> cir) {
        if(cir.getReturnValue() == DispenserBehavior.NOOP){
            cir.setReturnValue(event);
        }
    }
}
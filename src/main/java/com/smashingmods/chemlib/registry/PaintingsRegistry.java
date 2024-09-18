package com.smashingmods.chemlib.registry;

import com.smashingmods.chemlib.ChemLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PaintingsRegistry {
    private static final DeferredRegister<PaintingVariant> PAINTINGS = DeferredRegister.create(Registries.PAINTING_VARIANT, ChemLib.MODID);

    public static void register(IEventBus eventBus) {
        PAINTINGS.register("periodic_table", () -> new PaintingVariant(80, 48, ChemLib.modLoc("periodic_table")));
        PAINTINGS.register(eventBus);
    }
}

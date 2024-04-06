package com.smashingmods.chemlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.smashingmods.chemlib.registry.Registry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;

@Mod(ChemLib.MODID)
public class ChemLib {
    public static final String MODID = "chemlib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Style MOD_ID_TEXT_STYLE = Style.EMPTY.withFont(Style.DEFAULT_FONT).withItalic(true).withColor(ChatFormatting.BLUE);

    public ChemLib(IEventBus modEventBus) {
        Registry.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
        Config.loadConfig(Config.COMMON_SPEC, FMLPaths.CONFIGDIR.get().resolve("chemlib-common.toml"));
    }
}
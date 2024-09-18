package com.smashingmods.chemlib.datagen;

import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.api.ChemicalItemType;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends ItemTagsProvider {

    public ItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, TagsProvider<Block> pBlockTagProvider, ExistingFileHelper pFileHelper) {
        super(pOutput, pLookupProvider, pBlockTagProvider.contentsGetter(), ChemLib.MODID, pFileHelper);
    }

    @Override
    public void addTags(HolderLookup.Provider lookupProvider) {
        ItemRegistry.getChemicalItems().forEach(item -> {
            String type = item.getItemType().getSerializedName();
            String name = item.getChemicalName();
            TagKey<Item> key = commonTag(String.format("%ss/%s", type, name));
            tag(key).add(item);
        });

        ItemRegistry.getChemicalBlockItems().forEach(item -> {
            if (item.getMatterState().equals(MatterState.SOLID)) {
                String name = item.getChemicalName();
                TagKey<Item> key = commonTag(String.format("storage_blocks/%s", name));
                tag(key).add(item);
            }
        });

        ItemRegistry.getChemicalItemByNameAndType("potassium_nitrate", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            TagKey<Item> key = commonTag("dusts/niter");
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("hydroxylapatite", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            TagKey<Item> key = commonTag("dusts/apatite");
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("cellulose", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            TagKey<Item> key = commonTag("sawdust");
            tag(key).add(compound);
        });

        ItemRegistry.getChemicalItemByNameAndType("mercury_sulfide", ChemicalItemType.COMPOUND).ifPresent(compound -> {
            TagKey<Item> key = commonTag("dusts/cinnabar");
            tag(key).add(compound);
        });
    }

    private static TagKey<Item> commonTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }

    @Override
    @Nonnull
    public String getName() {
        return ChemLib.MODID + ":tags";
    }
}

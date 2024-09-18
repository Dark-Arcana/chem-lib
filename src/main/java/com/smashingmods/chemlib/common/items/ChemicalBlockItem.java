package com.smashingmods.chemlib.common.items;

import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.api.Element;
import com.smashingmods.chemlib.api.MatterState;
import com.smashingmods.chemlib.common.blocks.ChemicalBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class ChemicalBlockItem extends BlockItem implements Chemical {

    private final ChemicalBlock block;

    public ChemicalBlockItem(ChemicalBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.block = pBlock;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> pTooltipComponents, TooltipFlag tooltipFlag) {
        if (getChemical() instanceof Element element) {
            pTooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents(String.format("%s (%d)", getAbbreviation(), element.getAtomicNumber()))).withStyle(ChatFormatting.DARK_AQUA));
            pTooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents(element.getGroupName())).withStyle(ChatFormatting.GRAY));
        }
    }

    public Chemical getChemical() {
        return block.getChemical();
    }

    @Override
    public String getChemicalName() {
        return block.getChemicalName();
    }

    @Override
    public String getAbbreviation() {
        return getChemical().getAbbreviation();
    }

    @Override
    public MatterState getMatterState() {
        return getChemical().getMatterState();
    }

    @Override
    public String getChemicalDescription() {
        return getChemical().getChemicalDescription();
    }

    @Override
    public List<MobEffectInstance> getEffects() {
        return getChemical().getEffects();
    }

    @Override
    public int getColor() {
        return clampMinColorValue(getChemical().getColor(), 0x44);
    }

    @SuppressWarnings("unused")
    public int getColor(ItemStack pItemStack, int pTintIndex) {
        return getColor();
    }
}

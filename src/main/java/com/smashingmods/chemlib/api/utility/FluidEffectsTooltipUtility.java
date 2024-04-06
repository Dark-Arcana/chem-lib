package com.smashingmods.chemlib.api.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.smashingmods.chemlib.registry.ItemRegistry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.AttributeModifierTemplate;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class FluidEffectsTooltipUtility {

    public static List<Component> getBucketEffectTooltipComponents(ItemStack pStack) {
        List<Component> componentList = new ArrayList<>();

        BuiltInRegistries.FLUID.getResourceKey(((BucketItem) pStack.getItem()).getFluid()).ifPresent(fluidResourceKey -> {
            String chemicalName = StringUtils.removeEnd(fluidResourceKey.location().getPath(), "_fluid");
            AtomicReference<List<MobEffectInstance>> effectList = new AtomicReference<>();
            ItemRegistry.getElementByName(chemicalName).ifPresent(element -> effectList.set(element.getEffects()));
            ItemRegistry.getCompoundByName(chemicalName).ifPresent(compound -> effectList.set(compound.getEffects()));
            addTooltipEffects(effectList.get(), componentList);
        });
        return componentList;
    }
    
    private static String parseAttributeModifier(AttributeModifier modifier) { // weird 1.20.4 workaround
    	String toString = modifier.toString();
    	int indexName = toString.indexOf("name='") + 6;
    	int indexId = toString.indexOf("id=") - 3;
    	return toString.substring(indexName, indexId);
    }

    public static void addTooltipEffects(List<MobEffectInstance> pEffects, List<Component> pTooltips) {
        List<Pair<Attribute, AttributeModifier>> attributeModifierPairList = Lists.newArrayList();
        if (pEffects.isEmpty()) {
            pTooltips.add(MutableComponent.create(new LiteralContents(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.UNDERLINE).append(":"));
            pTooltips.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltips.add(MutableComponent.create(new LiteralContents(" ")));
            pTooltips.add(MutableComponent.create(new TranslatableContents("chemlib.effect.on_hit", null, TranslatableContents.NO_ARGS)).withStyle(ChatFormatting.UNDERLINE).append(":"));
            for (MobEffectInstance effectInstance : pEffects) {
                MutableComponent mutableComponent = Component.translatable(effectInstance.getDescriptionId());
                MobEffect effect = effectInstance.getEffect();
                Map<Attribute, AttributeModifierTemplate> attributeModifierMap = effect.getAttributeModifiers();
                int amplifier = effectInstance.getAmplifier();
                
                if (!attributeModifierMap.isEmpty()) {
                    for (Entry<Attribute, AttributeModifierTemplate> attributeModifierEntry : attributeModifierMap.entrySet()) {
                        AttributeModifier entryValue = attributeModifierEntry.getValue().create(amplifier);
                        AttributeModifier attributeModifier = new AttributeModifier(parseAttributeModifier(entryValue), entryValue.getAmount() * (amplifier + 1), entryValue.getOperation());
                        attributeModifierPairList.add(Pair.of(attributeModifierEntry.getKey(), attributeModifier));
                    }
                }
                
                if (amplifier > 0 && amplifier <= 20) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + amplifier));
                } else {
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(effectInstance, 1.0F, 20.F));
                }
                pTooltips.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!attributeModifierPairList.isEmpty()) {
            for (Pair<Attribute, AttributeModifier> attributeModifierPair : attributeModifierPairList) {
                AttributeModifier attributeModifier = attributeModifierPair.getValue();

                double baseModifierAmount = attributeModifier.getAmount();
                double finalModiferAmount;

                if (attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributeModifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    finalModiferAmount = attributeModifier.getAmount();
                } else {
                    finalModiferAmount = attributeModifier.getAmount() * 100.0D;
                }
                if (baseModifierAmount > 0.0D) {
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.plus.%s", attributeModifier.getOperation().toValue()),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().getDescriptionId()))
                            .withStyle(ChatFormatting.BLUE));

                } else if (baseModifierAmount < 0.0D) {
                    finalModiferAmount *= -1.0D;
                    pTooltips.add(Component.translatable(String.format("attribute.modifier.take.%s", attributeModifier.getOperation().toValue()),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(finalModiferAmount),
                            Component.translatable(attributeModifierPair.getKey().getDescriptionId()))
                            .withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}

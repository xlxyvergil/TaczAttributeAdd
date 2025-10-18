package net.puffish.attributesmod.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.puffish.attributesmod.AttributesMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;
import java.util.stream.Collectors;

@Mixin(SimpleRegistry.class)
public class SimpleRegistryMixin {

	@ModifyReturnValue(method = "getIds", at = @At("RETURN"))
	public Set<Identifier> modifyReturnValueAtGetIds(Set<Identifier> set) {
		if ((Object) this == Registries.ATTRIBUTE) {
			return set.stream().filter(
					k -> !(k.getNamespace().equals(AttributesMod.MOD_ID) && k.getPath().startsWith("player."))
			).collect(Collectors.toSet());
		} else {
			return set;
		}
	}

}

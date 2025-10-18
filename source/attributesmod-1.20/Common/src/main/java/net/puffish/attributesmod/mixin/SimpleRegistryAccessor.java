package net.puffish.attributesmod.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor<T> {
	@Accessor
	Map<Identifier, RegistryEntry.Reference<T>> getIdToEntry();

	@Accessor
	Map<RegistryKey<T>, RegistryEntry.Reference<T>> getKeyToEntry();
}

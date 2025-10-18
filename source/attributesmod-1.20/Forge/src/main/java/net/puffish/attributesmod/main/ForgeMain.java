package net.puffish.attributesmod.main;

import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.puffish.attributesmod.AttributesMod;
import net.puffish.attributesmod.util.Registrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(AttributesMod.MOD_ID)
public class ForgeMain {

	private final List<RegistryAlias<?>> registryAliases = new ArrayList<>();

	public ForgeMain() {
		AttributesMod.setup(new RegistrarImpl());

		MinecraftForge.EVENT_BUS.addListener(this::onIdMapping);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::onRegister);
	}

	private void onIdMapping(IdMappingEvent event) {
		registryAliases.forEach(RegistryAlias::apply);
	}

	private void onRegister(RegisterEvent event) {
		registryAliases.forEach(registryAlias -> registryAlias.applyFor(event.getVanillaRegistry()));
	}

	private class RegistrarImpl implements Registrar {
		@Override
		public <V, T extends V> void register(Registry<V> registry, Identifier id, T entry) {
			var deferredRegister = DeferredRegister.create(registry.getKey(), id.getNamespace());
			deferredRegister.register(id.getPath(), () -> entry);
			deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		@Override
		public <V> void registerAlias(Registry<V> registry, Identifier aliasId, Identifier id) {
			registryAliases.add(new RegistryAlias<>(registry, aliasId, id));
		}
	}

	private record RegistryAlias<V>(Registry<V> registry, Identifier aliasId, Identifier id) {
		public void applyFor(Registry<?> registry) {
			if (registry == this.registry) {
				apply();
			}
		}

		@SuppressWarnings("unchecked")
		public void apply() {
			try {
				var namespacedWrapperClass = Class.forName("net.minecraftforge.registries.NamespacedWrapper");
				var forgeRegistryClass = ForgeRegistry.class;

				var delegateField = namespacedWrapperClass.getDeclaredField("delegate");
				delegateField.setAccessible(true);

				var holdersByNameField = namespacedWrapperClass.getDeclaredField("holdersByName");
				holdersByNameField.setAccessible(true);

				var delegate = (ForgeRegistry<?>) delegateField.get(registry);
				var locked = delegate.isLocked();
				if (locked) {
					delegate.unfreeze();
				}
				delegate.addAlias(aliasId, id);
				if (locked) {
					delegate.freeze();
				}

				var holdersByName = (Map<Identifier, RegistryEntry.Reference<V>>) holdersByNameField.get(registry);
				holdersByName.put(aliasId, holdersByName.get(id));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}

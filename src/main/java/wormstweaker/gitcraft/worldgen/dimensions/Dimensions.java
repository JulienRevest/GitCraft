package wormstweaker.gitcraft.worldgen.dimensions;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import wormstweaker.gitcraft.Gitcraft;

public class Dimensions {
    public static final ResourceKey<Level> GITCRAFT = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Gitcraft.MOD_ID, "gitcraft"));

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Gitcraft.MOD_ID, "gitcraft_chunkgen"), GitcraftChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(Gitcraft.MOD_ID, "biomes"), GitcraftBiomeProvider.CODEC);
    }
}

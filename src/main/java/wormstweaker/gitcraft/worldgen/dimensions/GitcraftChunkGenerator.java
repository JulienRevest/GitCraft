package wormstweaker.gitcraft.worldgen.dimensions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.blending.Blender;
import wormstweaker.gitcraft.Gitcraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/*
* Big thanks to Jorrit Tyberghein
* https://youtu.be/rilsGp8dFJA
*/

public class GitcraftChunkGenerator extends ChunkGenerator {

    // Getting settings from dimension/gitcraft.json
    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(settingsInstance ->
            settingsInstance.group(
                Codec.INT.fieldOf("base").forGetter(Settings::baseHeight),
                Codec.FLOAT.fieldOf("verticalvariance").forGetter(Settings::verticalVariance),
                Codec.FLOAT.fieldOf("horizontalvariance").forGetter(Settings::horizontalVariance)
            ).apply(settingsInstance, Settings::new));
    public static final Codec<GitcraftChunkGenerator> CODEC = RecordCodecBuilder.create(gitcraftChunkGeneratorInstance ->
            gitcraftChunkGeneratorInstance.group(
                    RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(GitcraftChunkGenerator::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(GitcraftChunkGenerator::getGitcraftSettings)
            ).apply(gitcraftChunkGeneratorInstance, GitcraftChunkGenerator::new));

    private final Settings settings;

    public GitcraftChunkGenerator(Registry<Biome> biomeRegistry, Settings settings) {
        super(new GitcraftBiomeProvider(biomeRegistry), new StructureSettings(false));
        this.settings = settings;
        Gitcraft.LOGGER.info("Chunk generator settings: " + settings.baseHeight + ", " + settings.horizontalVariance + ", " + settings.verticalVariance);
    }

    public Settings getGitcraftSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((GitcraftBiomeProvider)biomeSource).getBiomeRegistry();
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long p_62156_) {
        return null;
    }

    @Override
    public Climate.Sampler climateSampler() {
        return null;
    }

    @Override
    public void applyCarvers(WorldGenRegion p_187691_, long p_187692_, BiomeManager p_187693_, StructureFeatureManager p_187694_, ChunkAccess p_187695_, GenerationStep.Carving p_187696_) {

    }

    @Override
    public void buildSurface(WorldGenRegion p_187697_, StructureFeatureManager p_187698_, ChunkAccess chunk) {
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        ChunkPos chunkPos = chunk.getPos();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Create bedrock floor
        int x, z;
        for (x = 0; x < 16; x++) {
            for(z = 0; z < 16; z++){
                chunk.setBlockState(pos.set(x, 0, z), bedrock, false);
            }
        }

        int baseHeight = settings.baseHeight();
        float verticalVariance = settings.verticalVariance();
        float horizontalVariance = settings.horizontalVariance();
        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                int realx = chunkPos.x * 16 + x;
                int realz = chunkPos.z * 16 + z;
                int height = getHeightAt(baseHeight, verticalVariance, horizontalVariance, realx, realz);
                for (int y = 1 ; y < height ; y++) {
                    chunk.setBlockState(pos.set(x, y, z), stone, false);
                }
            }
        }
    }

    private int getHeightAt(int baseHeight, float verticalVariance, float horizontalVariance, int realx, int realz) {
        return (int) (baseHeight + Math.sin(realx / horizontalVariance) * verticalVariance + Math.cos(realz / horizontalVariance) * verticalVariance);
    }

    // Spawn mobs on world creation, we don't spawn anything
    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_187748_, Blender p_187749_, StructureFeatureManager p_187750_, ChunkAccess p_187751_) {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int p_156153_, int p_156154_, Heightmap.Types p_156155_, LevelHeightAccessor p_156156_) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int p_156150_, int p_156151_, LevelHeightAccessor p_156152_) {
        return null;
    }

    private record Settings(int baseHeight, float verticalVariance, float horizontalVariance){}
}

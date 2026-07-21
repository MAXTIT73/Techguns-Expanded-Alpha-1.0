package com.TGaddon.techgunsexpanded.world;

import com.TGaddon.techgunsexpanded.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Generates Tungsten Carbide Ore in the Nether (dim -1).
 *
 * Uses vanilla WorldGenMinable so veins come out as proper compact blobs
 * instead of the thin one-block-wide snakes the old random-walk produced.
 */
public class WorldGenTungstenCarbideOre implements IWorldGenerator {

    private static final Logger LOGGER = LogManager.getLogger("TechgunsExpanded");

    // ------------------------------------------------------------------
    // Tuned to roughly one third the abundance of Nether Quartz.
    //
    // Target: veins appear three times less often than Nether Quartz.
    //
    // Vanilla quartz (ChunkGeneratorHell) rolls 16 veins per chunk, so a
    // third of that is 5. Vein size is capped at 5 blocks to keep deposits
    // small and scattered.
    // ------------------------------------------------------------------
    private static final int VEINS_PER_CHUNK = 5;
    private static final int BLOCKS_PER_VEIN = 5;

    /**
     * Nether lava sea sits at Y=31 and everything below it is solid rock down
     * to bedrock, so ore generated there is unreachable. Start above it.
     * Ceiling bedrock starts around Y=127, leave headroom.
     */
    private static final int Y_MIN = 32;   // final: 32
    private static final int Y_MAX = 100;  // final: 100

    /** Set true to log per-chunk vein counts when tuning generation. */
    private static final boolean DEBUG_LOGGING = false;

    private static final int NETHER_DIM_ID = -1;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != NETHER_DIM_ID) {
            return;
        }
        generateNether(random, chunkX, chunkZ, world);
    }

    private void generateNether(Random random, int chunkX, int chunkZ, World world) {
        if (ModBlocks.TUNGSTEN_CARBIDE_ORE == null) {
            LOGGER.error("[TechgunsExpanded] TUNGSTEN_CARBIDE_ORE is null - block was not registered, skipping worldgen");
            return;
        }

        IBlockState oreState = ModBlocks.TUNGSTEN_CARBIDE_ORE.getDefaultState();

        // Replace netherrack only. BlockMatcher is what vanilla quartz uses.
        WorldGenMinable generator = new WorldGenMinable(
                oreState,
                BLOCKS_PER_VEIN,
                BlockMatcher.forBlock(Blocks.NETHERRACK)
        );

        // Vanilla decoration convention: offset by 8 so veins straddle the
        // chunk corner. This is what keeps WorldGenMinable from triggering
        // cascading chunk generation when a vein spills over the border.
        int baseX = chunkX * 16 + 8;
        int baseZ = chunkZ * 16 + 8;

        int veinsPlaced = 0;
        for (int i = 0; i < VEINS_PER_CHUNK; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = Y_MIN + random.nextInt(Y_MAX - Y_MIN + 1);

            if (generator.generate(world, random, new BlockPos(x, y, z))) {
                veinsPlaced++;
            }
        }

        if (DEBUG_LOGGING) {
            LOGGER.info("[TechgunsExpanded] Nether chunk [{}, {}]: {}/{} veins placed (up to {} blocks each, Y {}-{})",
                    chunkX, chunkZ, veinsPlaced, VEINS_PER_CHUNK, BLOCKS_PER_VEIN, Y_MIN, Y_MAX);
        }
    }
}

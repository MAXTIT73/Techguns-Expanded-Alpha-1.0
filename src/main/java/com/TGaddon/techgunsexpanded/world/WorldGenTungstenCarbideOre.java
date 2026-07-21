package com.TGaddon.techgunsexpanded.world;

import com.TGaddon.techgunsexpanded.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class WorldGenTungstenCarbideOre implements IWorldGenerator {

    private static final Logger LOGGER = LogManager.getLogger("TechgunsExpanded");

    private static final int VEIN_SIZE_MIN = 1;
    private static final int VEIN_SIZE_MAX = 7;
    private static final int ATTEMPTS_PER_CHUNK = 2;

    // Avoid bedrock layers
    private static final int Y_MIN = 8;
    private static final int Y_MAX = 118;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimensionType() != DimensionType.NETHER) {
            return;
        }
        generateNether(random, chunkX, chunkZ, world);
    }

    private void generateNether(Random random, int chunkX, int chunkZ, World world) {
        IBlockState oreState = ModBlocks.TUNGSTEN_CARBIDE_ORE.getDefaultState();
        int totalPlaced = 0;

        int chunkBaseX = chunkX * 16;
        int chunkBaseZ = chunkZ * 16;

        for (int i = 0; i < ATTEMPTS_PER_CHUNK; i++) {
            int startX = chunkBaseX + random.nextInt(16);
            int startZ = chunkBaseZ + random.nextInt(16);
            int startY = Y_MIN + random.nextInt(Y_MAX - Y_MIN + 1);

            // Skip Soul Sand Valley biome (mod-added, not in vanilla 1.12.2)
            Biome biome = world.getBiome(new BlockPos(startX, 64, startZ));
            if (biome.getRegistryName() != null) {
                String name = biome.getRegistryName().toString().toLowerCase();
                if (name.contains("soul_sand") || name.contains("soulsand")) {
                    continue;
                }
            }

            int veinSize = VEIN_SIZE_MIN + random.nextInt(VEIN_SIZE_MAX - VEIN_SIZE_MIN + 1);
            totalPlaced += placeVein(world, random, startX, startY, startZ,
                    oreState, veinSize, chunkBaseX, chunkBaseZ);
        }

        LOGGER.info("[TechgunsExpanded] Placed {} ore blocks in Nether chunk [{}, {}]",
                totalPlaced, chunkX, chunkZ);
    }

    /**
     * Places ore via random walk. Stays within the chunk column to avoid loading adjacent chunks.
     * Returns number of blocks actually placed.
     */
    private int placeVein(World world, Random rand, int startX, int startY, int startZ,
                           IBlockState ore, int size, int chunkBaseX, int chunkBaseZ) {
        int placed = 0;
        int x = startX;
        int y = startY;
        int z = startZ;

        for (int i = 0; i < size; i++) {
            // Clamp to chunk column to never touch adjacent chunks
            x = Math.max(chunkBaseX, Math.min(chunkBaseX + 15, x));
            z = Math.max(chunkBaseZ, Math.min(chunkBaseZ + 15, z));
            y = Math.max(Y_MIN, Math.min(Y_MAX, y));

            BlockPos pos = new BlockPos(x, y, z);
            if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK) {
                world.setBlockState(pos, ore, 2);
                placed++;
            }

            // Random walk to adjacent block
            switch (rand.nextInt(6)) {
                case 0: y++; break;
                case 1: y--; break;
                case 2: z--; break;
                case 3: z++; break;
                case 4: x--; break;
                case 5: x++; break;
            }
        }

        return placed;
    }
}

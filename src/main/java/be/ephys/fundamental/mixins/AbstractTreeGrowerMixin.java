package be.ephys.fundamental.mixins;

import be.ephys.fundamental.biome_trees.BiomeTreeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(AbstractTreeGrower.class)
public abstract class AbstractTreeGrowerMixin {
  @Unique
  private final Random mc_fundamental$random = new Random();

  @Shadow @Nullable protected abstract Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204307_, boolean p_204308_);

  @Inject(method = "growTree", at = @At("HEAD"), cancellable = true)
  private void onGrowTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState blockState, Random random, CallbackInfoReturnable<Boolean> cir) {
    AbstractTreeGrower treeGrower = (AbstractTreeGrower) (Object) this;

    Holder<? extends ConfiguredFeature<?, ?>> holder = this.getConfiguredFeature(this.mc_fundamental$random, false);

    if (holder == null && treeGrower instanceof AbstractMegaTreeGrower megaTreeGrower) {
      holder = megaTreeGrower.getConfiguredMegaFeature(this.mc_fundamental$random);
    }

    if (holder == null) {
      return;
    }

    boolean success = BiomeTreeModule.spawnBiomeTree(world, chunkGenerator, pos, blockState, random, holder.value());

    if (success) {
      cir.setReturnValue(true);
    }
  }
}

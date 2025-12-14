package tk.chroma.antiplots.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.chroma.antiplots.client.AntiplotsClient;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Shadow public Camera camera;
    private static final int MAX_DISTANCE = 6;

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private <E extends BlockEntity> void renderMixin(E blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
        if (AntiplotsClient.hidingSigns() && distSquared(blockEntity.getBlockPos(), this.camera.getBlockPosition()) > MAX_DISTANCE * MAX_DISTANCE) {
            ci.cancel();
        }
    }

    private int distSquared(BlockPos b1, BlockPos b2) {
        // block positions are integer only but the usual distSqr still calculates distance between two by converting to doubles first
        int x = b1.getX() - b2.getX();
        int y = b1.getY() - b2.getY();
        int z = b1.getZ() - b2.getZ();
        return x * x + y * y + z * z;
    }
}

package maowcraft.tildehud;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class TildeHudClient implements ClientModInitializer {
    private MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((delta) -> {
            if (!client.options.debugEnabled && client.getWindow().getWidth() != 0 && client.getWindow().getHeight() != 0) {
                TextRenderer textRenderer = client.textRenderer;
                assert client.player != null;
                Vec3d playerPos = client.player.getPos();
                int x = 0;
                int y = 0;
                x = client.getWindow().getX() / client.getWindow().getWidth() + 5;
                y = client.getWindow().getY() / client.getWindow().getHeight();

                long playerX = Math.round(playerPos.getX());
                long playerY = Math.round(playerPos.getY());
                long playerZ = Math.round(playerPos.getZ());

                Direction rawDirection = client.player.getHorizontalFacing();
                String planeDirection;
                switch (rawDirection) {
                    case NORTH:
                        planeDirection = "-Z";
                        break;
                    case SOUTH:
                        planeDirection = "+Z";
                        break;
                    case WEST:
                        planeDirection = "-X";
                        break;
                    case EAST:
                        planeDirection = "+X";
                        break;
                    default:
                        planeDirection = "INVALID";
                }
                String formattedDirection = String.format(" [%s] [%s]", rawDirection, planeDirection);

                String[] splitFpsDebugString = client.fpsDebugString.split(" ");

                int framesPerSecond = Integer.parseInt(splitFpsDebugString[0]);
                float ticksPerSecond = 0;
                if (client.getServer() != null) {
                    ticksPerSecond = Math.round(client.player.getServer().getTickTime());
                }
                float time = Math.round(client.world.getTime());
                int lightLevel = client.world.getLightLevel(client.player.getBlockPos());

                int i = client.getWindow().getScaledHeight() - 9;

                if (client.player.dimension == DimensionType.OVERWORLD) {
                    long netherX = Math.round(playerX / 8);
                    long netherZ = Math.round(playerZ / 8);

                    textRenderer.drawWithShadow(netherX + " " + playerY + " " + netherZ, x, i - 15, 14680064);
                    textRenderer.drawWithShadow(playerX + " " + playerY + " " + playerZ + " " + formattedDirection.toUpperCase() + " [" + I18n.translate(client.world.getBiome(client.player.getBlockPos()).getTranslationKey()) + "]", x, i - 5, 16777215);
                } else if (client.player.dimension == DimensionType.THE_NETHER) {
                    long overworldX = Math.round(playerX * 8);
                    long overworldZ = Math.round(playerZ * 8);

                    textRenderer.drawWithShadow(playerX + " " + playerY + " " + playerZ, x, i - 15, 14680064);
                    textRenderer.drawWithShadow(overworldX + " " + playerY + " " + overworldZ + " " + formattedDirection.toUpperCase(), x, i - 5, 16777215);
                }
                
                textRenderer.drawWithShadow("FPS: " + framesPerSecond, x, y + 5, 16777215);
                textRenderer.drawWithShadow("Time: " + time, x, y + 15, 31683);
                textRenderer.drawWithShadow("Light Level: " + lightLevel, x, y + 25, 16759552);
                if (client.getServer() != null) {
                    textRenderer.drawWithShadow("TPS: " + ticksPerSecond, x, y + 35, 47360);
                }

                ItemStack stack = client.player.inventory.getStack(client.player.inventory.selectedSlot);

                if (stack != ItemStack.EMPTY && stack.getMaxDamage() > 0) {
                    if (stack.isDamaged()) {
                        textRenderer.drawWithShadow(stack.getMaxDamage() - stack.getDamage() + "/" + stack.getMaxDamage(), x, i - 25, 2236962);
                    }
                }
            }
        });
    }
}

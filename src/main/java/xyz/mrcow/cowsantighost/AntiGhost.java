package xyz.mrcow.cowsantighost;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

public class AntiGhost implements ClientModInitializer {

	private KeyBinding reveal;
	private DoubleOption rangeOption;

	@Override
	public void onInitializeClient() {
		System.out.println("Moo!");
		reveal = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("key.antighost.reveal",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				"key.category.cowutils"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (reveal.wasPressed()) {
				client.player.sendMessage(new LiteralText("Sending requests"), false);
				requestBlocks();
			}
		});
	}

	public void requestBlocks(){
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayNetworkHandler conn = mc.getNetworkHandler();
		if (conn==null)
			return;
		BlockPos pos=mc.player.getBlockPos();
		for (double dx=-range; dx<=range; dx++)
			for (double dy=-range; dy<=range; dy++)
				for (double dz=-range; dz<=range; dz++) {
					PlayerActionC2SPacket packet=new PlayerActionC2SPacket(
							PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
							new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz),
							Direction.UP       // with ABORT_DESTROY_BLOCK, this value is unused
					);
					conn.sendPacket(packet);
				}
	}

	private double range = 16;

	double getRange(){
		return range;
	}

	void setRange(double newRange){
		this.range = newRange;
	}
}

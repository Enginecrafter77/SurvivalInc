package net.schoperation.schopcraft.packet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.schoperation.schopcraft.SchopCraft;

public class SchopPackets {
	
	public static SimpleNetworkWrapper net;
	
	public static void initPackets() {
		
		net = NetworkRegistry.INSTANCE.newSimpleChannel(SchopCraft.MOD_ID);
		registerMessage(WetnessPacket.class, WetnessPacket.WetnessMessage.class);
		registerMessage(ThirstPacket.class, ThirstPacket.ThirstMessage.class);
		registerMessage(SanityPacket.class, SanityPacket.SanityMessage.class);
		registerMessage(TemperaturePacket.class, TemperaturePacket.TemperatureMessage.class);
		registerMessage(GhostPacket.class, GhostPacket.GhostMessage.class);
		registerMessage(SummonInfoPacket.class, SummonInfoPacket.SummonInfoMessage.class);
		registerMessage(PotionEffectPacket.class, PotionEffectPacket.PotionEffectMessage.class);
		
	}
	
	// Packet ID to keep the packets separate.
	private static int packetId = 0;
	
	private static void registerMessage(Class packet, Class message) {
		
		net.registerMessage(packet, message, packetId, Side.CLIENT);
		net.registerMessage(packet, message, packetId, Side.SERVER);
		packetId++;
	}
}
package com.test.rawplacepacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
@Mod(modid = "rawplacepacket", name = "RawPlacePacket", version = "1.0")
public class RawPlacePacket {
    public static final String MODID = "rawplacepacket";
    public static KeyBinding rawPlaceKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        rawPlaceKey = new KeyBinding("key.rawplace", Keyboard.KEY_R, "RawPlacePacket");
        ClientRegistry.registerKeyBinding(rawPlaceKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null || mc.world == null || mc.getConnection() == null)
        {
            return;
        }

        if (!rawPlaceKey.isPressed())
        {
            return;
        }

        double reach = mc.player.getEntityAttribute(net.minecraft.entity.ai.attributes.Attributes.REACH_DISTANCE).getAttributeValue();
        mc.objectMouseOver = mc.rayTrace(reach, 1.0F);

        if (mc.objectMouseOver == null || mc.objectMouseOver.getBlockPos() == null)
        {
            return;
        }

        BlockPos targetPos = mc.objectMouseOver.getBlockPos();
        EnumFacing hitSide = mc.objectMouseOver.sideHit;
        Vec3d hitVec = mc.objectMouseOver.hitVec;
        ItemStack heldItem = mc.player.getHeldItem(EnumHand.MAIN_HAND);

        if (heldItem.isEmpty())
        {
            return;
        }

        NetHandlerPlayClient netHandler = mc.getConnection();

        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(
                targetPos,
                hitSide.getIndex(),
                heldItem,
                (float) (hitVec.x - targetPos.getX()),
                (float) (hitVec.y - targetPos.getY()),
                (float) (hitVec.z - targetPos.getZ())
        );

        netHandler.sendPacket(packet);
    }
}

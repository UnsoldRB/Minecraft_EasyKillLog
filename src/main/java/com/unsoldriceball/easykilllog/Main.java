package com.unsoldriceball.easykilllog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;


@Mod(modid = Main.MOD_ID, acceptableRemoteVersions = "*")
public class Main
{
    public static final String MOD_ID = "easykilllog";
    public static final String MOD_ID_PETSRETREAT = "petsretreat";



    //ModがInitializeを呼び出す前に発生するイベント。
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //これでこのクラス内でForgeのイベントが動作するようになるらしい。
        MinecraftForge.EVENT_BUS.register(this);
    }




    //Entityが死亡したときのイベント
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote) return;
        if (event.getSource().getTrueSource() == null) return;
        if (event.getEntityLiving() == null) return;

        final Entity L_ATTACKER = event.getSource().getTrueSource();
        final EntityLivingBase L_VICTIM = event.getEntityLiving();


        //攻撃したのがプレイヤーなら
        if (L_ATTACKER instanceof EntityPlayer)
        {
            final EntityPlayer L_ATTACKER_PLAYER = (EntityPlayer) L_ATTACKER;

            showActionBar(L_ATTACKER_PLAYER, L_ATTACKER_PLAYER, L_VICTIM);
        }
        //PetsRetreat用の処理
        else if (L_ATTACKER instanceof EntityLivingBase)
        {
            final EntityLivingBase L_ATTACKER_ENTITY = (EntityLivingBase) L_ATTACKER;
            UUID uuid_owner = null;

            //EntityからownerのUUIDを抜き取る
            for(String _t : L_ATTACKER_ENTITY.getTags())
            {
                if (!_t.contains("@" + MOD_ID_PETSRETREAT)) continue;
                uuid_owner = UUID.fromString(_t.replace("@" + MOD_ID_PETSRETREAT + "_", ""));
                break;
            }

            if (uuid_owner != null)
            {
                final EntityPlayer L_OWNER = L_ATTACKER_ENTITY.world.getPlayerEntityByUUID(uuid_owner);

                if (L_OWNER != null)
                {
                    showActionBar(L_OWNER, L_ATTACKER_ENTITY, L_VICTIM);
                }
            }
        }
    }



    private void showActionBar(EntityPlayer p, Entity attacker, Entity victim)
    {
        final String L_NAME_ATTACKER = attacker.getDisplayName().getFormattedText();
        final String L_NAME_VICTIM = victim.getDisplayName().getFormattedText();
        p.sendStatusMessage(new TextComponentString(L_NAME_ATTACKER + "§r-> " + L_NAME_VICTIM + "   "), true);
    }
}

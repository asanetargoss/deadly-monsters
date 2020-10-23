package com.dmonsters.ai;

import java.util.Random;

import com.dmonsters.main.MainMod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public abstract class DeadlyMonsterAIBase extends EntityAIBase {
    public static boolean canAttack(Random random, EntityLivingBase attacker, EntityLivingBase target) {
        if (attacker.worldObj != target.worldObj || attacker.worldObj == null) {
            MainMod.logger.error("Deadly monster " + attacker.getClass().getName() +
                    " attempted to attack target " + target.getClass().getName() +
                    " in another world.");
            return false;
        }
        int i = 0;
        float heightFraction = 0.5F;
        do {
            Vec3d start = new Vec3d(attacker.posX, attacker.lastTickPosY + (heightFraction * attacker.height), attacker.posZ);
            Vec3d end = new Vec3d(target.posX, target.lastTickPosY + (heightFraction * target.height), target.posZ);
            if (attacker.worldObj.rayTraceBlocks(start, end, false, true, false) == null) {
                return true;
            }
            heightFraction = random.nextFloat();
            i++;
        } while (i < 3);
        return false;
    }
}

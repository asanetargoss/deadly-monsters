package com.dmonsters.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class DeadlyMonsterAIMeelee extends EntityAIAttackMelee
{
    public DeadlyMonsterAIMeelee(EntityCreature creature, double speed, boolean useLongMemory) {
        super(creature, speed, useLongMemory);
    }
}
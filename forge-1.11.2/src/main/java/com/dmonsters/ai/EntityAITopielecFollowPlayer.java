package com.dmonsters.ai;

import com.dmonsters.entity.EntityTopielec;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class EntityAITopielecFollowPlayer extends EntityAIBase {
    private final EntityTopielec topielec;
    private final float speed;
    
    public EntityAITopielecFollowPlayer(EntityTopielec _owner, float _speed) {
        this.topielec = _owner;
        this.speed = _speed;
    }

    public boolean shouldExecute() {
    	if (this.topielec.getAttackTarget() != null)
    		return true;
        return false;
    }

    public void updateTask() {
    	BlockPos resultPos = this.topielec.getAttackTarget().getPosition().subtract(this.topielec.getPosition());
    	float[] normVec = normlizeVector(resultPos);
    	this.topielec.setMovementVector(normVec[0], normVec[1], normVec[2]);
    }
    
    private float[] normlizeVector(BlockPos v) {
    	float length = (float) Math.sqrt((v.getX() * v.getX()) + (v.getY() * v.getY()) + (v.getZ() * v.getZ()));
    	float[] newVec = new float[3];
		newVec[0] = (v.getX() / length) * speed;
		newVec[1] = (v.getY() / length) * speed;
		newVec[2] = (v.getZ() / length) * speed;
    	return newVec;
    }
}
package com.dmonsters.entity;

import javax.annotation.Nullable;

import com.dmonsters.ai.DeadlyMonsterAIMeelee;
import com.dmonsters.main.MainMod;
import com.dmonsters.main.ModBlocks;
import com.dmonsters.main.ModConfig;
import com.dmonsters.main.ModSounds;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityPresent extends EntityMob {
    public static final ResourceLocation LOOT = new ResourceLocation(MainMod.MODID, "present");
    
    private int cageTicks = 0;
    private boolean debugCage;

    public EntityPresent(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.5F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D * ModConfig.speedMultiplier * ModConfig.entrailSpeedMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D * ModConfig.strengthMultiplier * ModConfig.entrailStrengthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(26.0D * ModConfig.healthMultiplier * ModConfig.entrailHealthMultiplier);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new DeadlyMonsterAIMeelee(this, 1.0D, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    	this.applyEntityAI();
    }

    private void applyEntityAI() {
    	this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
        	this.playSound(ModSounds.PRESENT_ATTACK, 1, 1);
        	if (entityIn instanceof EntityPlayer) {
        		makeCage((EntityPlayer) entityIn);
        	}
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void onLivingUpdate() {
    	if (debugCage) {
    		cageTicks++;
    		if (cageTicks == 400) {
    			cageTicks = 0;
    			debugCage = false;
    		}
    	}
        super.onLivingUpdate();
    }

    private void makeCage(EntityPlayer player) {
    	if (debugCage)
    		return;
    	debugCage = true;
    	
    	int height = 3;
    	int hSize = 3;
    	int vSize = 7;
    	int xPos = (int)this.posX;
    	int yPos = (int)this.posY;
    	int zPos = (int)this.posZ;
    	BlockPos pos;
    	Block block;
    	int xCent;
    	int zCent;
    	int hCenter = (int)(hSize * 0.5F) - 1;
    	//walls
    	for (int i = 0; i < vSize; i++) {
	    	for (int x = -hSize; x < hSize + 1; x++) {
	        	for (int z = -hSize; z < hSize + 1; z++) {
	        		//System.out.println("EntityPresent:: x:" + x + ", y: " + z + ", i: " + i);
	        		if (x == -hSize || x == hSize || z == -hSize || z == hSize) {
		        		pos = new BlockPos(x + xPos, yPos + height + i, z + zPos);
		        		block = this.worldObj.getBlockState(pos).getBlock();
		        		if (block == Blocks.AIR) {
		        			if (x == hCenter ||
		        				z == hCenter  )
		        				this.worldObj.setBlockState(pos, ModBlocks.presentBlock.getStateFromMeta(1));
		        			else
		        				this.worldObj.setBlockState(pos, ModBlocks.presentBlock.getStateFromMeta(0));
		        		}
	        		} else if (i == 0 || i == vSize - 1) {
	            		pos = new BlockPos(x + xPos, yPos + i + height, z + zPos);
		        		block = this.worldObj.getBlockState(pos).getBlock();
		        		if (block == Blocks.AIR) {
		        			xCent = hCenter;
		        			zCent = hCenter;
		        			if (x == xCent && z == zCent) {
		        				BlockPos lightPos = new BlockPos(xCent + xPos, yPos + height + 1, zCent + zPos);
			        			this.worldObj.setBlockState(lightPos, Blocks.TORCH.getDefaultState());
			        			if (!this.worldObj.isRemote) {
				                	Entity creeper = new EntityCreeper(this.worldObj);
				                	creeper.setPosition(lightPos.getX(), lightPos.getY(), lightPos.getZ());
				                	this.worldObj.spawnEntityInWorld(creeper);
			        			}
		        			}
		        			if (x == 0 || z == 0)
		        				this.worldObj.setBlockState(pos, ModBlocks.presentBlock.getStateFromMeta(1));
		        			else
		        				this.worldObj.setBlockState(pos, ModBlocks.presentBlock.getStateFromMeta(0));
		        		}
	        		}
	        	}
	    	}
    	}
    	
    	player.setPositionAndUpdate(xPos + hSize * 0.5F - 1, yPos + height + 1, zPos + hSize * 0.5F - 1);
    }
    
    @Override
    protected SoundEvent getDeathSound()
    {
    	return ModSounds.PRESENT_DEATH;
    }
    
    @Override
    protected SoundEvent getAmbientSound()
    {
    	return ModSounds.PRESENT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
    	return ModSounds.PRESENT_HURT;
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    public boolean getCanSpawnHere()
    {
    	BlockPos pos = this.getPosition();    	
        return super.getCanSpawnHere() && this.worldObj.canSeeSky(pos);
    }
}

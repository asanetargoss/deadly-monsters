package com.dmonsters.entity;

import javax.annotation.Nullable;

import com.dmonsters.ai.DeadlyMonsterAIMeelee;
import com.dmonsters.main.MainMod;
import com.dmonsters.main.ModConfig;
import com.dmonsters.main.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWideman extends EntityMob {
    private static final DataParameter<Boolean> ARMS_RAISED = EntityDataManager.createKey(EntityWideman.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityWideman.class, DataSerializers.BOOLEAN);
    
    public static final ResourceLocation LOOT = new ResourceLocation(MainMod.MODID, "wideman");
    

    public EntityWideman(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.95F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(ARMS_RAISED, Boolean.valueOf(false));
        this.getDataManager().register(ATTACKING, Boolean.valueOf(false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D * ModConfig.speedMultiplier * ModConfig.fallenLeaderSpeedMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20.0D * ModConfig.strengthMultiplier * ModConfig.fallenLeaderStrengthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D * ModConfig.healthMultiplier * ModConfig.fallenLeaderHealthMultiplier);
    }
    
    @Override
    public void onUpdate() {
    	super.onUpdate();
    }
    
    public void onLivingUpdate() {
        if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
            float f = this.getBrightness(1.0F);
            BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ);
            if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canSeeSky(blockpos)) {
            	this.setFire(8);
            }
        }
        super.onLivingUpdate();
    }


   public void setArmsRaised(boolean armsRaised) {
        this.getDataManager().set(ARMS_RAISED, Boolean.valueOf(armsRaised));
    }
   
   public void setAttaking(boolean mode) {
       this.getDataManager().set(ATTACKING, Boolean.valueOf(mode));
   }

    @SideOnly(Side.CLIENT)
    public boolean isArmsRaised() {
        return this.getDataManager().get(ARMS_RAISED).booleanValue();
    }
    
    public boolean getAttaking() {
        return this.getDataManager().get(ATTACKING).booleanValue();
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new DeadlyMonsterAIMeelee(this, 1.0D, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    	this.applyEntityAI();
    }

    private void applyEntityAI() {
    	this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn)) {
        	this.heal(60);
        	this.playSound(ModSounds.WIDEMAN_ATTACK, 1, 1);
        	System.out.println(this.getHealth());
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected SoundEvent getDeathSound()
    {
    	return ModSounds.WIDEMAN_DEATH;
    }
    
    @Override
    protected SoundEvent getAmbientSound()
    {
    	return ModSounds.WIDEMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
    	return ModSounds.WIDEMAN_HURT;
    }
    
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }
}

package com.minecolonies.colony.workorders;

import com.minecolonies.colony.CitizenData;
import com.minecolonies.colony.Colony;
import com.minecolonies.colony.buildings.Building;
import com.minecolonies.colony.jobs.JobBuilder;
import com.minecolonies.util.BlockPosUtil;
import com.minecolonies.util.Schematic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class WorkOrderBuild extends WorkOrder
{
    private static final String TAG_BUILDING      = "building";
    private static final String TAG_UPGRADE_LEVEL = "upgradeLevel";
    private static final String TAG_UPGRADE_NAME  = "upgrade";
    private static final String TAG_IS_CLEARED    = "isCleared";
    protected     BlockPos  buildingId;
    private       int       upgradeLevel;
    private       String    upgradeName;
    private       boolean   isCleared;
    protected Schematic schematic;

    public WorkOrderBuild()
    {
        super();
    }

    public WorkOrderBuild(Building building, int level)
    {
        super();
        this.buildingId = building.getID();
        this.upgradeLevel = level;
        this.upgradeName = building.getSchematicName() + level;
        if (level > 0)
        {
            this.isCleared = true;
        }
    }

    /**
     * Does this workorder have a loaded Schematic?
     * <p>
     * if a schematic is not null there exists a location for it
     *
     * @return true if there is a loaded schematic for this workorder
     */
    public boolean hasSchematic()
    {
        return schematic != null;
    }

    public boolean isCleared()
    {
        return isCleared;
    }

    public void setCleared(final boolean cleared)
    {
        isCleared = cleared;
    }

    /**
     * Returns the ID of the building (aka ChunkCoordinates)
     *
     * @return ID of the building
     */
    public BlockPos getBuildingId()
    {
        return buildingId;
    }

    /**
     * Returns the level up level of the building
     *
     * @return Level after upgrade
     */
    public int getUpgradeLevel()
    {
        return upgradeLevel;
    }

    /**
     * Returns the name after upgrade
     *
     * @return Name after yograde
     */
    public String getUpgradeName()
    {
        return upgradeName;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        BlockPosUtil.writeToNBT(compound, TAG_BUILDING, buildingId);
        compound.setInteger(TAG_UPGRADE_LEVEL, upgradeLevel);
        compound.setString(TAG_UPGRADE_NAME, upgradeName);
        compound.setBoolean(TAG_IS_CLEARED, isCleared);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        buildingId = BlockPosUtil.readFromNBT(compound, TAG_BUILDING);
        upgradeLevel = compound.getInteger(TAG_UPGRADE_LEVEL);
        upgradeName = compound.getString(TAG_UPGRADE_NAME);
        isCleared = compound.getBoolean(TAG_IS_CLEARED);
    }

    @Override
    public boolean isValid(Colony colony)
    {
        return colony.getBuilding(buildingId) != null;
    }

    @Override
    public void attemptToFulfill(Colony colony)
    {
        for (CitizenData citizen : colony.getCitizens().values())
        {
            JobBuilder job = citizen.getJob(JobBuilder.class);
            if (job == null || job.hasWorkOrder())
            {
                continue;
            }

            //  A Build WorkOrder may be fulfilled by a Builder as long as any ONE of the following is true:
            //  - The Builder's Work Building is built
            //  - OR the WorkOrder is for the Builder's Work Building
            //  - OR the WorkOrder is for the TownHall
            if (citizen.getWorkBuilding().getBuildingLevel() > 0 ||
                citizen.getWorkBuilding().getID().equals(buildingId) ||
                (colony.hasTownHall() && colony.getTownHall().getID().equals(buildingId)))
            {
                job.setWorkOrder(this);
                this.setClaimedBy(citizen);
                return;
            }
        }
    }

    /**
     * return the current schematic for this buildjob
     * @return null if none set
     */
    public Schematic getSchematic()
    {
        return schematic;
    }

    /**
     * set a new schematic for this build job
     * todo: make obsolete
     * @param schematic the new schematic to use
     */
    public void setSchematic(final Schematic schematic)
    {
        this.schematic = schematic;
    }
}

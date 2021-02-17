package com.fusionflux.fluxtech.blocks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class HighSpeedRail extends AbstractRailBlock {

    public static final EnumProperty<RailShape> SHAPE;
    public static final BooleanProperty POWERED;

    protected HighSpeedRail(boolean allowCurves, Settings settings) {
        super(allowCurves, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SHAPE, RailShape.NORTH_SOUTH)).with(POWERED, false));
    }



    protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean boolean4, int distance) {
        if (distance >= 8) {
            return false;
        } else {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            boolean bl = true;
            RailShape railShape = (RailShape)state.get(SHAPE);
            switch(railShape) {
                case NORTH_SOUTH:
                    if (boolean4) {
                        ++k;
                    } else {
                        --k;
                    }
                    break;
                case EAST_WEST:
                    if (boolean4) {
                        --i;
                    } else {
                        ++i;
                    }
                    break;
                case ASCENDING_EAST:
                    if (boolean4) {
                        --i;
                    } else {
                        ++i;
                        ++j;
                        bl = false;
                    }

                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_WEST:
                    if (boolean4) {
                        --i;
                        ++j;
                        bl = false;
                    } else {
                        ++i;
                    }

                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_NORTH:
                    if (boolean4) {
                        ++k;
                    } else {
                        --k;
                        ++j;
                        bl = false;
                    }

                    railShape = RailShape.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:
                    if (boolean4) {
                        ++k;
                        ++j;
                        bl = false;
                    } else {
                        --k;
                    }

                    railShape = RailShape.NORTH_SOUTH;
            }

            if (this.isPoweredByOtherRails(world, new BlockPos(i, j, k), boolean4, distance, railShape)) {
                return true;
            } else {
                return bl && this.isPoweredByOtherRails(world, new BlockPos(i, j - 1, k), boolean4, distance, railShape);
            }
        }
    }

    protected boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isOf(this)) {
            return false;
        } else {
            RailShape railShape = (RailShape)blockState.get(SHAPE);
            if (shape == RailShape.EAST_WEST && (railShape == RailShape.NORTH_SOUTH || railShape == RailShape.ASCENDING_NORTH || railShape == RailShape.ASCENDING_SOUTH)) {
                return false;
            } else if (shape == RailShape.NORTH_SOUTH && (railShape == RailShape.EAST_WEST || railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST)) {
                return false;
            } else if ((Boolean)blockState.get(POWERED)) {
                return world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, blockState, bl, distance + 1);
            } else {
                return false;
            }
        }
    }

    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
        boolean bl = (Boolean)state.get(POWERED);
        boolean bl2 = world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, state, true, 0) || this.isPoweredByOtherRails(world, pos, state, false, 0);
        if (bl2 != bl) {
            world.setBlockState(pos, (BlockState)state.with(POWERED, bl2), 3);
            world.updateNeighborsAlways(pos.down(), this);
            if (((RailShape)state.get(SHAPE)).isAscending()) {
                world.updateNeighborsAlways(pos.up(), this);
            }
        }

    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch(rotation) {
            case CLOCKWISE_180:
                switch((RailShape)state.get(SHAPE)) {
                    case ASCENDING_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                }
            case COUNTERCLOCKWISE_90:
                switch((RailShape)state.get(SHAPE)) {
                    case NORTH_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                }
            case CLOCKWISE_90:
                switch((RailShape)state.get(SHAPE)) {
                    case NORTH_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                }
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = (RailShape)state.get(SHAPE);
        switch(mirror) {
            case LEFT_RIGHT:
                switch(railShape) {
                    case ASCENDING_NORTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirror);
                }
            case FRONT_BACK:
                switch(railShape) {
                    case ASCENDING_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return (BlockState)state.with(SHAPE, RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirror);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED);
    }

    static {
        SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
        POWERED = Properties.POWERED;
    }
}

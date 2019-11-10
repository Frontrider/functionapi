package io.github.cottonmc.functionapi.content.templates.impl.block;

import io.github.cottonmc.functionapi.api.ExtendedBlockProperties;
import io.github.cottonmc.functionapi.content.templates.BlockTemplate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.List;

import static net.minecraft.block.StairsBlock.HALF;
import static net.minecraft.block.StairsBlock.SHAPE;
import static net.minecraft.block.StairsBlock.FACING;
import static net.minecraft.block.StairsBlock.WATERLOGGED;

/**
 * Block that takes the block template and builds up itself from it.
 */
public class TemplatedBlock extends Block implements ExtendedBlockProperties {
    private BlockTemplate template;

    public static BlockTemplate currentTemplate;


    public TemplatedBlock(BlockTemplate template, Settings blockSettings) {
        super(blockSettings);
        this.template = template;

    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        List<AbstractProperty> properties = currentTemplate.getProperties();
        if (!properties.isEmpty()) {
            for (AbstractProperty property : properties) {
                stateFactory$Builder_1 = stateFactory$Builder_1.add(property);
            }
        }
        if(currentTemplate.getType() == BlockTemplate.Type.STAIRS){
            stateFactory$Builder_1 = stateFactory$Builder_1.add(FACING, HALF, SHAPE, WATERLOGGED);
        }
        super.appendProperties(stateFactory$Builder_1);

    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return template.getRenderLayer();
    }

    @Override
    public boolean hasRandomTicks(BlockState blockState_1) {
        return template.ticksRandomly();
    }

    @Override
    public boolean isAir(BlockState blockState_1) {
        return template.isAir();
    }

    @Override
    public boolean allowsSpawning(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityType<?> entityType_1) {
        return template.canSpawnMobs();
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        if (template.isInvisible()) {
            return BlockRenderType.INVISIBLE;
        }
        return super.getRenderType(blockState_1);
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState blockState_1) {
        return super.getSoundGroup(blockState_1);
    }

    @Override
    public boolean isBlockStairs() {
        return template.getType() == BlockTemplate.Type.STAIRS;
    }
}

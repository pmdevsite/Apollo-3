package net.minecraft.item;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock
{
    public ItemBanner()
    {
        super(Blocks.STANDING_BANNER);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        boolean flag = iblockstate.getBlock().isReplaceable(worldIn, pos);

        if (facing != EnumFacing.DOWN && (iblockstate.getMaterial().isSolid() || flag) && (!flag || facing == EnumFacing.UP))
        {
            pos = pos.offset(facing);

            if (playerIn.canPlayerEdit(pos, facing, stack) && Blocks.STANDING_BANNER.canPlaceBlockAt(worldIn, pos))
            {
                if (worldIn.isRemote)
                {
                    return EnumActionResult.SUCCESS;
                }
                else
                {
                    pos = flag ? pos.down() : pos;

                    if (facing == EnumFacing.UP)
                    {
                        int i = MathHelper.floor_double((double)((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                        worldIn.setBlockState(pos, Blocks.STANDING_BANNER.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
                    }
                    else
                    {
                        worldIn.setBlockState(pos, Blocks.WALL_BANNER.getDefaultState().withProperty(BlockWallSign.FACING, facing), 3);
                    }

                    --stack.stackSize;
                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntityBanner)
                    {
                        ((TileEntityBanner)tileentity).setItemValues(stack);
                    }

                    return EnumActionResult.SUCCESS;
                }
            }
            else
            {
                return EnumActionResult.FAIL;
            }
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = "item.banner.";
        EnumDyeColor enumdyecolor = getBaseColor(stack);
        s = s + enumdyecolor.getUnlocalizedName() + ".name";
        return I18n.translateToLocal(s);
    }

    public static EnumDyeColor getBaseColor(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getSubCompound("BlockEntityTag", false);
        EnumDyeColor enumdyecolor;

        if (nbttagcompound != null && nbttagcompound.hasKey("Base"))
        {
            enumdyecolor = EnumDyeColor.byDyeDamage(nbttagcompound.getInteger("Base"));
        }
        else
        {
            enumdyecolor = EnumDyeColor.byDyeDamage(stack.getMetadata());
        }

        return enumdyecolor;
    }
}

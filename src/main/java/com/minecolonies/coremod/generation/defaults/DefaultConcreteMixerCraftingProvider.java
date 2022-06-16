package com.minecolonies.coremod.generation.defaults;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.crafting.ItemStorage;
import com.minecolonies.coremod.generation.CustomRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** Datagen for concrete mixer crafterrecipes */
public class DefaultConcreteMixerCraftingProvider extends CustomRecipeProvider
{
    public DefaultConcreteMixerCraftingProvider(@NotNull final DataGenerator generatorIn)
    {
        super(generatorIn);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "DefaultConcreteMixerCraftingProvider";
    }

    @Override
    public void run(final CachedOutput cache) throws IOException
    {

    }

    @Override
    protected void registerRecipes(@NotNull final Consumer<FinishedRecipe> consumer)
    {
        final List<ItemStorage> input = new ArrayList<>();
        input.add(new ItemStorage(new ItemStack(Items.SAND, 4)));
        input.add(new ItemStorage(new ItemStack(Items.GRAVEL, 4)));

        for (final DyeColor color : DyeColor.values())
        {
            final String prefix = color.name().toLowerCase(Locale.ROOT);
            final Item powder = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix + "_concrete_powder"));
            final Item concrete = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix + "_concrete"));
            final Item dye = ForgeRegistries.ITEMS.getValue(new ResourceLocation(prefix + "_dye"));

            if (powder == null || concrete == null || dye == null)
            {
                throw new IllegalStateException("Missing items for " + color.getSerializedName());
            }

            final List<ItemStorage> customInput = new ArrayList<>(input);
            customInput.add(new ItemStorage(new ItemStack(dye)));

            CustomRecipeBuilder.create(ModJobs.CONCRETE_ID.getPath() + "_custom", ForgeRegistries.ITEMS.getKey(powder).getPath())
                    .inputs(customInput)
                    .result(new ItemStack(powder, 8))
                    .build(consumer);

            CustomRecipeBuilder.create(ModJobs.CONCRETE_ID.getPath() + "_custom", ForgeRegistries.ITEMS.getKey(concrete).getPath())
                    .inputs(Collections.singletonList(new ItemStorage(new ItemStack(powder))))
                    .result(new ItemStack(concrete))
                    //.intermediate(Blocks.WATER)
                    .build(consumer);
            // TODO: it makes sense for this to have WATER as an intermediate, but the RS logic
            //       and JEI rendering don't currently support that.  Previous versions just used
            //       air, so we'll do the same for now.
        }
    }
}

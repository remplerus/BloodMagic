package wayoftime.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import wayoftime.bloodmagic.recipe.flask.RecipePotionCycle;
import wayoftime.bloodmagic.recipe.flask.RecipePotionEffect;
import wayoftime.bloodmagic.util.Constants;

public class PotionCycleRecipeSerializer<RECIPE extends RecipePotionCycle> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public PotionCycleRecipeSerializer(IFactory<RECIPE> factory)
	{
		this.factory = factory;
	}

	@Nonnull
	@Override
	public RECIPE read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json)
	{
		List<Ingredient> inputList = new ArrayList<Ingredient>();

		if (json.has(Constants.JSON.INPUT) && JSONUtils.isJsonArray(json, Constants.JSON.INPUT))
		{
			JsonArray mainArray = JSONUtils.getJsonArray(json, Constants.JSON.INPUT);

			arrayLoop: for (JsonElement element : mainArray)
			{
				if (inputList.size() >= RecipePotionEffect.MAX_INPUTS)
				{
					break arrayLoop;
				}

				if (element.isJsonArray())
				{
					element = element.getAsJsonArray();
				} else
				{
					element.getAsJsonObject();
				}

				inputList.add(Ingredient.deserialize(element));
			}
		}

		int numCycles = JSONUtils.getInt(json, Constants.JSON.COUNT);

		int syphon = JSONUtils.getInt(json, Constants.JSON.SYPHON);
		int ticks = JSONUtils.getInt(json, Constants.JSON.TICKS);
		int minimumTier = JSONUtils.getInt(json, Constants.JSON.ALTAR_TIER);

		return this.factory.create(recipeId, inputList, numCycles, syphon, ticks, minimumTier);
	}

	@Override
	public RECIPE read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer)
	{
		try
		{
			int size = buffer.readInt();
			List<Ingredient> input = new ArrayList<Ingredient>(size);

			for (int i = 0; i < size; i++)
			{
				input.add(i, Ingredient.read(buffer));
			}

			int syphon = buffer.readInt();
			int ticks = buffer.readInt();
			int minimumTier = buffer.readInt();

			int numCycles = buffer.readInt();

//
//			Effect outputEffect = Effect.get(buffer.readInt());
//			int baseDuration = buffer.readInt();

			return this.factory.create(recipeId, input, numCycles, syphon, ticks, minimumTier);
		} catch (Exception e)
		{
			throw e;
		}
	}

	@Override
	public void write(@Nonnull PacketBuffer buffer, @Nonnull RECIPE recipe)
	{
		try
		{
			recipe.write(buffer);
		} catch (Exception e)
		{
			throw e;
		}
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipePotionCycle>
	{
		RECIPE create(ResourceLocation id, List<Ingredient> input, int numCycles, int syphon, int ticks, int minimumTier);
	}
}

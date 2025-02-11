package wayoftime.bloodmagic.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import wayoftime.bloodmagic.potion.BloodMagicPotions;

@Mixin(LivingEntity.class)
public class MixinLivingEntity extends MixinEntity
{
	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null)
		{
			return;
		}

		boolean success = false;

		if (player.isPotionActive(BloodMagicPotions.SPECTRAL_SIGHT))
		{
			double distance = (player.getActivePotionEffect(BloodMagicPotions.SPECTRAL_SIGHT).getAmplifier() * 32 + 24);
			if (getDistanceSq(Minecraft.getInstance().player) <= (distance * distance))
			{
				if (!this.glowing)
				{
					if (!this.getFlag(6))
					{
						this.setFlag(6, true);
						this.glowing = true;
					}
				}

				success = true;
			}
		}

		if (!success && this.glowing && !this.isPotionActive(Effects.GLOWING))
		{
			this.setFlag(6, false);
			this.glowing = false;
		}
	}

	@Shadow
	public boolean isPotionActive(Effect potionIn)
	{
		throw new IllegalStateException("Mixin failed to shadow isPotionActive()");
	}
}

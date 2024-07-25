package co.skyclient.scc.mixins;

import co.skyclient.scc.cosmetics.Tag;
import co.skyclient.scc.cosmetics.TagCosmetics;
import co.skyclient.scc.hooks.NetworkPlayerInfoHook;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NetworkPlayerInfo.class)
public abstract class NetworkPlayerInfoMixin implements NetworkPlayerInfoHook {
    @Shadow public abstract GameProfile getGameProfile();

    @Unique
    private Tag scc$tag;
    @Unique
    private boolean scc$tagChecked;

    @Override
    public Tag scc$getTag() {
        if (!scc$tagChecked) {
            scc$tag = TagCosmetics.getInstance().getTag(getGameProfile().getName());
            scc$tagChecked = true;
        }
        return scc$tag;
    }

}

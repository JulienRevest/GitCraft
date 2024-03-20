package wormstweaker.gitcraft.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateGitCraftScreen extends Screen {
    protected final CreateWorldScreen parent;

//    FlatLevelGeneratorSettings generator; //TODO: Explore the FlatLevelGenerator

    public CreateGitCraftScreen(CreateWorldScreen createWorldScreen) {
        super(new TranslatableComponent("createWorld.customize.gitcraft.title"));
        this.parent = createWorldScreen;
    }

    protected void init() {

    }

//    public FlatLevelGeneratorSettings settings() { return this.generator; }
}

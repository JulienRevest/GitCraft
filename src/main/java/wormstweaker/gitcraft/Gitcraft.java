package wormstweaker.gitcraft;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.*;
import wormstweaker.gitcraft.repoUtils.RepoManager;
import wormstweaker.gitcraft.worldgen.dimensions.Dimensions;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("gitcraft")
public class Gitcraft {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "gitcraft";

    public Gitcraft() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Setup of the mod
     * @param event Forge setup event, called at the start of the game
     */
    private void setup(final FMLCommonSetupEvent event) {
        RepoManager test = new RepoManager();
        /* Lambda declaration of a Runnable, used for multi-threading */
        Runnable setup = () -> {
            test.setRepoUrl("https://github.com/DV8FromTheWorld/JDA");
//        test.setRepoUrl("https://github.com/YuwenXiong/py-R-FCN");
            try {
                test.cloneRepo();
                test.walkAllCommits();
                test.completeRelations();
                LOGGER.info(test.CURRENT_REPO.getBranchList());
            } catch (GitAPIException | IOException e) {
                LOGGER.error(e);
            }
        };
        // Run in thread to avoid lockup of the game
        Thread setupThread = new Thread(setup, "Repo setup");
        setupThread.start();

        /**
         * Dimension registration
         */
        event.enqueueWork(() -> {
            Dimensions.register();
        });
    }
}

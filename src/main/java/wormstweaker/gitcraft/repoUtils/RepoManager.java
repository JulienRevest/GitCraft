package wormstweaker.gitcraft.repoUtils;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RepoManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private String repoUrl;
    private File localPath;
    private Git repo = null;
    public Repo CURRENT_REPO;

    private final Map<String, ArrayList<Commit>> commitsMap = new HashMap<>();

    /* TODO:
        - Create a tree of all the commits, maybe with the branch as the first node
        - Figure out a way to know if a parent/child comment belongs to another branch
        - Multithread the git tree walking process
        - Render the tree in a custom GUI to get a POC going?
        - Create a world
     */

    public RepoManager() {
        CURRENT_REPO = new Repo();
    }

    public RepoManager(String repoUrl) throws IOException, GitAPIException {
        CURRENT_REPO = new Repo();
        setRepoUrl(repoUrl);
        cloneRepo();
        walkAllCommits();
        completeRelations();
    }

    /**
     * Complete the relations in the CURRENT_REPO
     * Figure out missing child commits and inter-branch relations
     */
    public void completeRelations() {
        LOGGER.info("Completing relations of available commits...");
        CURRENT_REPO.figureOutRelations();
    }

    /**
     * Set the repo URL in the manager and in our repo object
     * @param repoUrl
     */
    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
        CURRENT_REPO.setRepoUrl(repoUrl);
    }

    /**
     * Clone a remote repo in a local folder
     * @throws IOException
     */
    public void cloneRepo() throws IOException {
        try {
            /* Clone to local folder for branch inspection */
            LOGGER.info("Begin clone, this could take a while...");
            URL repoURL = new URL(repoUrl);
            /* Grab our local path depending on the OS */
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
                localPath = new File(Minecraft.getInstance().gameDirectory.getCanonicalPath() + "\\GitCraft\\" + new URIish(repoURL).getHumanishName());
            } else if (System.getProperty("os.name").contains("nux") || System.getProperty("os.name").contains("nix")) {
                localPath = new File(Minecraft.getInstance().gameDirectory.getCanonicalPath() + "/GitCraft/" + new URIish(repoURL).getHumanishName());
            }
            try {
                Git.cloneRepository()
                        .setURI(String.valueOf(repoURL))
                        .setDirectory(localPath)
                        .call();
            } catch (JGitInternalException e) {
                LOGGER.warn("Exception thown! Maybe the repo already exists locally?");
                LOGGER.warn(e);
            }
            LOGGER.info("Finished clone");
        } catch (GitAPIException | IOException e) {
            LOGGER.error(e);
        }
        /* Register the local repo path for future use */
        Repository localRepo = null;
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            localRepo = new FileRepository(localPath + "\\.git");
        } else if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("nux") || System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("nix")) {
            localRepo = new FileRepository(localPath + "/.git");
        }
        assert localRepo != null;
        repo = new Git(localRepo);
    }

    /**
     * Walk trough a branch, listing the commits in this branch
     * @param branchName Name of the branch to walk through
     * @throws IOException
     * @throws GitAPIException
     */
    public void walkCommitsOf(String branchName) throws IOException, GitAPIException {
        RevWalk walk = new RevWalk(repo.getRepository());

        Iterable<RevCommit> commits = repo.log().all().call();

        ArrayList<Commit> commitList = new ArrayList<>();
        for (RevCommit commit : commits) {
            boolean foundInThisBranch = false;

            RevCommit targetCommit = walk.parseCommit(repo.getRepository().resolve(commit.getName()));
            for (Map.Entry<String, Ref> e : repo.getRepository().getAllRefs().entrySet()) {  // TODO: Remove deprecated call
                if (e.getKey().startsWith(Constants.R_REMOTES)) {   //Constants.R_REMOTES for remote repo R_HEAD for local
                    if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
                        String foundInBranch = e.getValue().getName();
                        if (branchName.equals(foundInBranch)) {
                            foundInThisBranch = true;
                            break;
                        }
                    }
                }
            }
            if (foundInThisBranch) {
                if(commit.getParents().length > 0) {
//                    commitList.add(new RelationCommit(commit, commit.getParent(0)));
                    CURRENT_REPO.getBranch(branchName).addCommit(new Commit(commit, commit.getParent(0)));
                } else {
//                    commitList.add(new RelationCommit(commit));
                    CURRENT_REPO.getBranch(branchName).addCommit(new Commit(commit));
                }
            }
        }
    }

    /**
     * Walk through all the branches of the current repo
     * @throws IOException
     * @throws GitAPIException
     */
    public void walkAllCommits() throws IOException, GitAPIException {
        LOGGER.info("Building commits list...");
        LOGGER.info("This can take a while depending on your repo size");

        /*
        * List remote branches
        * We still need a local repo to list all the branches
        */
        List<Ref> branches = repo.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();

        for (Ref branch : branches) {
            CURRENT_REPO.addToList(new Branch(branch));
            if(!branch.getName().equals("HEAD")) {
                String branchName = branch.getName();
                //TODO: Multithread this
                walkCommitsOf(branchName);
            }
        }
    }
}

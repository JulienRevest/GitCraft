package wormstweaker.gitcraft.repoUtils;

import java.util.HashMap;
import java.util.Map;

public class Repo {

    private final HashMap<String, Branch> branchList;
    private String repoUrl;

    public Repo() {
        branchList = new HashMap<>();
        repoUrl = null;
    }

    /**
     * Add a branch object to the repo hashmap
     * @param branch Branch object to add
     */
    public void addToList(Branch branch) {
        branchList.put(branch.getBranchName(), branch);
    }

    /**
     * Return the specified branch object
     * @param branchName Name of the desired branch
     * @return Branch object
     */
    public Branch getBranch(String branchName) {
        return branchList.get(branchName);
    }

    /**
     * Set the repo URL
     * @param repoUrl The repo URL
     */
    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    /**
     * Get the hashmap containing all the commits of this branch
     * @return HashMap<String, Branch> branchList
     */
    public HashMap<String, Branch> getBranchList() {
        return branchList;
    }

    /**
     * Try to figure out the relationships of all the commits in this repo
     */
    public void figureOutRelations() {
        for (Map.Entry<String, Branch> branch : branchList.entrySet()) {
            branch.getValue().figureOutRelations();
        }
        //TODO: Multithread this
/*        Runnable relationRunnable = () -> {
            for (Map.Entry<String, Branch> branch : branchList.entrySet()) {
                branch.getValue().figureOutRelations();
            }
        };
        Thread relationThread = new Thread(relationRunnable, "RelationThread");
        relationThread.start();*/
    }
}

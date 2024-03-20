package wormstweaker.gitcraft.repoUtils;

import org.eclipse.jgit.lib.Ref;

import java.util.HashMap;
import java.util.Map;

public class Branch {

    private final HashMap<String, Commit> commitList;
    private final Ref branchRef;

    public Branch(Ref branchRef) {
        this.branchRef = branchRef;
        commitList = new HashMap<>();
    }

    public Ref getBranchRef() {return branchRef;}

    /**
     * Get this branch object name
     * @return String This branch name
     */
    public String getBranchName() {return branchRef.getName();}

    /**
     * Add a commit to this branch hashmap
     * @param commit The commit object to add
     */
    public void addCommit(Commit commit) {
        commitList.put(commit.getCommitHash(), commit);
    }

    /**
     * Get a specific commit object with its id
     * @param commitId The commit ID
     * @return Commit The commit object
     */
    public Commit getCommit(String commitId) {
        return commitList.get(commitId);
    }

    /**
     * Try to figure out the relationships of all the commits in this branch
     */
    public void figureOutRelations() {
        for(Map.Entry<String, Commit> commit : commitList.entrySet()) {
            for (Map.Entry<String, Commit> childCommit : commitList.entrySet()) {
                // If our commit hash is the same as the childCommit parent hash, childCommit is the child of commit
                if (childCommit.getValue().getParentCommit() != null && commit.getValue().getCommitHash().equals(childCommit.getValue().getParentCommit().getName())) {
                    commit.getValue().setChildCommit(childCommit.getValue());
//                    System.out.println("Child of " + commit.getValue().getCommitHash() + " is " + childCommit.getValue().getCommitHash());    //DEBUG
                }
            }
        }
    }
}

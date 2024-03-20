package wormstweaker.gitcraft.repoUtils;

import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {

    private final RevCommit baseCommit;
    private final RevCommit parentCommit;
    private Commit childCommit;
    private boolean isChildInAnotherBranch;
    private Branch childBranch;
    private boolean isParentInAnotherBranch;
    private Branch parentBranch;

    public Commit(RevCommit commit, RevCommit parent) {
        baseCommit = commit;
        parentCommit = parent;
        //TODO: Set this commit branch when creating it
        childCommit = null;
    }

    public Commit(RevCommit commit) {
        baseCommit = commit;
        parentCommit = null;
    }

    /**
     * Get the commit parent
     * @return RevCommit parentCommit
     */
    public RevCommit getParentCommit() {
        return parentCommit;
    }

    /**
     * Set the child of this commit
     * @param childCommit
     */
    public void setChildCommit(Commit childCommit) {
        this.childCommit = childCommit;
    }

    /**
     * Return this commit RevCommit object
     * @return
     */
    public RevCommit getCommit() {
        return baseCommit;
    }

    /**
     * Get the hash/name of this commit
     * @return
     */
    public String getCommitHash() { return baseCommit.getName(); }
}

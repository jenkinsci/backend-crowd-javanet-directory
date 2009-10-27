package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.model.group.GroupType;
import static com.atlassian.crowd.integration.model.group.GroupType.GROUP;
import com.atlassian.crowd.integration.model.group.GroupWithAttributes;

/**
 * @author Kohsuke Kawaguchi
 */
public class GroupImpl extends EmptyAttributes implements GroupWithAttributes {
    private final JavaNetDirectory directory;
    private final String name;

    public GroupImpl(JavaNetDirectory directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    public GroupType getType() {
        return GROUP;
    }

    public boolean isActive() {
        return true;
    }

    public String getDescription() {
        return name;
    }

    public Long getDirectoryId() {
        return directory.getDirectoryId();
    }

    public String getName() {
        return name;
    }
}

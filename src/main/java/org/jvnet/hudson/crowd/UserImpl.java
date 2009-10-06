package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.model.user.UserWithAttributes;

/**
 * @author Kohsuke Kawaguchi
*/
class UserImpl extends EmptyAttributes implements UserWithAttributes {
    private final String name;
    private final JavaNetDirectory parent;

    UserImpl(JavaNetDirectory parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public String getEmailAddress() {
        return name + "@dev.java.net";
    }

    @Override
    public String getFirstName() {
        return "";
    }

    @Override
    public String getLastName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getIconLocation() {
        return null;
    }

    @Override
    public Long getDirectoryId() {
        return parent.getDirectoryId();
    }

    @Override
    public String getName() {
        return name;
    }

}

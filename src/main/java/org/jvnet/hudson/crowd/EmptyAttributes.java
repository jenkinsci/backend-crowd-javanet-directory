package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.model.AttributeAware;

import java.util.List;
import java.util.Collections;
import java.util.Set;

/**
 * @author Kohsuke Kawaguchi
 */
public class EmptyAttributes implements AttributeAware {
    @Override
    public List<String> getAttributes(String s) {
        return Collections.emptyList();
    }

    @Override
    public String getAttribute(String s) {
        return null;
    }

    @Override
    public Set<String> getAttributeNames() {
        return Collections.emptySet();
    }

    @Override
    public boolean hasAttribute(String s) {
        return false;
    }
}

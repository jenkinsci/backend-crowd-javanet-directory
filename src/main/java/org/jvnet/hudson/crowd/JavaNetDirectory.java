package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.integration.exception.InactiveAccountException;
import com.atlassian.crowd.integration.exception.InvalidAuthenticationException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.model.group.Group;
import com.atlassian.crowd.integration.model.group.GroupWithAttributes;
import com.atlassian.crowd.integration.model.user.User;
import com.atlassian.crowd.integration.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.asList;

import org.kohsuke.jnt.ProcessingException;
import org.kohsuke.jnt.JavaNet;

/**
 * Adapter to expose java.net user database to Crowd.
 *
 * <p>
 * This has some hard-coded knowledge about groups that Confluence expects.
 *
 * @see <a href="http://confluence.atlassian.com/display/CROWD/Creating+a+Custom+Directory+Connector">guide</a>
 * @author Kohsuke Kawaguchi
 */
public class JavaNetDirectory extends AbstractRemoteDirectory {
    private final Map<String,GroupImpl> groups = new HashMap<String,GroupImpl>();
    private final GroupImpl confluenceUsers = new GroupImpl(this, CONFLUENCE_USERS);
    private final GroupImpl confluenceAdministrators = new GroupImpl(this,CONFLUENCE_ADMINISTRATORS);
    private final GroupImpl jiraUsers = new GroupImpl(this,JIRA_USERS);
    private final GroupImpl jiraDevelopers = new GroupImpl(this,JIRA_DEVELOPERS);
    private final GroupImpl jiraAdministrators = new GroupImpl(this,JIRA_ADMINISTRATORS);

    public JavaNetDirectory() {
        for (GroupImpl g : asList(confluenceUsers,confluenceAdministrators,jiraUsers,jiraDevelopers,jiraAdministrators))
            groups.put(g.getName(), g);
    }

    public String getDescriptiveName() {
        return "java.net user database";
    }

    public User findUserByName(String name) throws ObjectNotFoundException {
        return findUserWithAttributesByName(name);
    }

    public UserWithAttributes findUserWithAttributesByName(final String name) throws ObjectNotFoundException {
        return new UserImpl(this, name);
    }

    public User authenticate(final String name, PasswordCredential password) throws ObjectNotFoundException, InactiveAccountException, InvalidAuthenticationException {
        try {
            System.out.println("Authenticating "+name);
            if (testMode) {
                // during test, don't hit java.net, and just use username==password
                if (!name.equals(password.getCredential()))
                    throw new InvalidAuthenticationException("Failed to authenticate "+name);
            } else {
                // in production, do the real thing
                JavaNet.connect(name,password.getCredential());
            }
            return new UserImpl(this,name);
        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new InvalidAuthenticationException("Failed to authenticate "+name,e);
        }
    }

    public Group findGroupByName(String name) throws ObjectNotFoundException {
        return findGroupWithAttributesByName(name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws ObjectNotFoundException {
        return groups.get(name);
    }

    @Override
    public List searchGroups(EntityQuery entityQuery) {
        return asList(confluenceAdministrators,confluenceUsers,jiraUsers,jiraAdministrators,jiraDevelopers);
    }

    @Override
    public boolean isUserDirectGroupMember(String userName, String groupName) {
        if (groupName.equals(CONFLUENCE_USERS))     return true;
        if (groupName.equals(JIRA_USERS))     return true;

        // TODO: how to handle jira-developers category?

        return userName.equals("kohsuke");
    }

    @Override
    public List searchGroupRelationships(MembershipQuery q) {
        if (q.getEntityToMatch().getEntityType()==Entity.USER && q.getEntityToReturn().getEntityType()==Entity.GROUP) {
            // looking up groups by the user
            List<String> r = new ArrayList<String>();
            for (String group : groups.keySet()) {
                if (isUserDirectGroupMember(q.getEntityNameToMatch(),group))
                    r.add(group);
            }
            return r;
        }
        return super.searchGroupRelationships(q);
    }

    private static final String CONFLUENCE_USERS          = "confluence-users";
    private static final String CONFLUENCE_ADMINISTRATORS = "confluence-administrators";

    private static final String JIRA_USERS          = "jira-users";
    private static final String JIRA_DEVELOPERS     = "jira-developers";
    private static final String JIRA_ADMINISTRATORS = "jira-administrators";

    private static final boolean testMode = Boolean.getBoolean(JavaNetDirectory.class.getName()+".test");
}

package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.integration.exception.InactiveAccountException;
import com.atlassian.crowd.integration.exception.InvalidAuthenticationException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.model.group.Group;
import com.atlassian.crowd.integration.model.group.GroupWithAttributes;
import com.atlassian.crowd.integration.model.user.User;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import org.kohsuke.jnt.JavaNet;
import org.kohsuke.jnt.ProcessingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapter to expose java.net user database to Crowd.
 *
 * <p>
 * This has some hard-coded knowledge about groups that Confluence/JIRA expects.
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

    private final ConcurrentHashMap<String,String> authCache = new ConcurrentHashMap<String, String>();

    private final Map<String,UserImpl> allUsers = new HashMap<String,UserImpl>();

    public JavaNetDirectory() {
        for (GroupImpl g : asList(confluenceUsers,confluenceAdministrators,jiraUsers,jiraDevelopers,jiraAdministrators))
            groups.put(g.getName(), g);

        try {
            BufferedReader r = new BufferedReader(new FileReader("/home/crowd/users.txt"));
            String name;
            while ((name=r.readLine())!=null)
                allUsers.put(name,findUserByName(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDescriptiveName() {
        return "java.net user database";
    }

    public UserImpl findUserByName(String name) {
        return findUserWithAttributesByName(name);
    }

    public UserImpl findUserWithAttributesByName(final String name) {
        return new UserImpl(this, name);
    }

    public User authenticate(final String name, PasswordCredential password) throws ObjectNotFoundException, InactiveAccountException, InvalidAuthenticationException {
        try {
            System.out.println("Authenticating "+name);
            String p = password.getCredential();
            if (testMode) {
                // during test, don't hit java.net, and just use username==password
                if (!name.equals(p))
                    throw new InvalidAuthenticationException("Failed to authenticate "+name);
            } else {
                // in production, do the real thing
                String d = DigestUtil.getDigestOf(p);
                boolean authenticated = d.equals(authCache.get(name));
                if (!authenticated) {// cache failed
                    JavaNet.connect(name,p);
                    authCache.put(name,d); // cache this password for the future
                }
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
        LOGGER.fine("searchGroups: "+entityQuery);
        return new ArrayList<GroupImpl>(groups.values());
    }

    /**
     * JIRA calls this method with {@link NullRestriction} (which presumably means "SELECT *"), and if
     * we don't return all the users, JIRA will fail with http://jira.atlassian.com/browse/CWD-202
     * (users are unable to login, can't search with that user, etc.)
     */
    @Override
    public List searchUsers(EntityQuery entityQuery) {
        LOGGER.fine("searchUsers: "+entityQuery);
        return new ArrayList<UserImpl>(allUsers.values());
    }

    @Override
    public boolean isUserDirectGroupMember(String userName, String groupName) {
        if (groupName.equals(CONFLUENCE_USERS))     return true;
        if (groupName.equals(JIRA_USERS))     return true;
        if (groupName.equals(JIRA_ADMINISTRATORS) && userName.equals("abayer"))     return true;

        // TODO: how to handle jira-developers category?
        if (groupName.equals(JIRA_DEVELOPERS) && userName.startsWith("a"))     return true;

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

        if (q.getEntityToMatch().getEntityType()==Entity.GROUP && q.getEntityToReturn().getEntityType()==Entity.USER) {
            // looking up users by the group
            List<String> r = new ArrayList<String>();
            for (String user : allUsers.keySet()) {
                if (isUserDirectGroupMember(user,q.getEntityNameToMatch()))
                    r.add(user);
            }
            return r;
        }

        LOGGER.fine("Unhandled searchGroupRelationships: "+q);
        return super.searchGroupRelationships(q);
    }

    private static final String CONFLUENCE_USERS          = "confluence-users";
    private static final String CONFLUENCE_ADMINISTRATORS = "confluence-administrators";

    private static final String JIRA_USERS          = "jira-users";
    private static final String JIRA_DEVELOPERS     = "jira-developers";
    private static final String JIRA_ADMINISTRATORS = "jira-administrators";

    private static final boolean testMode = Boolean.getBoolean(JavaNetDirectory.class.getName()+".test");

    private static final Logger LOGGER = Logger.getLogger(JavaNetDirectory.class.getName());

    static {
        LOGGER.setLevel(Level.FINEST);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler h = new ConsoleHandler();
        h.setLevel(Level.FINEST);
        LOGGER.addHandler(h);
    }
}

package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.directory.RemoteDirectory;
import com.atlassian.crowd.integration.model.user.User;
import com.atlassian.crowd.integration.model.user.UserWithAttributes;
import com.atlassian.crowd.integration.model.user.UserTemplate;
import com.atlassian.crowd.integration.model.group.Group;
import com.atlassian.crowd.integration.model.group.GroupWithAttributes;
import com.atlassian.crowd.integration.model.group.GroupTemplate;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.exception.InactiveAccountException;
import com.atlassian.crowd.integration.exception.InvalidAuthenticationException;
import com.atlassian.crowd.integration.exception.InvalidUserException;
import com.atlassian.crowd.integration.exception.InvalidCredentialException;
import com.atlassian.crowd.integration.exception.InvalidGroupException;
import com.atlassian.crowd.integration.exception.InvalidMembershipException;
import com.atlassian.crowd.integration.exception.MembershipNotFoundException;
import com.atlassian.crowd.integration.exception.DirectoryAccessException;
import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Collections;

/**
 * Base minimal implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractRemoteDirectory implements RemoteDirectory {
    private long id;
    private Map<String,String> attributes = Collections.emptyMap();

    @Override
    public long getDirectoryId() {
        return id;
    }

    @Override
    public void setDirectoryId(long id) {
        this.id = id;
    }

    @Override
    public void setAttributes(Map<String, String> atts) {
        this.attributes = atts;
    }

    @Override
    public User addUser(UserTemplate userTemplate, PasswordCredential passwordCredential) throws InvalidUserException, ObjectNotFoundException, InvalidCredentialException {
        throw new UnsupportedOperationException("Adding user is not supported");
    }

    @Override
    public User updateUser(UserTemplate userTemplate) throws InvalidUserException, ObjectNotFoundException {
        throw new UnsupportedOperationException("Updating user is not supported");
    }

    @Override
    public void updateUserCredential(String s, PasswordCredential passwordCredential) throws ObjectNotFoundException, InvalidCredentialException {
        throw new UnsupportedOperationException("Changing the password is not supported");
    }

    @Override
    public User renameUser(String s, String s1) throws ObjectNotFoundException, InvalidUserException {
        throw new UnsupportedOperationException("Renaming user is not supported");
    }

    @Override
    public void storeUserAttributes(String s, Map<String, List<String>> stringListMap) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeUserAttributes(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeUser(String s) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List searchUsers(EntityQuery entityQuery) {
        return Collections.emptyList();
    }

    @Override
    public Group addGroup(GroupTemplate groupTemplate) throws InvalidGroupException, ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group updateGroup(GroupTemplate groupTemplate) throws InvalidGroupException, ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group renameGroup(String s, String s1) throws ObjectNotFoundException, InvalidGroupException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void storeGroupAttributes(String s, Map<String, List<String>> stringListMap) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGroupAttributes(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGroup(String s) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List searchGroups(EntityQuery entityQuery) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUserDirectGroupMember(String s, String s1) {
        return false;
    }

    @Override
    public boolean isGroupDirectGroupMember(String s, String s1) {
        return false;
    }

    @Override
    public void addUserToGroup(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addGroupToGroup(String s, String s1) throws ObjectNotFoundException, InvalidMembershipException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeUserFromGroup(String s, String s1) throws ObjectNotFoundException, MembershipNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeGroupFromGroup(String s, String s1) throws ObjectNotFoundException, InvalidMembershipException, MembershipNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List searchGroupRelationships(MembershipQuery membershipQuery) {
        return Collections.emptyList();
    }

    @Override
    public void testConnection() throws DirectoryAccessException {
    }

    @Override
    public boolean supportsNestedGroups() {
        return false;
    }

    @Override
    public List<String> getAttributes(String name) {
        String v = attributes.get(name);
        return v!=null ? Collections.singletonList(v) : Collections.<String>emptyList();
    }

    @Override
    public String getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    @Override
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }
}

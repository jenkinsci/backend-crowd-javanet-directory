package org.jvnet.hudson.crowd;

import com.atlassian.crowd.integration.authentication.PasswordCredential;
import com.atlassian.crowd.integration.directory.RemoteDirectory;
import com.atlassian.crowd.integration.exception.DirectoryAccessException;
import com.atlassian.crowd.integration.exception.InvalidCredentialException;
import com.atlassian.crowd.integration.exception.InvalidGroupException;
import com.atlassian.crowd.integration.exception.InvalidMembershipException;
import com.atlassian.crowd.integration.exception.InvalidUserException;
import com.atlassian.crowd.integration.exception.MembershipNotFoundException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.model.group.Group;
import com.atlassian.crowd.integration.model.group.GroupTemplate;
import com.atlassian.crowd.integration.model.user.User;
import com.atlassian.crowd.integration.model.user.UserTemplate;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base minimal implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractRemoteDirectory implements RemoteDirectory {
    private long id;
    private Map<String,String> attributes = Collections.emptyMap();

    public long getDirectoryId() {
        return id;
    }

    public void setDirectoryId(long id) {
        this.id = id;
    }

    public void setAttributes(Map<String, String> atts) {
        this.attributes = atts;
    }

    public User addUser(UserTemplate userTemplate, PasswordCredential passwordCredential) throws InvalidUserException, ObjectNotFoundException, InvalidCredentialException {
        throw new UnsupportedOperationException("Adding user is not supported");
    }

    public User updateUser(UserTemplate userTemplate) throws InvalidUserException, ObjectNotFoundException {
        throw new UnsupportedOperationException("Updating user is not supported");
    }

    public void updateUserCredential(String s, PasswordCredential passwordCredential) throws ObjectNotFoundException, InvalidCredentialException {
        throw new UnsupportedOperationException("Changing the password is not supported");
    }

    public User renameUser(String s, String s1) throws ObjectNotFoundException, InvalidUserException {
        throw new UnsupportedOperationException("Renaming user is not supported");
    }

    public void storeUserAttributes(String s, Map<String, List<String>> stringListMap) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void removeUserAttributes(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void removeUser(String s) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public List searchUsers(EntityQuery entityQuery) {
        return Collections.emptyList();
    }

    public Group addGroup(GroupTemplate groupTemplate) throws InvalidGroupException, ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public Group updateGroup(GroupTemplate groupTemplate) throws InvalidGroupException, ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public Group renameGroup(String s, String s1) throws ObjectNotFoundException, InvalidGroupException {
        throw new UnsupportedOperationException();
    }

    public void storeGroupAttributes(String s, Map<String, List<String>> stringListMap) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void removeGroupAttributes(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void removeGroup(String s) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public List searchGroups(EntityQuery entityQuery) {
        return Collections.emptyList();
    }

    public boolean isUserDirectGroupMember(String s, String s1) {
        return false;
    }

    public boolean isGroupDirectGroupMember(String s, String s1) {
        return false;
    }

    public void addUserToGroup(String s, String s1) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void addGroupToGroup(String s, String s1) throws ObjectNotFoundException, InvalidMembershipException {
        throw new UnsupportedOperationException();
    }

    public void removeUserFromGroup(String s, String s1) throws ObjectNotFoundException, MembershipNotFoundException {
        throw new UnsupportedOperationException();
    }

    public void removeGroupFromGroup(String s, String s1) throws ObjectNotFoundException, InvalidMembershipException, MembershipNotFoundException {
        throw new UnsupportedOperationException();
    }

    public List searchGroupRelationships(MembershipQuery membershipQuery) {
        return Collections.emptyList();
    }

    public void testConnection() throws DirectoryAccessException {
    }

    public boolean supportsNestedGroups() {
        return false;
    }

    public List<String> getAttributes(String name) {
        String v = attributes.get(name);
        return v!=null ? Collections.singletonList(v) : Collections.<String>emptyList();
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }
}

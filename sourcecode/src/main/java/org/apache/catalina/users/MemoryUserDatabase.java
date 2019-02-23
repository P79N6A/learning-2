/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.users;

import org.apache.catalina.*;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.res.StringManager;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Concrete implementation of {@link UserDatabase} that loads all
 * defined users, groups, and roles into an in-memory data structure,
 * and uses a specified XML file for its persistent storage.</p>
 *
 * @author Craig R. McClanahan
 * @since 4.1
 */
public class MemoryUserDatabase implements UserDatabase {


    private static final Log log = LogFactory.getLog(MemoryUserDatabase.class);

    // ----------------------------------------------------------- Constructors


    /**
     * Create a new instance with default values.
     */
    public MemoryUserDatabase() {
        this(null);
    }


    /**
     * Create a new instance with the specified values.
     *
     * @param id Unique global identifier of this user database
     */
    public MemoryUserDatabase(String id) {
        this.id = id;
    }


    // ----------------------------------------------------- Instance Variables


    /**
     * The set of {@link Group}s defined in this database, keyed by
     * group name.
     */
    protected final HashMap<String,Group> groups = new HashMap<>();


    /**
     * The unique global identifier of this user database.
     */
    protected final String id;


    /**
     * The relative (to <code>catalina.base</code>) or absolute pathname to
     * the XML file in which we will save our persistent information.
     */
    protected String pathname = "conf/tomcat-users.xml";
//    protected String pathname = "conf/tomcat-users.xml";


    /**
     * The relative or absolute pathname to the file in which our old
     * information is stored while renaming is in progress.
     */
    protected String pathnameOld = pathname + ".old";


    /**
     * The relative or absolute pathname of the file in which we write
     * our new information prior to renaming.
     */
    protected String pathnameNew = pathname + ".new";


    /**
     * A flag, indicating if the user database is read only.
     */
    protected boolean readonly = true;

    /**
     * The set of {@link Role}s defined in this database, keyed by
     * role name.
     */
    protected final HashMap<String,Role> roles = new HashMap<>();


    /**
     * The string manager for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(Constants.Package);


    /**
     * The set of {@link User}s defined in this database, keyed by
     * user name.
     */
    protected final HashMap<String,User> users = new HashMap<>();


    // ------------------------------------------------------------- Properties


    /**
     * @return the set of {@link Group}s defined in this user database.
     */
    @Override
    public Iterator<Group> getGroups() {
        synchronized (groups) {
            return groups.values().iterator();
        }
    }


    /**
     * @return the unique global identifier of this user database.
     */
    @Override
    public String getId() {
        return this.id;
    }


    /**
     * @return the relative or absolute pathname to the persistent storage file.
     */
    public String getPathname() {
        return this.pathname;
    }


    /**
     * Set the relative or absolute pathname to the persistent storage file.
     *
     * @param pathname The new pathname
     */
    public void setPathname(String pathname) {

        this.pathname = pathname;
        this.pathnameOld = pathname + ".old";
        this.pathnameNew = pathname + ".new";

    }


    /**
     * @return the readonly status of the user database
     */
    public boolean getReadonly() {
        return this.readonly;
    }


    /**
     * Setting the readonly status of the user database
     *
     * @param readonly the new status
     */
    public void setReadonly(boolean readonly) {

        this.readonly = readonly;

    }


    /**
     * @return the set of {@link Role}s defined in this user database.
     */
    @Override
    public Iterator<Role> getRoles() {
        synchronized (roles) {
            return roles.values().iterator();
        }
    }


    /**
     * @return the set of {@link User}s defined in this user database.
     */
    @Override
    public Iterator<User> getUsers() {
        synchronized (users) {
            return users.values().iterator();
        }
    }



    // --------------------------------------------------------- Public Methods


    /**
     * Finalize access to this user database.
     *
     * @exception Exception if any exception is thrown during closing
     */
    @Override
    public void close() throws Exception {

        save();

        synchronized (groups) {
            synchronized (users) {
                users.clear();
                groups.clear();
            }
        }

    }


    /**
     * Create and return a new {@link Group} defined in this user database.
     *
     * @param groupname The group name of the new group (must be unique)
     * @param description The description of this group
     */
    @Override
    public Group createGroup(String groupname, String description) {
        if (groupname == null || groupname.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullGroup");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        MemoryGroup group = new MemoryGroup(this, groupname, description);
        synchronized (groups) {
            groups.put(group.getGroupname(), group);
        }
        return group;
    }


    /**
     * Create and return a new {@link Role} defined in this user database.
     *
     * @param rolename The role name of the new group (must be unique)
     * @param description The description of this group
     */
    @Override
    public Role createRole(String rolename, String description) {
        if (rolename == null || rolename.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullRole");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        MemoryRole role = new MemoryRole(this, rolename, description);
        synchronized (roles) {
            roles.put(role.getRolename(), role);
        }
        return role;
    }


    /**
     * Create and return a new {@link User} defined in this user database.
     *
     * @param username The logon username of the new user (must be unique)
     * @param password The logon password of the new user
     * @param fullName The full name of the new user
     */
    @Override
    public User createUser(String username, String password,
                           String fullName) {

        if (username == null || username.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullUser");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        MemoryUser user = new MemoryUser(this, username, password, fullName);
        synchronized (users) {
            users.put(user.getUsername(), user);
        }
        return user;
    }


    /**
     * Return the {@link Group} with the specified group name, if any;
     * otherwise return <code>null</code>.
     *
     * @param groupname Name of the group to return
     */
    @Override
    public Group findGroup(String groupname) {

        synchronized (groups) {
            return groups.get(groupname);
        }

    }


    /**
     * Return the {@link Role} with the specified role name, if any;
     * otherwise return <code>null</code>.
     *
     * @param rolename Name of the role to return
     */
    @Override
    public Role findRole(String rolename) {

        synchronized (roles) {
            return roles.get(rolename);
        }

    }


    /**
     * Return the {@link User} with the specified user name, if any;
     * otherwise return <code>null</code>.
     *
     * @param username Name of the user to return
     */
    @Override
    public User findUser(String username) {

        synchronized (users) {
            return users.get(username);
        }

    }


    /**
     * Initialize access to this user database.
     *
     * @exception Exception if any exception is thrown during opening
     */
    @Override
    public void open() throws Exception {

        synchronized (groups) {
            synchronized (users) {

                // Erase any previous groups and users
                users.clear();
                groups.clear();
                roles.clear();

                String pathName = getPathname();
                try (InputStream is = ConfigFileLoader.getInputStream(getPathname())) {
                    // Construct a digester to read the XML input file
                    Digester digester = new Digester();
                    try {
                        digester.setFeature(
                                "http://apache.org/xml/features/allow-java-encodings", true);
                    } catch (Exception e) {
                        log.warn(sm.getString("memoryUserDatabase.xmlFeatureEncoding"), e);
                    }
                    digester.addFactoryCreate("tomcat-users/group",
                            new MemoryGroupCreationFactory(this), true);
                    digester.addFactoryCreate("tomcat-users/role",
                            new MemoryRoleCreationFactory(this), true);
                    digester.addFactoryCreate("tomcat-users/user",
                            new MemoryUserCreationFactory(this), true);

                    // Parse the XML input to load this database
                    digester.parse(is);
                } catch (IOException ioe) {
                    log.error(sm.getString("memoryUserDatabase.fileNotFound", pathName));
                }
            }
        }
    }


    /**
     * Remove the specified {@link Group} from this user database.
     *
     * @param group The group to be removed
     */
    @Override
    public void removeGroup(Group group) {

        synchronized (groups) {
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeGroup(group);
            }
            groups.remove(group.getGroupname());
        }
    }


    /**
     * Remove the specified {@link Role} from this user database.
     *
     * @param role The role to be removed
     */
    @Override
    public void removeRole(Role role) {

        synchronized (roles) {
            Iterator<Group> groups = getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                group.removeRole(role);
            }
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeRole(role);
            }
            roles.remove(role.getRolename());
        }

    }


    /**
     * Remove the specified {@link User} from this user database.
     *
     * @param user The user to be removed
     */
    @Override
    public void removeUser(User user) {

        synchronized (users) {
            users.remove(user.getUsername());
        }

    }


    /**
     * Check for permissions to save this user database to persistent storage
     * location.
     * @return <code>true</code> if the database is writable
     */
    public boolean isWriteable() {

        File file = new File(pathname);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty(Globals.CATALINA_BASE_PROP),
                            pathname);
        }
        File dir = file.getParentFile();
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }


    /**
     * Save any updated information to the persistent storage location for
     * this user database.
     *
     * @exception Exception if any exception is thrown during saving
     */
    @Override
    public void save() throws Exception {

        if (getReadonly()) {
            log.error(sm.getString("memoryUserDatabase.readOnly"));
            return;
        }

        if (!isWriteable()) {
            log.warn(sm.getString("memoryUserDatabase.notPersistable"));
            return;
        }

        // Write out contents to a temporary file
        File fileNew = new File(pathnameNew);
        if (!fileNew.isAbsolute()) {
            fileNew = new File(System.getProperty(Globals.CATALINA_BASE_PROP), pathnameNew);
        }

        try (FileOutputStream fos = new FileOutputStream(fileNew);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
                PrintWriter writer = new PrintWriter(osw)) {

            // Print the file prolog
            writer.println("<?xml version='1.0' encoding='utf-8'?>");
            writer.println("<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"");
            writer.println("              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
            writer.println("              xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"");
            writer.println("              version=\"1.0\">");

            // Print entries for each defined role, group, and user
            Iterator<?> values = null;
            values = getRoles();
            while (values.hasNext()) {
                writer.print("  ");
                writer.println(values.next());
            }
            values = getGroups();
            while (values.hasNext()) {
                writer.print("  ");
                writer.println(values.next());
            }
            values = getUsers();
            while (values.hasNext()) {
                writer.print("  ");
                writer.println(((MemoryUser) values.next()).toXml());
            }

            // Print the file epilog
            writer.println("</tomcat-users>");

            // Check for errors that occurred while printing
            if (writer.checkError()) {
                throw new IOException(sm.getString("memoryUserDatabase.writeException",
                        fileNew.getAbsolutePath()));
            }
        } catch (IOException e) {
            if (fileNew.exists() && !fileNew.delete()) {
                log.warn(sm.getString("memoryUserDatabase.fileDelete", fileNew));
            }
            throw e;
        }

        // Perform the required renames to permanently save this file
        File fileOld = new File(pathnameOld);
        if (!fileOld.isAbsolute()) {
            fileOld = new File(System.getProperty(Globals.CATALINA_BASE_PROP), pathnameOld);
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
        }
        File fileOrig = new File(pathname);
        if (!fileOrig.isAbsolute()) {
            fileOrig = new File(System.getProperty(Globals.CATALINA_BASE_PROP), pathname);
        }
        if (fileOrig.exists()) {
            if (!fileOrig.renameTo(fileOld)) {
                throw new IOException(sm.getString("memoryUserDatabase.renameOld",
                        fileOld.getAbsolutePath()));
            }
        }
        if (!fileNew.renameTo(fileOrig)) {
            if (fileOld.exists()) {
                if (!fileOld.renameTo(fileOrig)) {
                    log.warn(sm.getString("memoryUserDatabase.restoreOrig", fileOld));
                }
            }
            throw new IOException(sm.getString("memoryUserDatabase.renameNew",
                    fileOrig.getAbsolutePath()));
        }
        if (fileOld.exists() && !fileOld.delete()) {
            throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
        }
    }


    /**
     * Return a String representation of this UserDatabase.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MemoryUserDatabase[id=");
        sb.append(this.id);
        sb.append(",pathname=");
        sb.append(pathname);
        sb.append(",groupCount=");
        sb.append(this.groups.size());
        sb.append(",roleCount=");
        sb.append(this.roles.size());
        sb.append(",userCount=");
        sb.append(this.users.size());
        sb.append("]");
        return sb.toString();
    }
}



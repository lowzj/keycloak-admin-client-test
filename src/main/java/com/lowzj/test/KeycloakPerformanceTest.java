package com.lowzj.test;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2015, Easemob.
 * All rights reserved.
 * Author: zhangjin@easemob.com
 */
public class KeycloakPerformanceTest {
    private static final Logger LOG = LoggerFactory.getLogger(KeycloakPerformanceTest.class);
    private Keycloak keycloak;

    private static KeycloakPerformanceTest instance;
    static {
        if (instance == null) {
            Keycloak keycloak = Keycloak.getInstance(
                    "http://localhost:8080/auth",   // auth url
                    "master",                       // realm
                    "admin",                        // username
                    "123456",                       // password
                    "admin-cli"                     // client_id
            );
            instance = new KeycloakPerformanceTest(keycloak);
        }
    }

    private KeycloakPerformanceTest(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public static KeycloakPerformanceTest getInstance() {
        return instance;
    }

    public Keycloak getKeycloak() {
        return keycloak;
    }

    public void run(RunContext context) {
        createRealm(context);
        createUser(context);
        createClient(context);
        createRole(context);
    }

    private void createRealm(RunContext context) {
        if (context.getRealmsCount() <= 0) {
            return;
        }
        int succeed = 0;
        TimeStat timeStat = new TimeStat().start();
        for (int i = 0; i < context.getRealmsCount(); ++i) {
            String realmName = getRealmName(context, i);
            RealmRepresentation realm = getRealmRepresentation(realmName);
            boolean status = false;
            try {
                keycloak.realms().create(realm);
                status = true;
                ++succeed;
            } catch (Exception e) {
                LOG.error("createRealm error " + realmName, e);
            } finally {
                timeStat.step("createRealm: status=" + status +
                        " realmName=" + realmName);
            }
        }
        timeStat.stat("createRealm size=" + context.getRealmsCount());
        context.setRealmsSucceed(succeed);
    }

    private void createUser(RunContext context) {
        if (context.getUsersCount() <= 0) {
            return;
        }

        int succeed = 0;
        TimeStat timeStat = new TimeStat().start();
        for (int i = 0; i < context.getRealmsSucceed(); ++i) {
            String realmName = getRealmName(context, i);
            UsersResource usersResource = keycloak.realm(realmName).users();
            for (int j = 0; j < context.getUsersCount(); ++j) {
                String username = realmName + "_user_" + succeed;
                UserRepresentation user = getUserRepresentation(username);
                boolean status = false;
                try {
                    usersResource.create(user).close();
                    status = true;
                    ++succeed;
                } catch (Exception e) {
                    LOG.error("createUser error " + realmName + " " + username, e);
                } finally {
                    timeStat.step("createUser: status=" + status +
                            " realmName=" + realmName + " username=" + username);
                }
            }
        }
        timeStat.stat("createUser size=" + succeed);
        context.setUsersSucceed(succeed);
    }

    private void createClient(RunContext context) {
        if (context.getClientsCount() <= 0) {
            return;
        }
        int succeed = 0;
        TimeStat timeStat = new TimeStat().start();
        for (int i = 0; i < context.getRealmsSucceed(); ++i) {
            String realmName = getRealmName(context, i);
            ClientsResource clientsResource = keycloak.realm(realmName).clients();
            for (int j = 0; j < context.getClientsCount(); ++j) {
                String clientName = realmName + "_client_" + succeed;
                ClientRepresentation client = getClientRepresentation(clientName);
                boolean status = false;
                try {
                    clientsResource.create(client).close();
                    status = true;
                    ++succeed;
                } catch (Exception e) {
                    LOG.error("createClient error " + realmName + " " + clientName, e);
                } finally {
                    timeStat.step("createClient: status=" + status +
                            " realmName=" + realmName + " clientName=" + clientName);
                }
            }
        }
        timeStat.stat("createClient size=" + succeed);
        context.setClientsSucceed(succeed);
    }

    private void createRole(RunContext context) {
        if (context.getRolesCount() <= 0) {
            return;
        }

        int succeed = 0;
        TimeStat timeStat = new TimeStat().start();
        for (int i = 0; i < context.getRealmsSucceed(); ++i) {
            String realmName = getRealmName(context, i);
            RolesResource rolesResource = keycloak.realm(realmName).roles();
            for (int j = 0; j < context.getRolesCount(); ++j) {
                String roleName = realmName + "_role_" + succeed;
                RoleRepresentation role = getRoleRepresentation(roleName);
                boolean status = false;
                try {
                    rolesResource.create(role);
                    status = true;
                    ++succeed;
                } catch (Exception e) {
                    LOG.error("createRole error " + realmName + " " + rolesResource, e);
                } finally {
                    timeStat.step("createRole: status=" + status +
                            " realmName=" + realmName + " roleName=" + roleName);
                }
            }
        }
        timeStat.stat("createRole size=" + succeed);
        context.setRolesSucceed(succeed);
    }

    private void readUser(RunContext context, String realmName, TimeStat timeStat) {

        RealmsResource realms = keycloak.realms();
        for (RealmRepresentation realm : realms.findAll()) {
            System.out.println("realm: " + realm.getRealm());
            UsersResource usersResource = keycloak.realm(realm.getRealm()).users();
            int searchOneTime = 100;
            List<UserRepresentation> userList = null;
            int start = 0;
            do {
                try {
                    userList = usersResource.search("", start, searchOneTime);
                } catch (Exception e) {

                }
                start += (userList != null ? userList.size() : 0);
            } while (userList != null && userList.size() >= searchOneTime);
        }
    }

    private void readClient(RunContext context, String realmName, TimeStat timeStat) {

    }

    //=========================================================================

    private String getRealmName(RunContext context, int index) {
        return context.getPrefix() + "_" + index;
    }

    private RealmRepresentation getRealmRepresentation(String realmName) {
        RealmRepresentation realm= new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setEnabled(true);
        return realm;
    }

    private UserRepresentation getUserRepresentation(String username) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("123456");

        List<CredentialRepresentation> credentials = new ArrayList<CredentialRepresentation>();
        credentials.add(credential);
        user.setCredentials(credentials);
        return user;
    }

    private ClientRepresentation getClientRepresentation(String name) {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(name);
        client.setName(name);
        client.setEnabled(true);
        return client;
    }

    private RoleRepresentation getRoleRepresentation(String name) {
        RoleRepresentation role = new RoleRepresentation();
        role.setId(name);
        role.setName(name);
        role.setDescription(name);
        return role;
    }
}

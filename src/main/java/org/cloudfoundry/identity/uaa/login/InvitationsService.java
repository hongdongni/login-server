package org.cloudfoundry.identity.uaa.login;

public interface InvitationsService {
    void inviteUser(String email, String currentUser);

    void acceptInvitation(String userId, String email, String password);
}

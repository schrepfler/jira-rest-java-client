package it;

import com.atlassian.jira.rest.client.TestUtil;

import javax.ws.rs.core.Response;

/**
 * TODO: Or just make this a static utility? want to have access to the client, though...could take it in & be an instance :-/
 *
 */
class AbstractAsynchronousMetadataRestClientTest extends AbstractAsynchronousRestClientTest {

    //Scheme Ids
    final static long ALL_SCRUM_TYPES_SCHEME = 10204L;
    final static long ALL_TYPES_BUT_NO_DEFAULT_SCHEME = 10205L;
    final static long DEFAULT_SCHEME_ID = 10000L;
    final static long NO_BUGS_SCHEME = 10300L;
    final static long TASKS_AND_BUGS_SCHEME = 10202L;
    final static long TASKS_BUGS_AND_SUBTASKS_SCHEME = 10203L;
    final static long TASKS_ONLY_SCHEME = 10201L;

    //Issue Types
    final static long BUG = 10102L;
    final static long EPIC = 10000L;
    final static long FEATURE = 10104L;
    final static long IMPROVEMENT = 10103L;
    final static long STORY = 10001L;
    final static long TASK = 10100L;


    //projects
    final static long TSKS_PROJECT_ID =  10000L;
    final static long TSKSBUGS_PROJECT_ID =  10001L;



    void unhappyAuthenticationAndAuthorization(Runnable badCase) {

        System.out.println("zero");
        //authentication--anonymous requests/those from unknown users aren't allowed
        setAnonymousMode();
        TestUtil.assertErrorCode(Response.Status.UNAUTHORIZED, badCase);

        System.out.println("one");
        //unknown user
        setClient("idont", "exist");
        TestUtil.assertErrorCode(Response.Status.UNAUTHORIZED, badCase);

        System.out.println("two");
        //bad password of known user
        setClient("jsmith", "i forgot");
        TestUtil.assertErrorCode(Response.Status.UNAUTHORIZED, badCase);

        System.out.println("three");
        //insufficient privileges--only admin should be allowed to do this action
        setClient("jsmith", "password");
        TestUtil.assertErrorCode(Response.Status.FORBIDDEN, badCase);

        //revert back to the default
        setAdmin();
    }

    /**
     * FIXME:needs to go
     * @param secs
     */
    void delay(long secs) {
        try {
            Thread.sleep(secs *1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

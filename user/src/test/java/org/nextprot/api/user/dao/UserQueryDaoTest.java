package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserQueriesDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserQueryDaoTest extends UserApplicationBaseTest {

    @Autowired private UserQueryDao userQueryDao;

    @Test
    public void testGetUserQueries() {

        List<UserQuery> list = userQueryDao.getUserQueries("spongebob");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(2, list.size());

        assertExpectedUserQuery(list.get(0), 15, "spongebob", "myquery", "my first query", false, "sparql query", new HashSet<String>());
        assertExpectedUserQuery(list.get(1), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testGetUserQueryById() {

        UserQuery userQuery = userQueryDao.getUserQueryById(15);

        assertExpectedUserQuery(userQuery, 15, "spongebob", "myquery", "my first query", false, "sparql query", new HashSet<String>());
    }

    @Test
    public void testGetUserQueriesByTag() {

        List<UserQuery> list = userQueryDao.getUserQueriesByTag("public");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(1, list.size());

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testGetUserQueriesByUnknownTag() {

        List<UserQuery> list = userQueryDao.getUserQueriesByTag("publication");

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGetPublishedQueries() {

        List<UserQuery> list = userQueryDao.getPublishedQueries();

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(1, list.size());

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testGetTagsByQueryId() {

        SetMultimap<Long, String> tags = userQueryDao.getQueryTags(Arrays.asList(16L));

        assertEquals(1, tags.size());
        assertEquals(Sets.newHashSet("public"), tags.get(16L));
    }

    @Test
    public void testGetTagsByUnknownQueryId() {

        SetMultimap<Long, String> tags = userQueryDao.getQueryTags(Arrays.asList(17L));

        assertTrue(tags.isEmpty());
        assertEquals(Sets.<String>newHashSet(), tags.get(17L));
    }

    @Test
    public void testGetTagsByUnknownQueryIds() {

        SetMultimap<Long, String> tags = userQueryDao.getQueryTags(Arrays.asList(16L, 17L));

        assertTrue(!tags.isEmpty());
        assertEquals(Sets.newHashSet("public"), tags.get(16L));
        assertEquals(Sets.<String>newHashSet(), tags.get(17L));
    }

    @Test
    public void testCreateUserQuery() {

        UserQuery query = new UserQuery();

        query.setTitle("ma requete");
        query.setSparql("yet another sparql query");
        query.setOwnerId(24);

        long id = userQueryDao.createUserQuery(query);
        assertTrue(id > 0);

        UserQuery query2 = userQueryDao.getUserQueryById(id);

        assertExpectedUserQuery(query2, id, "tahitibob", "ma requete", null, false, "yet another sparql query", Sets.<String>newHashSet());
    }

    @Test
    public void testCreateUserQueryAllField() {

        UserQuery query = new UserQuery();

        query.setTitle("ma requete");
        query.setDescription("une simple requete");
        query.setSparql("yet another sparql query");
        query.setPublished(true);
        query.setOwnerId(24);

        long id = userQueryDao.createUserQuery(query);
        assertTrue(id > 0);

        UserQuery query2 = userQueryDao.getUserQueryById(id);

        assertExpectedUserQuery(query2, id, "tahitibob", "ma requete", "une simple requete", true, "yet another sparql query", Sets.<String>newHashSet());
    }

    @Test
    public void testCreateUserQueryTags() {

        userQueryDao.createUserQueryTags(16, Sets.newHashSet("great", "heavy"));

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public", "great", "heavy"));
    }

    @Test
    public void testDeleteUserQueryTags() {

        Set<String> accs = new HashSet<String>();
        accs.add("public");

        int count = userQueryDao.deleteUserQueryTags(16, accs);

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertEquals(1, count);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.<String>newHashSet());
    }

    @Test
    public void testDeleteUserQueryTags2() {

        Set<String> accs = new HashSet<String>();
        accs.add("pim");
        accs.add("pam");
        accs.add("poum");

        int count = userQueryDao.deleteUserQueryTags(16, accs);

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertEquals(0, count);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testDeleteUserQuery() {

        int count = userQueryDao.deleteUserQuery(16);

        assertEquals(1, count);
        assertNull(userQueryDao.getUserQueryById(16));
        assertTrue(userQueryDao.getQueryTags(Arrays.asList(16L)).isEmpty());
    }

    @Test
    public void testDeleteUserQuery2() {

        int count = userQueryDao.deleteUserQuery(17);

        assertEquals(0, count);
        assertNull(userQueryDao.getUserQueryById(17));
        assertTrue(userQueryDao.getQueryTags(Arrays.asList(17L)).isEmpty());
    }

    @Test
    public void testUpdateUserQuery() {

        UserQuery query = new UserQuery();

        query.setUserQueryId(16);
        query.setTitle("ma requete");
        query.setDescription("une simple requete");
        query.setSparql("yet another sparql query");
        query.setPublished(true);

        userQueryDao.updateUserQuery(query);

        query  = userQueryDao.getUserQueryById(16);

        assertExpectedUserQuery(query, 16, "spongebob", "ma requete", "une simple requete", true, "yet another sparql query", Sets.newHashSet("public"));
    }

    private static void assertExpectedUserQuery(UserQuery userQuery, long expectedUserQueryId, String expectedOwner, String expectedTitle,
                                                String expectedDescription, boolean expectedPublished, String expectedSparql,
                                                Set<String> expectedTags) {
        assertNotNull(userQuery);
        assertEquals(expectedUserQueryId, userQuery.getUserQueryId());
        assertEquals(expectedOwner, userQuery.getOwner());
        assertEquals(expectedOwner, userQuery.getResourceOwner());
        assertEquals(expectedDescription, userQuery.getDescription());
        assertEquals(expectedTitle, userQuery.getTitle());
        assertTrue(userQuery.getPublished() == expectedPublished);
        assertEquals(expectedSparql, userQuery.getSparql());
        assertEquals(expectedTags, userQuery.getTags());
    }
}
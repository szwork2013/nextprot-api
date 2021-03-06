package org.nextprot.api.user.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for operating (CRUD) on user queries (SPARQL)
 * 
 * @author dteixeira
 */
@Lazy
@Controller
@Api(name = "User Queries", description = "Method to manipulate user queries (SPARQL)", role = "ROLE_USER")
public class UserQueryController {

	@Autowired
	private UserQueryService userQueryService;

	@RequestMapping(value = "/queries/public", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getPublicUserQueries() {
		return userQueryService.getPublishedQueries();
	}
	
	@RequestMapping(value = "/queries/tutorial", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getTutorialUserQueries() {
		return userQueryService.getTutorialQueries();
	}
	
	
	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.GET })
	public List<UserQuery> getUserQueries(@PathVariable("username") String username) {
		return userQueryService.getUserQueries(username);
	}

	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.POST })
	@ResponseBody
	public UserQuery createAdvancedQuery(@RequestBody UserQuery advancedUserQuery, @PathVariable("username") String username) {
		advancedUserQuery.setOwner(username);
		return userQueryService.createUserQuery(advancedUserQuery);
	}

	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserQuery updateAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserQuery advancedUserQuery, Model model) {

		// Never trust what the users sends to you! Set the correct username, so it will be verified by the service,
		UserQuery q = userQueryService.getUserQueryById(advancedUserQuery.getUserQueryId());
		advancedUserQuery.setOwner(q.getOwner());

		return userQueryService.updateUserQuery(advancedUserQuery);
	}

	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.DELETE })
	public Model deleteAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		// Never trust what the users sends to you! Send the query with the correct username, so it will be verified by the service,
		long qid = Long.parseLong(id);
		UserQuery q = userQueryService.getUserQueryById(qid);
		userQueryService.deleteUserQuery(q);
		return model;

	}
	


}

package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.yoshikawaa.app.githubmanager.api.GithubApiService;

public class AbstractGithubApiController {

	@Autowired
	protected GithubApiService api;
}

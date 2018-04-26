# github-manager

Simple Github Management Application using [Github API v3](https://developer.github.com/v3/).

## Features

### Labels management
  
Manage your repository's labels.

* You can specify a repository, and create, update, and delete labels.
* You can specify a user/organization, and create, update, and delete labels.

### Milestones management

Manage your repository's milestones.

* You can specify a repository, and create, update, and delete milestones.
* You can specify a user/organization, and create, milestoness.

## How to use

Build and run application.

```console
$ mvn clean spring-boot:run
```

Access with brower `http://localhost:9999/github-manager/`.

### For HTTP Proxy Environment

Set http proxy as properties in `application-proxy.yml`.

```yaml
proxy:
  host: proxy host
  port: proxy port
  user: proxy auth user
  password: proxy auth password

```

Build and run application.

```console
$ mvn clean spring-boot:run -Dspring-boot.run.profiles=proxy
```

# github-manager

Simple Github Management Application using [Github API v3](https://developer.github.com/v3/).

## Features

### Labels management
  
Manage your repository's labels.

* You can specify a repository, and create, update, and delete labels.
* You can specify a user/organization, and create, update, and delete labels of all repositories.

> Notice:: label's description (GET/SET) is not supported by Github API.

### Milestones management

Manage your repository's milestones.

* You can specify a repository, and create, update, and delete milestones.
* You can specify a user/organization, and create, update, and delete milestones of all repositories.

### Issues and Pull Requests browsing 

Browse your and public repository's issues and pull requests.

* You can search issue and pull request, and access quickly.

### Pull Requests analysis 

Analysis your and public repository's pull requests.

* You can categorize and summary comments in pull requests.

## How to use

> Notice:: If you don't have client id, you must register App at [Github - Register a new OAuth application](https://github.com/settings/applications/new).

Set client id + secret as properties in `application.yml`.

```yaml
security:
  oauth2:
    client:
      client-id: client id
      client-secret: client secret
```

> Notice:: If you don't want to write client id + secret in a file, you can set using [command line arguments](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

Build and run application.

```console
$ mvn clean spring-boot:run
```

Access with brower `http://localhost:9999/github-manager/`.

### For HTTP Proxy Environment

Add http proxy as properties in `application.yml`.

```yaml
proxy:
  host: proxy host
  port: proxy port
  user: proxy auth user
  password: proxy auth password
```

## How to Configure

### Default Page Size

Set page size as properties in `application.yml`

```yaml
app:
  pagination:
    per-page: 100
```


### Categories of Pull Request Comments

Set categories key as properties in `application.yml`

```yaml
app:
  analysis:
    comment:
      categories:
        - "[good]"
        - "[bad]"
      category-default: "[other]"
```

### Disable Unavailable Repositories

> Notice:: If you get an error such as '422 Not Found' in the operation of a repository that you can refer to, you may not have write permission. In that case, you can disable the repositories.

Set repository name as properties in `application.yml`

```yaml
app:
  unavailable:
    repositories:
    - name of unavailable repository
```

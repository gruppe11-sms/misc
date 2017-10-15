# misc
Misc stuff needed for the project, that are not system specific


## Api server setup

1. Download traefik <https://github.com/containous/traefik/releases> for your operating system
   1. If you are using windows you need to add .exe to the file name to be able to start it
   2. For Mac and Linux the downloaded file need to marked as executeable. 
2. Clone this repo `git clone git@github.com:gruppe11-sms/misc.git`
3. Navigate into the cloned repository
4. Start traefik: `path/to/traefik/executeable -c traefik.toml`

Traefik should now be running, you can check by opening <http://localhost:8080> in you webbrowser

### Configure the backends
The current Traefik configuration expects the following:
* The role system running on port 8084
* The auditsystem running on port 8085
* The frontend running on port 4200

The frontend by default runs on port 4200. However the role system and auditsystem needs to be changed using 
the `server.port` option in the application configuration


### Adding new services
Traefik follows a pretty simple configuration scheme. To access a service from the outside you need to 
configure a backend and a frontend for it. 

#### Configuring the backend
A backend is added under the `[backends]` tag. So if I need 
to add a new service called wiki i will add: `[backends.wiki]` under `[backends]`. Then the actual
servers that host the service needs to be configured. This is done by adding `[backends.wiki.servers.serverNum]`. 
It's possible to configure multiple servers for each backend, and Traefik will load balance between them. 
For each server you need to configure a url, like so: `url = "http://localhost:6842"`, if my server runs on 
port 6842. A weight also needs to be added, this is mostly for the Traefik load balancer, and can just be 
set to 1: `weight = 1`. 

### Configuring the frontend
Frontends are added under the `[frontends]` tag. For each frontend you need to give it a name, tell it what 
backend to use, and setup rules for when the traffik should be routed to that backend. 
First thing first, giving it a name works just like for backends, you simple add a tag: `[frontends.wiki]`, if we 
are making a frontend for the wiki system. Under that tag we tell Traefik which backend that should serve the 
frontend, simply by doing: `backend = "wiki"`.  
Next we add rules for when the trafik should be routed to that backend, this is done under the routes for the service. 
For example if we want all trafik that hits `/api/wiki` to go to the wiki system we simply add:
```toml
[frontends.wiki.routes.wiki_api]
rule = "Path:/api/wiki"
```

More rule options for the frontends can be found in traefiks own documentation: <https://docs.traefik.io/basics/#frontends>

### Example configuration for entire system
So a pure configuration for the entire wiki system would look, like this:

```toml
[backends]
  ... (Other backends here) ...
 
  [backends.wiki]
    url = "http://localhost:6842"
    weight = 1
  
[frontends]
  ... (Other frontends here) ...
  
  [frontends.wiki]
  backend = "wiki"
    [frontends.wiki.routes.wiki_api]
    rule = "Path:/api/wiki"
```

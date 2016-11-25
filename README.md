
- an `Actor` continiously checks a service and emits `StatusEvent`
- another `Actor` streams the response based on `StatusEvent`


```
/usr/local/sbtool/bin/sbt run
``` 


- `http://localhost:9000/health` publishes a `CheckHealthCommand`, in response to which
`HealthCheckMonitor` publishes `ServiceStatusEvent` which would be `Running` or `Stopped`, `Starting`.


- then there's another `Actor` which consumes the `ServiceStatuse`s and responds to UI. 
Right now it just prints on console, but the goal is to make UI to continiously get those events.


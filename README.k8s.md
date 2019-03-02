# Minikube
## Installation
https://github.com/kubernetes/minikube#installation

## Commands
Start
```
$ minikube start
```
Status
```
$ minikube status
```

# Kubernetes

## Set Up
```sh
$ source <(kubectl completion bash)
```

Run a local registry (ignore this step if using DockerHub)
```sh
$ docker run -d -p 5000:5000 registry
```

Build Docker Image
```sh
$ docker build -t ramays/springldap .
```

Retag to Push to Local Registry
```sh
$ docker tag ramays/springldap localhost:5000/dev/ramays/springldap
```

Push to Local Registry
```sh
$ docker push localhost:5000/dev/ramays/springldap
```

Run a single pod using the Kubernetes command line, inspect the Pod and then invoke the Spring Boot App
```sh
$ kubectl run springldap --image=localhost:5000/dev/ramays/springldap --port=9090 --generator=run-pod/v1

$ kubectl get pods
NAME         READY   STATUS    RESTARTS   AGE
springldap   1/1     Running   0          107s

$ kubectl describe pod/springldap
Name:               springldap
Namespace:          default
Priority:           0
PriorityClassName:  <none>
Node:               minikube/192.168.0.21
Start Time:         Tue, 19 Feb 2019 23:47:12 +0000
Labels:             run=springldap
Annotations:        <none>
Status:             Running
IP:                 172.17.0.5
Containers:
  springldap:
    Container ID:   docker://59d3327a4135b62a7d5d01a9b0cb3923953757f6ba1845647f9c49ded5b97205
    Image:          localhost:5000/dev/ramays/springldap
    Image ID:       docker-pullable://localhost:5000/dev/ramays/springldap@sha256:3141232e1e660a788bf14f83d97ed4e1c240f1691c779fd7ac2fc8bee076a7f4
    Port:           9090/TCP
    Host Port:      0/TCP
    State:          Running
      Started:      Tue, 19 Feb 2019 23:47:13 +0000
    Ready:          True
    Restart Count:  0
    Environment:    <none>
    Mounts:
      /var/run/secrets/kubernetes.io/serviceaccount from default-token-rbxnm (ro)
Conditions:
  Type              Status
  Initialized       True
  Ready             True
  ContainersReady   True
  PodScheduled      True
Volumes:
  default-token-rbxnm:
    Type:        Secret (a volume populated by a Secret)
    SecretName:  default-token-rbxnm
    Optional:    false
QoS Class:       BestEffort
Node-Selectors:  <none>
Tolerations:     node.kubernetes.io/not-ready:NoExecute for 300s
                 node.kubernetes.io/unreachable:NoExecute for 300s
Events:
  Type    Reason     Age   From               Message
  ----    ------     ----  ----               -------
  Normal  Scheduled  3m3s  default-scheduler  Successfully assigned default/springldap to minikube
  Normal  Pulling    3m2s  kubelet, minikube  pulling image "localhost:5000/dev/ramays/springldap"
  Normal  Pulled     3m2s  kubelet, minikube  Successfully pulled image "localhost:5000/dev/ramays/springldap"
  Normal  Created    3m2s  kubelet, minikube  Created container
  Normal  Started    3m2s  kubelet, minikube  Started container

$ curl http://172.17.0.5:9090
<!DOCTYPE html>.....
```

# General Inspection Commands
Check Services - Pods
```sh
$ kubectl get pods
```

Check Services - Replication Controllers
```sh
$ kubectl get rc
```

Check Services
```sh
$ kubectl get services
```

Pod information
```sh
$ kubectl get pods -o wide
$ kubectl describe pod springldap-xxxxx
$ kubectl get po springldap-xxxxx -o yaml
```

Meta Information
```sh
$ kubectl explain pods
$ kubectl explain pods.spec
```

CleanUp
```sh
$  kubectl delete all --all
pod "springldap-cdxlk" deleted
pod "springldap-cgjsl" deleted
pod "springldap-lw48v" deleted
replicationcontroller "springldap" deleted
service "kubernetes" deleted                     <-- will be recreated!
service "springldap-nodeport" deleted
```

## Create a Pod Using YAML
```YAML
apiVersion: v1
kind: Pod
metadata:
  name: springldap-manual
spec:
  containers:
  - image: localhost:5000/dev/ramays/springldap
    name: springldap
    ports:
    - containerPort: 9090
      protocol: TCP
```

```sh
$ kubectl create -f springldap-manual.yaml
```

Create a Replication Controller (which will automatically create the pods)
```YAML
apiVersion: v1
kind: ReplicationController
metadata:
  name: springldap
spec:
  replicas: 3
  selector:
    app: springldap
  template:
    metadata:
      labels:
        app: springldap
    spec:
      containers:
      - name: springldap
        image: localhost:5000/dev/ramays/springldap
        ports:
        - containerPort: 9090
```

```sh
$ kubectl create -f springldap-rc.yaml
$ kubectl get pods,rc,services
NAME                   READY   STATUS    RESTARTS   AGE
pod/springldap-l9vkj   1/1     Running   0          54s
pod/springldap-l9xsh   1/1     Running   0          54s
pod/springldap-vcsl4   1/1     Running   0          54s

NAME                               DESIRED   CURRENT   READY   AGE
replicationcontroller/springldap   3         3         3       54s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   7m29s
```

# Using LoadBalancers and NodePorts

Clean up and recreate the pods using a Replication Controller
```sh
$ kubectl delete all --all
$ kubectl create -f springldap-rc.yaml
```

Expose Spring Boot Pods Through a Load-Balancer (not supported in Minikube v0.30.0)
```sh
$ kubectl expose rc springldap --type=LoadBalancer --name springldap-http
```

Expose Spring Boot Pods Through a NodePort
```sh
$ kubectl expose rc springldap --name springldap-nodeport
```

Check Services
```sh
$ kubectl get services
NAME                  TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
kubernetes            ClusterIP   10.96.0.1       <none>        443/TCP    71d
springldap-nodeport   ClusterIP   10.109.11.173   <none>        9090/TCP   6s
$ curl 10.109.11.173:9090
```

Scale Up
```sh
$ kubectl scale rc springldap --replicas=3
```

Delete a Single Pod (A new one will be started by the RC)
```sh
$ kubectl delete po springldap-xxxxx
```

Display Pods along with the app label
```sh
$ kubectl get pods -L app -o wide
```

Edit RC YAML and Apply Changes
```sh
$ kubectl edit rc springldap
```

Delete RC but Keep the Pods Runnning
```sh
$ kubectl delete rc springldap --cascade=false
```

## Create ReplicaSet
```YAML
apiVersion: apps/v1beta2
kind: ReplicaSet
metadata:
  name: springldap
spec:
  replicas: 3
  selector:
    matchLabels:
      app: springldap
  template:
    metadata:
      labels:
        app: springldap
    spec:
      containers:
      - name: springldap
        image: localhost:5000/dev/ramays/springldap
```

A ReplicaSet is the replacement for ReplicationControllers
```sh
$ kubectl delete all --all
$ kubectl create -f springldap-rs.yaml
$ kubectl get pods,rs,services
NAME                   READY   STATUS    RESTARTS   AGE
pod/springldap-4m5bk   1/1     Running   0          10s
pod/springldap-n5r6p   1/1     Running   0          10s
pod/springldap-zspzn   1/1     Running   0          10s

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.extensions/springldap   3         3         3       10s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   23s
```

## Create a Service to Wrap the Pods
```YAML
apiVersion: v1
kind: Service
metadata:
  name: springldap
spec:
  ports:
  - port: 80
    targetPort: 9090
  selector:
    app: springldap
```

```sh
$ kubectl delete all --all
$ kubectl create -f springldap-svc.yaml
service/springldap created

$ kubectl get pods,rc,services
NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP   37s
service/springldap   ClusterIP   10.107.146.119   <none>        80/TCP    3s
```
Note that only the service is createed and no backing pods so curl at the service address does not do anything
```sh
$ curl 10.107.146.119
curl: (7) Failed to connect to 10.107.146.119 port 80: Connection refused
```

Service is targeted at port 9090 of the pods
```sh
$ kubectl describe service/springldap
Name:              springldap
Namespace:         default
Labels:            <none>
Annotations:       <none>
Selector:          app=springldap
Type:              ClusterIP
IP:                10.107.146.119
Port:              <unset>  80/TCP
TargetPort:        9090/TCP
Endpoints:         <none>
Session Affinity:  None
Events:            <none>
```

Create the ReplicaSet which will create the pods
```sh
$ kubectl create -f springldap-rs.yaml
replicaset.apps/springldap created

$ kubectl get pods,rs,services
NAME                   READY   STATUS    RESTARTS   AGE
pod/springldap-nz8hx   1/1     Running   0          9s
pod/springldap-s2xjz   1/1     Running   0          9s
pod/springldap-t5r4d   1/1     Running   0          9s

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.extensions/springldap   3         3         3       9s

NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP   3m48s
service/springldap   ClusterIP   10.107.146.119   <none>        80/TCP    3m14s
```
Now we can invoke the spring boot service - note that this does not work from another machine
```sh
$ curl 10.107.146.119
<!DOCTYPE html>.....
```

```sh
$ curl 10.101.78.142  (does not work externally)
```

Get the environment for a particular Pod which will show the host and port that exposes this Pod through a Service.
```sh
$ kubectl get services
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)   AGE
kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP   5d16h
springldap   ClusterIP   10.106.116.230   <none>        80/TCP    5d16h

$ kubectl exec springldap-xxx env
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/jdk/bin
HOSTNAME=springldap-rkbfp
SPRINGLDAP_PORT_80_TCP_PORT=80                        
SPRINGLDAP_SERVICE_HOST=10.106.116.230                 <----
SPRINGLDAP_PORT_80_TCP=tcp://10.106.116.230:80
SPRINGLDAP_PORT_80_TCP_ADDR=10.106.116.230
KUBERNETES_SERVICE_HOST=10.96.0.1                      <----
KUBERNETES_PORT_443_TCP_ADDR=10.96.0.1
KUBERNETES_PORT=tcp://10.96.0.1:443
KUBERNETES_PORT_443_TCP=tcp://10.96.0.1:443
KUBERNETES_PORT_443_TCP_PROTO=tcp
KUBERNETES_SERVICE_PORT=443                            <----
KUBERNETES_SERVICE_PORT_HTTPS=443
SPRINGLDAP_PORT=tcp://10.106.116.230:80
SPRINGLDAP_PORT_80_TCP_PROTO=tcp
KUBERNETES_PORT_443_TCP_PORT=443
SPRINGLDAP_SERVICE_PORT=80                             <----
JAVA_VERSION_MAJOR=8
JAVA_VERSION_MINOR=192
JAVA_VERSION_BUILD=12
JAVA_PACKAGE=server-jre
JAVA_JCE=standard
JAVA_HOME=/opt/jdk
GLIBC_REPO=https://github.com/sgerrand/alpine-pkg-glibc
GLIBC_VERSION=2.28-r0
LANG=C.UTF-8
HOME=/root
```
Note that all services are available through environment variables. So if a mydatabase service is created, for example, that wil be available through `MYDATABASE_SERVICE_HOST` and `MYDATABASE_SERVICE_PORT`

You cannot ping a Service IP because it is a virtual IP address and has no meaning without the port
```sh
$ curl 10.106.116.230
<!DOCTYPE html><html....

$ ping 10.106.116.230
PING 10.106.116.230 (10.106.116.230) 56(84) bytes of data.
^C
--- 10.106.116.230 ping statistics ---
7 packets transmitted, 0 received, 100% packet loss, time 6131ms
```

## Exposing the Service to External Clients

The Service can be exposed as a LoadBalancer, a NodePort or through an Ingress Service

### NodePort

For a NodePort Service, each cluster node open a port on the node itself will redirect traffic received on that cluster node to the underlying Service.  Then the Service is accessible through a dedicated port on all nodes.  The NodePort values are from 30000 to 32768

```YAML
apiVersion: v1
kind: Service
metadata:
  name: springldap-nodeport
spec:
  type: NodePort
  ports:
  - port: 80
    targetPort: 9090
    nodePort: 19090
  selector:
    app: springldap
```
Note that minikube only has a single node.

```sh
$ kubectl get -o wide nodes
NAME       STATUS   ROLES    AGE   VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION      CONTAINER-RUNTIME
minikube   Ready    master   88d   v1.13.3   192.168.0.21   <none>        Ubuntu 18.04.1 LTS   4.15.0-45-generic   docker://18.9.1

$ curl 192.168.0.21:30090
<!DOCTYPE html><html lang="en">....

$ kubectl get svc
NAME                  TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
kubernetes            ClusterIP   10.96.0.1        <none>        443/TCP        5d16h
springldap            ClusterIP   10.106.116.230   <none>        80/TCP         5d16h
springldap-nodeport   NodePort    10.110.130.36    <none>        80:30090/TCP   5m21s

$ curl 10.110.130.36
<!DOCTYPE html><html lang="en">....
```
The same would work for all Node IP addresses at port `30090`

## Configuration and Passwords through Kubernetes ConfigMaps and Secrets

Kubernetes allows configuration options to be separated out into ConfigMaps, simple maps with key value pairs in which the values can be literals to files, which the application does not need to be aware of.  The contents of the map are made available to the containers as environment variables or as files in a volume.

### Creating ConfigMap from a command line
```sh
$ kubectl create configmap spring-ldap-configmap --from-literal=database-name=MYDB
```

### Creating ConfigMap from a file

```sh
$ kubectl create configmap spring-ldap-configmap --from-file=application.properties
```

### Providing a ConfigMap entry to a Container

#### Single Entry As an Environment Variable
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: springldap-manual
spec:
  containers:
  - image: localhost:5000/dev/ramays/springldap
    env:                                          <----
    - name : INTERVAL                             <----
      valueFrom:                                  <----
        configMapKeyRef:                          <----
          name: spring-ldap-configmap             <----
          key: database-name                      <----
    name: springldap
    ports:
    - containerPort: 9090
      protocol: TCP
```

#### All entries as Environment Variables with a Prefix
The prefix is optional but useful for grouping config entries.
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: springldap-manual
spec:
  containers:
  - image: localhost:5000/dev/ramays/springldap
    envFrom:                                      <----
    - prefix: CONFIG_                             <----
      configMapRef:                               <----
        name: spring-ldap-configmap               <----
    name: springldap
    ports:
    - containerPort: 9090
      protocol: TCP
```

### Create Secret from the Command Line
```sh
kubectl create secret generic spring-ldap-secret --from-literal=database-password=abc123
```

### Using the Secret through an Environment Variable
```yaml
    env:
    - name : DATABASE-PASSWORD
      valueFrom:
        configMapKeyRef:
          name: spring-ldap-secret
          key: database-password
```
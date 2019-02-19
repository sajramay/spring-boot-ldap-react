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

Start the Kubectl instance
```sh
$ kubectl run springldap --image=localhost:5000/dev/ramays/springldap --port=9090 --generator=run/v1
```

Check Services - Pods
```sh
$ kuvectl get pods
```

Check Services - Replication Controllers
```sh
$ kuvectl get rc
```

Check Services
```sh
$ kubectl get services
```

Expose Service Through a Load-Balancer (not supported in Minikube v0.30.0)
```sh
$ kubectl expose rc springldap --type=LoadBalancer --name springldap-http
```

Expose  Service Through a NodePort
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

Manually Create a Pod
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
$ kubectl delete rc kubia --cascade=false
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

## Create Services

```sh
$ kubectl  delete all --all
$ kubectl create -f springldap-rc.yaml
$ kubectl get pods,rc,services
NAME                   READY   STATUS    RESTARTS   AGE
pod/springldap-2ntqv   1/1     Running   0          3s
pod/springldap-76n5j   1/1     Running   0          3s
pod/springldap-k9dwk   1/1     Running   0          3s

NAME                               DESIRED   CURRENT   READY   AGE
replicationcontroller/springldap   3         3         3       3s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   42s
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

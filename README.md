# Lagom on Kube

This *tutorial* describes how to bootstrap a production-ready [Lagom][lagom] microservices cluster in a [Kubernetes][kubernetes] environment.

![alt lagom on kube][tech]

## The Problem

[Lagom][lagom] is a great **java**- and **scala**-based [reactive][reactive] microservices framework using enterprise-ready [Lightbend][lightbend] technologies. The only issue with lagom is that its free version is intended for the development only; if you want to use it for production, Lightbend offers a comercial version including [ConductR][conductr] or the [Lightbend reactive platform][lightbend-ra].

With this tutorial, you can bootstrap a [Kubernetes][kubernetes] cluster hosting [Lagom][lagom] microservices, which is **free-to-use**, however **production-ready** and based on **enterprise technology**.

The [Lagom framework][lagom] lacks following fuctionality and attributes required for a production-ready microservices framework:

- Service orchestration
- Scalability
- Monitoring
- Resilience
- Load balancing
- Service discovery

Turns out, all of the above can be achieved with [Docker][docker] and [Kubernetes][kubernetes]. (I'm pretty sure it's also possible using [Docker Swarm][docker-swarm] or [Apache Mesos][apache-mesos], but those ways are outside of the scope of this tutorial.)

## The Solution

- **Service orchestration**: we package all of the Lagom services into Docker containers, which are then 'orchestrated' (i.e. deployed, scaled, managed, etc.) through standard Kubernetes tools.

- **Scalability** is provided on two levels
  - Within a Kubernetes cluster, scaling is performed by *Deployments* and *Replication controllers*
  - AWS offers *Auto Scaling group* for further scalability when a Kubernetes cluster hits its limit

- **Monitoring** is provided on two levels as well
  - Heapster (InfluxDB and Grafana) to monitor containers and nodes
  - New Relic to monitor everything at service level

- **Resilience**
  - *Kubernetes Pods* can be configured to track the health status of Docker containers
  - *Deployments* keep number of active containers in line with current load

- **Load balancing** is provided by Kubernetes *services* which select a Pod randomly for each request (more complex load balancing strategies are available using [Ingress][ingress] or an external load balancer, however these methods are outside of the scope of this document)

- [**Service Discovery**][service-discovery] was the *only part* which had to be implemented, not just configured. It is based on the free-to-use Lagom develempent-mode service register and received a number of improvements making it resilient, persistent and compatible with the Kubernetes architecture.

## The Technologies

- [AWS][aws] as a cloud platform
- [kops][kops] to bootstrap Kubernetes on AWS
- [Kubernetes][kubernetes] for service orchestration
- [Ansible][ansible] for configuration management
- [Docker][docker] as a container
- [Lagom][lagom] as a microservices framework
- [Heapster][heapster] for container and node monitoring
- [New Relic][new relic] for application monitoring
- [Cassandra][cassandra] as a persistent storage for service registry

## The Description

The Demo infrastructure is described in [this document][description]

[lagom]: https://www.lagomframework.com/
[reactive]: http://www.reactivemanifesto.org/
[lightbend]: http://www.lightbend.com/platform
[conductr]: https://conductr.lightbend.com/
[lightbend-ra]: http://www.lightbend.com/platform
[docker-swarm]: https://docs.docker.com/engine/swarm/
[apache-mesos]: http://mesos.apache.org/
[service-discovery]: lagomKubeServiceRegister
[aws]: https://console.aws.amazon.com
[kops]: https://github.com/kubernetes/kops/blob/master/docs/aws.md
[kubernetes]: https://kubernetes.io/
[ansible]: https://www.ansible.com/
[docker]: https://www.docker.com/
[heapster]: https://github.com/kubernetes/heapster
[new relic]: https://newrelic.com/
[cassandra]: http://cassandra.apache.org/
[ingress]: https://kubernetes.io/docs/concepts/services-networking/ingress/
[description]: ./docs/service-description.md
[tech]: https://alexanderray.github.io/lagom-on-kube-dev/img/tech_web.svg

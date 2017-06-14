# Main Tutorial: How to build and deploy the application

1. Creating and publishing **Docker containers**

    First, we need to build **Docker images** from application sources:
    - [Create a Docker image for the Lagom Kube Service Registry][lagom-kube-service-registry]
    - [Create a Docker image for the Lagom Kube services][lagom-kube-services]

1. Deploying the application

    There are three deployment methods available:

    a) Manual deployment on **MiniKube**:

     The [Minikube deployment tutorial][minikube-deployment] describes how to deploy the application infrastructure on a local machine with MiniKube:

    b) Manual deployment on **AWS**

     These four tutorials describe the process of manual deployment on [Amazon Web Services][amazon-webservices]:
     1. [CLI Tools][cli-tools]: preparing your system for further deployment
     1. [AWS][aws]: making your **Amazon account** kops-ready
     1. [kops][kops]: manually creating and configuring a cluster on AWS
     1. [Kubernetes][kubernetes]: deploying the application to the cluster using previously prepared Docker images

    c) Automated deployment on **AWS**

     For the automated deployment, read the [Ansible tutorial][ansible].

[lagom-kube-service-registry]: ../lagomKubeServiceRegister/README.md
[lagom-kube-services]: ../lagomKubeServices/README.md
[minikube-deployment]: minikube.md
[cli-tools]: cli-tools.md
[aws]: aws.md
[kops]: kops.md
[kubernetes]: kubernetes.md
[ansible]: ansible.md
[amazon-webservices]: https://aws.amazon.com

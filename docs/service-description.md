# Service Description

## This Github project serves as an example for using our technologies and the Lagom on Kube framework

- This document explains how to set up and start up a Kubernetes cluster with Lagom services, either on AWS or in a Minikube

    ![alt kubernetes services][main]

- We have created 3 demo services connected to the Service Registry

  - A Telegram bot Lagom service forwarding requests to the Wolfram Lagom service

  - A Wolfram Lagom service forwarding requests from the Telegram bot to the Wolfram Alpha engine, then forwards the response back to the Telegram bot Lagom service

  - A HelloWorld service simply returning environment variables

  ![alt lagom services][lagom-services]

- These 3 services, as well as the Service Registry, are deployed into the Kubernetes cluster

  ![alt kubernetes][kubernetes]

## Based on these projects, scripts, and tutorials you will learn to

1. Compile and configure the [Service Registry][1]

1. Compile and configure the [Lagom demo services][2]

1. Deploy [Kubernetes on Minicube][3] locally

1. Deploy a [Kubernetes cluster on AWS using kops][4]

1. Automatically deploy a [Kubernetes cluster on AWS using Ansible][5]

## Tutorial

See the [tutorial][tutorial] to learn how to build and deploy an application using the example of a Telegram bot.

[main]: https://alexanderray.github.io/lagom-on-kube-dev/img/main.svg
[lagom-services]: https://alexanderray.github.io/lagom-on-kube-dev/img/lagom_kube_services.svg
[kubernetes]: https://alexanderray.github.io/lagom-on-kube-dev/img/kubernetes.svg
[1]: ./../lagomKubeServiceRegister/README.md
[2]: ./../lagomKubeServices/README.md
[3]: ./minikube.md
[4]: ./kops.md
[5]: ./ansible.md
[tutorial]: ./tutorial.md

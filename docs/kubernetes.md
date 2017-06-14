# Setting up Kubernetes

1. Install the [Kubernetes dashboard][dashboard]

    ```shell
    kubectl create -f https://raw.githubusercontent.com/kubernetes/kops/master/addons/kubernetes-dashboard/v1.5.0.yaml
    ```

1. Obtain the dashboard password (user is *admin*)

    ```shell
    kops get secrets -oplaintext --type=secret kube
    ```

1. Bootstrap [Heapster][heapster] (including InfluxDB and Grafana) for monitoring, and [configure it][heapster-configs] based on [these configuration files][heapster-orig-configs]

    ```shell
    kubectl create -f ./kube/manuell/heapster
    ```

1. Create namespaces for the **Tools** and **Lagom services**

    ```shell
    kubectl create ./kube/manuell/000.namespaces.yaml
    ```

1. Install the [Apache Cassandra][cassandra] database and [configure][cassandra-config] it based on [these configuration files][cassandra-orig-config]

    ```shell
    kubectl create -f ./kube/manuell/cassandra/cassandra.yaml
    ```

## Now, the Kubernetes infrastructure is ready for installation of Lagom Kube Service Registry and Lagom Kube services

1. Install the **Lagom Kube Service Registry**

    ```shell
    kubectl create -f ./kube/manuell/001.lrs.service.yaml
    kubectl create -f ./kube/manuell/002.lrs.deployment.yaml
    ```

1. Configure the **Telegram bot token** for your build
    If you don't have a Telegram bot token yet, create a **Telegram bot** as described in the [Introduction for Telegram developers][introduction-telegram-developers].

    Enter your bot's **token** into the [Telegram bot service configuration][telegrambot-service-config-24]:
      ```yaml
      ...
      value: {{ TELEGRAM_BOT_TOKEN }}
      ...
      ```

1. Install the **Lagom services**

    ```shell
    kubectl create -f ./kube/manuell/003.telegrambot.service.yaml
    kubectl create -f ./kube/manuell/004.telegrambot.deployment.yaml
    kubectl create -f ./kube/manuell/005.helloworldsvc.service.yaml
    kubectl create -f ./kube/manuell/006.helloworldsvc.deployment.yaml
    ```

1. Configure the [Wolfram Alpha][wolfram-alpha] **AppID** for your build

    If you don't have a **Wolfram Alpha AppID** yet:
    - Create an account on the [Wolfram Alpha Developer Portal][wolfram-alpha]
    - Create a new Wolfram Alpha **application**

    Enter your **AppID** into the [Wolfram service configuration][wolfram-service-config-47]:
      ```yaml
      ...
      - name: WOLFRAM_APPID
        value: {{ WOLFRAM_APPID }}
      ...
      ```

1. Create a **New Relic account**

    Create an account at [New Relic][new-relic] and enter the **license key** provided
    both into the [Wolfram service configuration][wolfram-service-config-49]
    and into the [Service Registry deployment configuration][services-register-deployment-config-21]:
      ```yaml
      ...
      - name: NEW_RELIC_LICENSE_KEY
        value: {{ NEW_RELIC_LICENSE_KEY }}
      ...
      ```

1. Install the **Wolfram Alpha Kube Service**

    ```shell
    kubectl create -f ./kube/manuell/007.wolfram-svc.yaml
    ```

1. Configure the **Service Registry routing**

    ```shell
    kubectl port-forward {{ lagom-kube-service-register }} 8000 --namespace=tools
    kubectl port-forward {{ lagom-kube-service-register }} 9000 --namespace=tools
    ```

    We can now test our application:
    - Check if all of three services are registered in the Service Registry
    ```http://127.0.0.1:8000/services```
    - Go to ```http://localhost:9000/api/helloworldservice/v1/list-environment``` and check if the HelloWorld service is up

1. Launch **Telegram** and test the bot: ```/test``` and ```/ask {{ question }}```

[dashboard]: https://github.com/kubernetes/dashboard
[heapster]: https://github.com/kubernetes/heapster
[heapster-configs]: ../kube/manuell/heapster
[heapster-orig-configs]: https://github.com/kubernetes/heapster/tree/master/deploy/kube-config/influxdb
[cassandra]: http://cassandra.apache.org/
[cassandra-config]: ../kube/manuell/cassandra/cassandra.yaml
[cassandra-orig-config]: https://github.com/kubernetes/kubernetes/tree/master/examples/storage/cassandra
[telegrambot-service-config-24]: ../kube/manuell/004.telegrambot.deployment.yaml#L24
[wolfram-alpha]: https://developer.wolframalpha.com/portal
[wolfram-service-config-47]: ../kube/manuell/007.wolfram-svc.yaml#L47
[new-relic]: https://newrelic.com
[wolfram-service-config-49]: ../kube/manuell/007.wolfram-svc.yaml#L49
[services-register-deployment-config-21]: ../kube/manuell/002.lrs.deployment.yaml#L21

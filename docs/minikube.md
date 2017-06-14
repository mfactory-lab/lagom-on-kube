# Minikube deployment

1. Install [Minikube][minikube]

    Install **Minikube** as described in [this how-to][minikube] and run it:
    ```shell
    minikube start
    ```

1. Install the [Kubernetes Dashboard][dashboard]
    // TODO: Minikube has its own dashboard: `minikube dashboard`

    ```shell
    kubectl create -f https://raw.githubusercontent.com/kubernetes/kops/master/addons/kubernetes-dashboard/v1.5.0.yaml
    ```

    After installation, launch the dashboard as a proxy:

    `kubectl proxy --port=8080`
    
    It is now accessible at
    
    `http://localhost:8080/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard`

1. Bootstrap [Heapster][heapster] (including **InfluxDB** and **Grafana**) for monitoring, and [configure][heapster-configs] it based on [these configuration files][heapster-orig-configs]

    ```shell
    kubectl create -f ./kube/heapster
    ```

    **Grafana** is now available at `http://localhost:8080/api/v1/proxy/namespaces/kube-system/services/monitoring-grafana`.

1. Create namespaces for the **Tools** and **Lagom services**

    ```shell
    kubectl create ./kube/000.namespaces.yaml
    ```

1. Install the [Apache Cassandra][cassandra] database and deploy the [configuration files][cassandra-config] (developed based on [these configurations][cassandra-orig-config])

    ```shell
    kubectl create -f ./kube/cassandra/cassandra_minikube.yaml
    ```

## Now, the Kubernetes infrastructure is ready for installation of Lagom Kube Service Registry and Lagom Kube services

1. Install the **Lagom Kube Service Registry**

    ```shell
    kubectl create -f ./kube/001.lrs.service.yaml
    kubectl create -f ./kube/002.lrs.deployment.yaml
    ```

1. Configure the **Telegram bot token** for your build

    If you don't have a Telegram bot token yet, create a **Telegram bot** as described in the [Introduction for Telegram developers][introduction-telegram-developers].

    Enter your bot's token into the [Telegram bot service configuration][telegrambot-service-config-24]:
      ```yaml
      ...
      value: {{ TELEGRAM_BOT_TOKEN }}
      ...
      ```

1. Install the **Lagom services**

    ```shell
    kubectl create -f ./kube/003.telegrambot.service.yaml
    kubectl create -f ./kube/004.telegrambot.deployment.yaml
    kubectl create -f ./kube/005.helloworldsvc.service.yaml
    kubectl create -f ./kube/006.helloworldsvc.deployment.yaml
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

1. Create a **New Relic** account

    Create an account at [New Relic][new-relic] and enter the license key provided
    both into the [Wolfram service configuration][wolfram-service-config-49]
    and into the [Service Registry Deployment configuration][services-register-deployment-config-21]:
      ```yaml
      ...
      - name: NEW_RELIC_LICENSE_KEY
        value: {{ NEW_RELIC_LICENSE_KEY }}
      ...
      ```

1. Install the **Wolfram Alpha Kube Service**

    ```shell
    kubectl create -f ./kube/007.wolfram-svc.yaml
    ```

1. Configure the **Service Registry routing**

    ```shell
    kubectl port-forward {{ lagom-kube-service-register }} 8000 --namespace=tools
    kubectl port-forward {{ lagom-kube-service-register }} 9000 --namespace=tools
    ```

1. We can now test our application:
    - Check if all three **services** are registered in the **Service Registry**:
    
    `http://127.0.0.1:8000/services`
    - Check if the **HelloWorld service** is up:
    
    `http://localhost:9000/api/helloworldservice/v1/list-environment`

1. Launch Telegram and test the bot: `/test` and `/ask {{ question }}`

[minikube]: https://github.com/kubernetes/minikube
[dashboard]: https://github.com/kubernetes/dashboard
[heapster]: https://github.com/kubernetes/heapster
[heapster-configs]: ../kube/heapster
[heapster-orig-configs]: https://github.com/kubernetes/heapster/tree/master/deploy/kube-config/influxdb
[cassandra]: http://cassandra.apache.org/
[cassandra-config]: ../kube/cassandra/cassandra_minikube.yaml
[cassandra-orig-config]: https://github.com/kubernetes/kubernetes/tree/master/examples/storage/cassandra
[wolfram-alpha]: https://developer.wolframalpha.com/portal
[telegrambot-service-config]: ../kube/004.telegrambot.deployment.yaml
[telegrambot-service-config-24]: ../kube/004.telegrambot.deployment.yaml#L24
[wolfram-service-config]: ../kube/007.wolfram-svc.yaml
[wolfram-service-config-47]: ../kube/007.wolfram-svc.yaml#L47
[wolfram-service-config-49]: ../kube/007.wolfram-svc.yaml#L49
[services-register-deployment-config]: ../kube/002.lrs.deployment.yaml
[services-register-deployment-config-21]: ../kube/002.lrs.deployment.yaml#L21
[new-relic]: https://newrelic.com
[introduction-telegram-developers]: https://core.telegram.org/bots#6-botfather

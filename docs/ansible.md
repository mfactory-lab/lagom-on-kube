# Ansible

This how-to uses [Ansible configuration][ansible] to bootstrap all services. It is based on [github.com/scholzj/aws-k8s-kops-ansible][ansible-kubernetes].

- First, you need to [set up the variables][ansible-vars]

- Then you can bootstrap your cluster with demo services

```shell
ansible-playbook ./kube/ansible/bootstrap-all.yaml
```

[ansible]: http://docs.ansible.com/ansible/index.html
[ansible-kubernetes]: https://github.com/scholzj/aws-k8s-kops-ansible
[ansible-vars]: ansible-vars.md

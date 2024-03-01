---
layout: minimal
title: Google Kubernets Engine - GKE
nav_exclude: true
---

# Google Kubernets Engine - GKE

Alguns comandos úteis para a criação e deploy no GKE

Criar um cluster no GKE:

    gcloud init
    gcloud components update

    gcloud config set compute/region southamerica-east1-a
    gcloud container clusters create [NOME DO CLUSTER] --region southamerica-east1-a 

    gcloud config set project [ID DO PROJETO]

Converter docker-compose em kubernets:

    kompose convert -f docker-compose.yml -o pods.yml

Aplicar os serviços no servidor:

    kubectl apply -f result.yml

    kubectl get service

Expor um serviços na internet

    kubectl expose deployment [NOME DO SERVIÇO] --type=LoadBalancer --port 80 --target-port 80 --name=[NOME DO SERVIÇO PARA INTERNET]

Excluir um serviço

    kubectl delete service [NOME DO SERVIÇO]
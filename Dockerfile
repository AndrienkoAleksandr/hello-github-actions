FROM registry.access.redhat.com/ubi8/openjdk-11@sha256:b9481cc1bfb3b8c332b5d6f8fe51fc2a6ba997ea0dc0c3b293029a0f9297cca4

WORKDIR /home/jboss
USER root

ADD entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh; mkdir -p github-action
COPY . github-action

RUN cd github-action; mvn clean install

ENTRYPOINT ["/entrypoint.sh"]

FROM epamedp/edp-jenkins-base-agent:1.0.26 as base

ARG KUBECTL_VERSION="1.21.6"
ARG HELM_VERSION="3.6.3"
ARG HADOLINT_VERSION="2.10.0"
ARG HELM_PUSH_PLUGIN="0.10.3"

USER root
RUN rm -rf /usr/local/bin/kubectl
USER jenkins
WORKDIR /tmp
RUN curl -LOf "https://dl.k8s.io/release/v${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
    && chmod +x ./kubectl \
    && mv ./kubectl /usr/local/bin/kubectl

RUN curl -LOf https://get.helm.sh/helm-v${HELM_VERSION}-linux-amd64.tar.gz | tar -xzf \
    && chmod +x linux-amd64/helm \
    && mv linux-amd64/helm /usr/local/bin/helm \
    && ln -s -linux-amd64/helm /usr/local/bin/helm3

RUN helm plugin install https://github.com/chartmuseum/helm-push

RUN curl -LOf https://github.com/hadolint/hadolint/releases/download/v${HADOLINT_VERSION}/hadolint-Linux-x86_64 \
    && mv ./hadolint /bin/hadolint

RUN git clone https://github.com/tfutils/tfenv.git ~/.tfenv \
    && echo 'export PATH="$HOME/.tfenv/bin:$PATH"' >> ~/.bash_profile \
    && ln -s ~/.tfenv/bin/* /usr/local/bin



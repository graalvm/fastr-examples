# Based on https://github.com/graalvm/fastr#building-fastr-from-source
FROM openjdk:9-jdk

RUN sed -i "s/deb.debian.org/cdn-fastly.deb.debian.org/" /etc/apt/sources.list \
    && sed -i "s/security.debian.org/cdn-fastly.debian.org\/debian-security/" /etc/apt/sources.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential \
        gfortran \
        libpcre3-dev \
        libreadline-dev \
        zlib1g-dev \
        libbz2-dev \
        liblzma-dev \
        libcurl4-openssl-dev \
        ed \
        libmpc-dev \
        libssl-dev \
    && ldconfig

WORKDIR /jdk
RUN wget https://github.com/graalvm/openjdk8-jvmci-builder/releases/download/jvmci-19.2-b02/openjdk-8u222-jvmci-19.2-b02-linux-amd64.tar.gz \
	&& tar -xzf openjdk-8u222-jvmci-19.2-b02-linux-amd64.tar.gz

ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/lib/x86_64-linux-gnu/

ENV FASTR_HOME=/fastr
ENV MX_HOME=/usr/mx
ENV PATH=$MX_HOME:/graal/sulong/mxbuild/SULONG_LLVM_ORG/bin:$PATH
ENV JAVA_HOME=/jdk/openjdk1.8.0_222-jvmci-19.2-b02

# See https://github.com/oracle/graal/blob/master/compiler/src/org.graalvm.compiler.hotspot/src/org/graalvm/compiler/hotspot/JVMCIVersionCheck.java
#ENV JVMCI_VERSION_CHECK=ignore

WORKDIR $FASTR_HOME

# based on https://github.com/oracle/fastr#building-fastr-from-source
RUN git clone --depth 1 -b master https://github.com/graalvm/mx.git $MX_HOME
RUN git clone --depth 1 https://github.com/oracle/fastr $FASTR_HOME
RUN mx sforceimports
WORKDIR /graal/sulong
RUN mx build --projects SULONG_LLVM_ORG
RUN mx build
WORKDIR $FASTR_HOME
RUN mx build
    
# https://github.com/oracle/fastr/issues/11#issuecomment-389490445
WORKDIR /graal/compiler
RUN mx build

WORKDIR /graal/tools
RUN mx build

ARG VCS_URL
ARG VCS_REF
ARG BUILD_DATE
ARG GNUR_VERSION=3.5.1
LABEL org.label-schema.license="https://raw.githubusercontent.com/graalvm/fastr/master/LICENSE" \
    org.label-schema.vendor="FastR project team" \
	org.label-schema.name="FastR" \
	org.label-schema.description="FastR is an implementation of the R Language in Java atop Truffle, a framework for building self-optimizing AST interpreters." \ 
    org.label-schema.vcs-url=$VCS_URL \
    org.label-schema.vcs-ref=$VCS_REF \
    org.label-schema.build-date=$BUILD_DATE \
    org.fastr.version="devel" \
    org.gnur.version=$GNUR_VERSION \
    org.label-schema.schema-version="rc1" \
	maintainer="Zbyněk Šlajchrt <zbynek.slajchrt@oracle.com>"

WORKDIR $FASTR_HOME
ENV LANG=en_US.UTF-8
ENV LD_LIBRARY_PATH=/graal/sulong/mxbuild/SULONG_LLVM_ORG/lib
ENV PATH=$FASTR_HOME/bin:$PATH
ENV DEFAULT_DYNAMIC_IMPORTS=graal/compiler,graal/tools

RUN mv etc/Makevars.site.debug etc/Makevars.site

WORKDIR $FASTR_HOME/rpkgs
RUN wget https://cran.r-project.org/src/contrib/Archive/Rcpp/Rcpp_1.0.0.tar.gz \
	&& tar -xzf Rcpp_1.0.0.tar.gz
RUN R CMD INSTALL Rcpp

COPY . /fastr_llvm_debug_demo

ENV MX_R_GLOBAL_ARGS="--cp-sfx /graal/tools/mxbuild/dists/jdk1.8/truffle-profiler.jar:/graal/tools/mxbuild/dists/jdk1.8/chromeinspector.jar"

EXPOSE 9229

WORKDIR /

# ENTRYPOINT [ "mx", "--dynamicimports", "graal/compiler", "R" ]
# enable compilation logs with mx --dynamicimport graal/compiler --J "@-Dgraal.TraceTruffleCompilation=true" R

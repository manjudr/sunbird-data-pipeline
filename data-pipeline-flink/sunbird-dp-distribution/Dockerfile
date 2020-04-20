FROM anandp504/flink:1.10.0-scala_2.12
USER flink
WORKDIR /opt/sunbird-dp-distribution/

COPY target/sunbird-dp-distribution-1.0.tar.gz /tmp
RUN tar -xvf /tmp/sunbird-dp-distribution-1.0.tar.gz -C $FLINK_HOME/lib/
MAINTAINER "Manjunath Davanam" "manjunathd@Ilimi.in"
RUN mkdir -p /home/sunbird/druid-proxy-api
WORKDIR /home/sunbird/sunbird-dp-distribution
COPY --from=0 /opt/sunbird-dp-distribution/ /home/sunbird/sunbird-dp-distribution
WORKDIR /home/sunbird/unbird-dp-distribution/

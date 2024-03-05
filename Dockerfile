FROM registry.access.redhat.com/ubi9/openjdk-17:1.18

COPY target/*.jar /opt/app.jar
USER 1001
EXPOSE 8080
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED", \
            "--add-opens","java.base/java.util=ALL-UNNAMED", \
            "--add-opens","java.base/java.lang.reflect=ALL-UNNAMED", \
            "--add-opens","java.desktop/java.awt.font=ALL-UNNAMED",\
            "--add-opens","java.base/java.text=ALL-UNNAMED",\
            "-jar","/opt/app.jar"]

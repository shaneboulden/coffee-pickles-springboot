package com.rock.coffeepickles.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.export.MBeanExporter;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rock.coffeepickles.web",
        "com.rock.coffeepickles.service"})
@EntityScan("com.rock.coffeepickles.domain")
@EnableJpaRepositories("com.rock.coffeepickles.repository")
public class CoffeepicklesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeepicklesApplication.class, args);
	}

    @Bean
    public MetricsEndpointMetricReader metricsEndpointMetricReader(MetricsEndpoint metricsEndpoint) {
        return new MetricsEndpointMetricReader(metricsEndpoint);
    }

    @Bean
    @ExportMetricWriter
    public MetricWriter metricWriter (MBeanExporter exporter) {
        return new JmxMetricWriter(exporter);
    }
}

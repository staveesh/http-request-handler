package com.taveeshsharma.requesthandler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class InitialDataConfiguration {

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() {
        Resource rolesData = new ClassPathResource("roles.json");
        Resource ciphersData = new ClassPathResource("ciphers.json");
        Resource filtersData = new ClassPathResource("filters.json");
        Resource vpnData = new ClassPathResource("vpn.json");
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{rolesData, ciphersData, filtersData, vpnData});
        return factory;
    }
}

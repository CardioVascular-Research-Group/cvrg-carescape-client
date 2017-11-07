package edu.jhu.cvrg.carescape;

/*
Copyright 2017 Johns Hopkins University Institute for Computational Medicine

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
* @author Stephen Granite
* 
*/

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ComponentScan(basePackages = "edu.jhu.cvrg.carescape")
public class ApplicationConfigs {

    @Bean
    public String version() {
        return "v1.0, updated 6/28/2017";
    }

    @Bean
    public String dateFormat() {
        return "yyyy-MM-dd HH:mm";
    }

    @Bean
    public org.apache.commons.configuration2.Configuration configurationFile() throws ConfigurationException {
        Configurations configurations = new Configurations();
        return configurations.ini(new File("carescape.conf"));
    }

    @Bean
    public String carescapeIPAddress(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("carescape-ip-address");
    }

    @Bean
    public String mulesoftAddress(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("mulesoft-address");
    }

    @Bean
    public String mulesoftClientId(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("mulesoft-clientid");
    }
    
    @Bean
    public String mulesoftClientSecret(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("mulesoft-clientsecret");
    }

    @Bean
    public String openTSDBAddress(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("opentsdb-address");
    }

    @Bean
    public String roomToCapture(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("room-to-capture");
    }

    @Bean
    public String rootDirectory(org.apache.commons.configuration2.Configuration configurationFile) { // Chaining dependency.
        return configurationFile.getString("root-directory");
    }

}

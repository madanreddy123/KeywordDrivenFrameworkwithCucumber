package com.base;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

        public Properties prop;
        public Properties init_prop() {

            prop = new Properties();
            try {
                FileInputStream ip = new FileInputStream("./src/main/resources/Properties/Config.properties");
                prop.load(ip);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return prop;

        }

    }

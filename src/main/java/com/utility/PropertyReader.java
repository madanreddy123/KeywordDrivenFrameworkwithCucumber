package com.utility;
import java.io.FileInputStream;
import java.util.Properties;
public class PropertyReader {

    private static Properties prop = new Properties();

    public static Properties init_prop() {
        prop = new Properties();
        try {
            FileInputStream ip = new FileInputStream("./src/main/resources/Properties/Config.properties");
            prop.load(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static String getFieldValue(String fieldName) {
        init_prop();
        String fieldValue = prop.getProperty(fieldName);
        System.out.println("Received parameter '" + fieldName + "' = " + fieldValue);

        if (fieldValue == null) {
            System.out.println("INCORRECT PARAMETER '" + fieldName + "' WAS SUPPLIED FOR RETRIEVAL FROM CONFIG");
            return "false";
        } else {
            return fieldValue;
        }
    }

    public static void setFieldValue(String fieldName, String fieldValue) {
        prop.setProperty(fieldName, fieldValue);
        System.out.println("Set parameter '" + fieldName + "' = " + fieldValue);
    }
}

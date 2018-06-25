# annotation-processor
Simple example of Java annotation processing.

In this example, the **annotation-lib** provides an annotation that creates a Bean of a configuration interface from Owner (http://owner.aeonbits.org/).

The example application **annotation-example** is a quick and simple SpringBoot application that retrieves the configuration from a property file. By annotating the interface for the configuration, it's processed during compilation and a Bean is created with the instance of the configuration.

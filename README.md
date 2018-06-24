# annotation-processor
Simple example of Java annotation processing.

In this example, the **annotation-lib** provides an annotation that creates a Bean of a configuration interface from Owner (http://owner.aeonbits.org/).

The example application **annotation-example** is a quick and simple SpringBoot application that retrieves the configuration from a property file. By annotation the interface of the configuration, during the compilation the annotation processor creates the Bean of the produced configuration.
